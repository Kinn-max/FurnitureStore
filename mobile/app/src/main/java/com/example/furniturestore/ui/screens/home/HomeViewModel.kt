package com.example.furniturestore.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.config.TokenManager
import com.example.furniturestore.model.Product
import com.example.furniturestore.model.ProductVariant
import com.example.furniturestore.repositories.MainLog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class HomeUiState(
    val status: LoadStatus = LoadStatus.Innit(),
    val hi: String = "",
    val productJustForYou: List<Product> = emptyList(),
    val productDeal: List<Product> = emptyList(),
    val name: String = "",
    val selectedProduct: Product? = null,
    val productVariants: List<ProductVariant> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val log: MainLog?,
    private val tokenManager: TokenManager
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
                _uiState.value = _uiState.value.copy(
                    name = name,
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
                val result = db.collection("product").get().await()
                val productList = result.map { document ->
                    val isVariant = document.getBoolean("isVariant") ?: false
                    Product(
                        id = document.id.toIntOrNull(),
                        name = document.getString("name"),
                        price = document.getDouble("price"),
                        isVariant = isVariant,
                        description = document.getString("description"),
                        image = document.getString("image"),
                        category_id = document.getString("category_id")
                    )
//                    val product = document.toObject(Product::class.java)
//                    product.copy(id = document.id.toIntOrNull())
                }

                val updateProductList = productList.map { product ->
                    if (product.isVariant == true) {
                        fetchVariantPrice(product)
                    } else {
                        product
                    }
                }

                val randomList = updateProductList.shuffled().take(count)

                _uiState.value = if (isDeal) {
                    _uiState.value.copy(productDeal = randomList, status = LoadStatus.Success())
                } else {
                    _uiState.value.copy(
                        productJustForYou = randomList,
                        status = LoadStatus.Success()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error(e.message ?: "Lỗi tải dữ liệu")
                )
            }
        }
    }

    private suspend fun fetchVariantPrice(product: Product): Product {
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
                val productDoc = db.collection("product").document(productId).get().await()
                val product = productDoc.let { doc ->
                    val isVariant = doc.getBoolean("isVariant") ?: false
                    Product(
                        id = doc.id.toIntOrNull(),
                        name = doc.getString("name"),
                        price = doc.getDouble("price"),
                        isVariant = isVariant,
                        description = doc.getString("description"),
                        image = doc.getString("image"),
                        category_id = doc.getString("category_id")
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
}