package com.example.furniturestore.ui.screens.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.config.TokenManager
import com.example.furniturestore.model.ProductWithCategory
import com.example.furniturestore.repositories.MainLog
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

data class FavoriteUiState(
    val status: LoadStatus = LoadStatus.Innit(),
    val searchResults: List<ProductWithCategory> = emptyList(),
    val retryAction: (() -> Unit)? = null
)
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val log: MainLog?,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAllFavorite()
    }

    fun loadAllFavorite() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            Log.d("FavoriteViewModel", "Starting loadAllFavorite")
            try {
                val uid = tokenManager.getUserUid()
                Log.d("FavoriteViewModel", "User UID: $uid")
                if (uid == null || uid.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        status = LoadStatus.Error("User ID is missing"),
                        retryAction = { loadAllFavorite() }
                    )
                    Log.d("FavoriteViewModel", "User ID missing, set error state")
                    return@launch
                }

                // Step 1: Fetch favorite product IDs with timeout
                Log.d("FavoriteViewModel", "Fetching favorite docs for user $uid")
                val favoriteDocs = withTimeoutOrNull(10_000L) {
                    db.collection("favorite")
                        .whereEqualTo("user_id", uid)
                        .get()
                        .await()
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        status = LoadStatus.Error("Timeout fetching favorite products"),
                        retryAction = { loadAllFavorite() }
                    )
                    Log.d("FavoriteViewModel", "Timeout fetching favorite docs")
                    return@launch
                }

                val productIds = favoriteDocs.mapNotNull { it.getString("product_id") }
                Log.d("productId", productIds.toString())

                if (productIds.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        searchResults = emptyList(),
                        status = LoadStatus.Success()
                    )
                    Log.d("FavoriteViewModel", "No favorite products found")
                    return@launch
                }

                // Step 2: Fetch categories and create a map
                Log.d("FavoriteViewModel", "Fetching categories")
                val categoryResult = withTimeoutOrNull(10_000L) {
                    db.collection("category").get().await()
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        status = LoadStatus.Error("Timeout fetching categories"),
                        retryAction = { loadAllFavorite() }
                    )
                    Log.d("FavoriteViewModel", "Timeout fetching categories")
                    return@launch
                }

                val categoryMap = categoryResult.associateBy(
                    { it.id },
                    { it.getString("name") ?: "" }
                )
                Log.d("categoryMap", categoryMap.toString())

                // Step 3: Fetch products by their document IDs
                val productList = mutableListOf<ProductWithCategory>()
                for (productId in productIds) {
                    try {
                        Log.d("FavoriteViewModel", "Fetching product $productId")
                        val document = withTimeoutOrNull(10_000L) {
                            db.collection("product")
                                .document(productId)
                                .get()
                                .await()
                        } ?: continue

                        if (document.exists()) {
                            val product = try {
                                val categoryId = document.getString("category_id") ?: ""
                                val categoryName = categoryMap[categoryId] ?: ""

                                val name = document.getString("name") ?: continue
                                val price = document.getDouble("price") ?: continue

                                ProductWithCategory(
                                    id = productId.toIntOrNull(),
                                    name = name,
                                    price = price,
                                    isVariant = document.getBoolean("isVariant") ?: false,
                                    description = document.getString("description"),
                                    image = document.getString("image"),
                                    color = document.getString("color"),
                                    category = categoryName,
                                    isFavorite = true
                                )
                            } catch (e: Exception) {
                                log?.e("Error parsing product $productId: ${e.message}", msg = TODO())
                                null
                            }
                            if (product != null) {
                                productList.add(product)
                            }
                        } else {
                            Log.d("FavoriteViewModel", "Product $productId does not exist")
                        }
                    } catch (e: Exception) {
                        log?.e("Error fetching product $productId: ${e.message}", msg = TODO())
                    }
                }

                Log.d("productList", productList.toString())
                _uiState.value = _uiState.value.copy(
                    searchResults = productList,
                    status = LoadStatus.Success()
                )
                Log.d("FavoriteViewModel", "Completed loading favorites")
            } catch (e: Exception) {
                log?.e("Unexpected error: ${e.message}", msg = TODO())
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error(e.message ?: "Unknown error"),
                    retryAction = { loadAllFavorite() }
                )
                Log.d("FavoriteViewModel", "Error state set: ${e.message}")
            }
        }
    }

    fun toggleFavorite(product: ProductWithCategory) {
        viewModelScope.launch {
            val userId = tokenManager.getUserUid()
            if (userId == null || product.id == null) {
                Log.e("FavoriteError", "User ID or Product ID is null")
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error("Đụ mạ chưa đăng nhập!")
                )
                return@launch
            }

            val isCurrentlyFavorite = product.isFavorite ?: false

            try {
                if (isCurrentlyFavorite) {
                    // Remove from favorites
                    val favoriteDocs = db.collection("favorite")
                        .whereEqualTo("user_id", userId)
                        .whereEqualTo("product_id", product.id.toString())
                        .get()
                        .await()
                    favoriteDocs.forEach { doc ->
                        doc.reference.delete().await()
                    }
                    Log.d("Favorite", "Removed product ${product.id} from favorites")
                } else {
                    // Add to favorites
                    val favoriteData = hashMapOf(
                        "user_id" to userId,
                        "product_id" to product.id.toString()
                    )
                    db.collection("favorite")
                        .add(favoriteData)
                        .await()
                    Log.d("Favorite", "Added product ${product.id} to favorites")
                }

                // Reload the favorites list to update the UI
                loadAllFavorite()
            } catch (e: Exception) {
                Log.e("FavoriteError", "Error toggling favorite: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error("Error toggling favorite: ${e.message}")
                )
            }
        }
    }
}