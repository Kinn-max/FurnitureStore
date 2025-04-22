package com.example.furniturestore.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturestore.common.status.LoadStatus
import com.example.furniturestore.config.TokenManager
import com.example.furniturestore.model.ProductVariant
import com.example.furniturestore.model.ProductWithCategory
import com.example.furniturestore.repositories.MainLog
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class HomeUiState(
    val status: LoadStatus = LoadStatus.Innit(),
    val hi: String = "",
    val productJustForYou: List<ProductWithCategory> = emptyList(),
    val productDeal: List<ProductWithCategory> = emptyList(),
    val name: String = "",
    val photo: String = "",
    val selectedProduct: ProductWithCategory? = null,
    val productVariants: List<ProductVariant> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val log: MainLog?,
    private val tokenManager: TokenManager,
    //
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeApp()
        loadRandomProducts(5, isDeal = false)
        loadRandomProducts(2, isDeal = true)
    }

    private fun loadHomeApp() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val name = tokenManager.getName() ?: ""
                val photo = tokenManager.getPhoto() ?: ""
                _uiState.value = _uiState.value.copy(
                    name = name,
                    photo = photo,
                    status = LoadStatus.Success()
                )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(status = LoadStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun loadRandomProducts(count: Int, isDeal: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val categoryResult = db.collection("category").get().await()
                val categoryMap =
                    categoryResult.associateBy({ it.id }, { it.getString("name") ?: "" })

                // Step 1: Fetch all products
                val productResult = db.collection("product").get().await()
                var productList = productResult.map { document ->
                    val productId = document.id.toIntOrNull()
                    val categoryId = document.getString("category_id") ?: ""
                    val categoryName = categoryMap[categoryId] ?: ""

                    ProductWithCategory(
                        id = productId,
                        name = document.getString("name"),
                        price = document.getDouble("price"),
                        isVariant = document.getBoolean("isVariant") ?: false,
                        description = document.getString("description"),
                        image = document.getString("image"),
                        color = document.getString("color"),
                        category = categoryName, // thêm tên category vào đây
                        isFavorite = false
                    )
                }

                // Step 2: Get userId from tokenManager
                val userId = tokenManager.getUserUid()
                Log.e("userId", "User ID: $userId")

                // Step 3: Update product list with favorite status if user is logged in
                val updatedProductList = if (userId != null && userId.isNotEmpty()) {
                    // Fetch the user's favorite products
                    Log.e("FavoriteQuery", "Querying favorites for user_id: $userId")
                    val favoriteResult = db.collection("favorite")
                        .whereEqualTo("user_id", userId)
                        .get()
                        .await()

                    // Log the raw result
                    Log.e("favoriteResult", "Favorite Result size: ${favoriteResult.documents.size}")
                    favoriteResult.documents.forEach { doc ->
                        Log.e("favoriteDoc", "Doc: ${doc.id}, Data: ${doc.data}")
                    }

                    // Extract product_ids, handling both String and Long types
                    val favoriteProductIds = favoriteResult.mapNotNull { document ->
                        try {
                            val rawProductId = document.get("product_id")
                            val productId = when (rawProductId) {
                                is Long -> rawProductId.toInt()
                                is String -> rawProductId.toIntOrNull()
                                else -> null
                            }
                            Log.e("favoriteProductId", "Extracted product_id: $productId from doc: ${document.id}, Raw: $rawProductId")
                            productId
                        } catch (e: Exception) {
                            Log.e("FavoriteError", "Invalid product_id format: ${document.get("product_id")}", e)
                            null
                        }
                    }

                    Log.e("favoriteProductIds", "Favorite Product IDs: $favoriteProductIds")

                    // Update the product list with favorite status
                    productList.map { product ->
                        val isFavorite = product.id != null && product.id in favoriteProductIds
                        if (product.isVariant == true) {
                            fetchVariantPrice(product).copy(isFavorite = isFavorite)
                        } else {
                            product.copy(isFavorite = isFavorite)
                        }
                    }
                } else {
                    Log.e("userId", "No user ID, skipping favorite check")
                    productList.map { product ->
                        if (product.isVariant == true) {
                            fetchVariantPrice(product)
                        } else {
                            product
                        }
                    }
                }

                val randomList = updatedProductList.shuffled().take(count)

                _uiState.value = if (isDeal) {
                    _uiState.value.copy(productDeal = randomList, status = LoadStatus.Success())
                } else {
                    _uiState.value.copy(
                        productJustForYou = randomList,
                        status = LoadStatus.Success()
                    )
                }
            } catch (e: Exception) {
                Log.e("LoadRandomProductsError", "Error: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error(e.message ?: "Lỗi tải dữ liệu")
                )
            }
        }
    }

    private suspend fun fetchVariantPrice(product: ProductWithCategory): ProductWithCategory {
        return try {
            var result =
                db.collection("product_variant").whereEqualTo("product_id", product.id.toString())
                    .limit(1)
                    .get()
                    .await()
            if (!result.isEmpty) {
                val variant = result.documents[0].toObject(ProductVariant::class.java)
                product.copy(
                    price = variant?.price,
                )
            } else {
                product
            }
        } catch (e: Exception) {
            log?.e(
                "Error fetching variant price: ${e.message}",
                msg = TODO()
            )
            product // Trả về product gốc nếu có lỗi
        }
    }

    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val categoryResult = db.collection("category").get().await()
                val categoryMap =
                    categoryResult.associateBy({ it.id }, { it.getString("name") ?: "" })
                val productDoc = db.collection("product").document(productId).get().await()

                val product = productDoc.let { doc ->
                    val isVariant = doc.getBoolean("isVariant") ?: false
                    val categoryName = categoryMap[doc.getString("category_id")] ?: ""

                    ProductWithCategory(
                        id = doc.id.toIntOrNull(),
                        name = doc.getString("name"),
                        price = doc.getDouble("price"),
                        isVariant = isVariant,
                        description = doc.getString("description"),
                        image = doc.getString("image"),
                        color = doc.getString("color"),
                        category = categoryName, // thêm tên category vào đây
                        isFavorite = false
                    )
                }

                val updatedProduct = if (product.isVariant == true) {
                    fetchVariantPrice(product)
                } else {
                    product
                }

                val variantList = if (product.isVariant == true) {
                    val variantResult = db.collection("product_variant")
                        .whereEqualTo("product_id", productId)
                        .get()
                        .await()
                    variantResult.map { it.toObject(ProductVariant::class.java) }
                } else {
                    emptyList()
                }

                _uiState.value = _uiState.value.copy(
                    selectedProduct = updatedProduct,
                    productVariants = variantList,
                    status = LoadStatus.Success()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error(e.message ?: "Lỗi tải chi tiết sản phẩm")
                )
            }
        }
    }
    fun toggleFavorite(product: ProductWithCategory) {
        viewModelScope.launch {
            val userId = tokenManager.getUserUid()
            if (userId != null && product.id != null) {
                val currentState = _uiState.value
                val isCurrentlyFavorite = product.isFavorite ?: false

                if (isCurrentlyFavorite) {

                    try {
                        val favoriteDocs = db.collection("favorite")
                            .whereEqualTo("user_id", userId)
                            .whereEqualTo("product_id", product.id.toString())
                            .get()
                            .await()
                        favoriteDocs.forEach { doc ->
                            doc.reference.delete().await()
                        }
                        Log.e("Favorite", "Removed product ${product.id} from favorites")
                    } catch (e: Exception) {
                        Log.e("FavoriteError", "Error removing favorite: ${e.message}", e)
                        return@launch
                    }
                } else {

                    try {
                        db.collection("favorite").add(
                            hashMapOf(
                                "user_id" to userId,
                                "product_id" to product.id.toString()
                            )
                        ).await()
                        Log.e("Favorite", "Added product ${product.id} to favorites")
                    } catch (e: Exception) {
                        Log.e("FavoriteError", "Error adding favorite: ${e.message}", e)
                        return@launch
                    }
                }

                val updatedJustForYou = currentState.productJustForYou.map {
                    if (it.id == product.id) it.copy(isFavorite = !isCurrentlyFavorite) else it
                }
                val updatedDeals = currentState.productDeal.map {
                    if (it.id == product.id) it.copy(isFavorite = !isCurrentlyFavorite) else it
                }

                _uiState.value = currentState.copy(
                    productJustForYou = updatedJustForYou,
                    productDeal = updatedDeals
                )
            } else {
                Log.e("FavoriteError", "User ID or Product ID is null")
                _uiState.value = _uiState.value.copy(status = LoadStatus.Error("Chưa đăng nhập!"))
            }
        }
    }
}