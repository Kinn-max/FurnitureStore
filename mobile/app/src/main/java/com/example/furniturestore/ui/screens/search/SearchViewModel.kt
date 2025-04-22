package com.example.furniturestore.ui.screens.search

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

data class SearchUiState(
    val status: LoadStatus = LoadStatus.Innit(),
    val searchQuery: String = "",
    val searchResults: List<ProductWithCategory> = emptyList(),
    val isSearchActive: Boolean = false,
    val recentSearches: List<String> = emptyList(),
    val totalResults: Int = 0
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val log: MainLog?,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    // Set search query
    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    // Activate or deactivate search
    fun setSearchActive(active: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchActive = active)
        if (!active) {
            // Clear results when search is deactivated
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), totalResults = 0)
        }
    }

    // Save a search query to recent searches
    fun addToRecentSearches(query: String) {
        if (query.isBlank()) return

        val currentSearches = _uiState.value.recentSearches.toMutableList()
        // Remove if exists already to avoid duplicates
        currentSearches.remove(query)
        // Add to beginning of list
        currentSearches.add(0, query)
        // Keep only the most recent 10 searches
        val trimmedSearches = currentSearches.take(10)

        _uiState.value = _uiState.value.copy(recentSearches = trimmedSearches)
    }

    // Clear all recent searches
    fun clearRecentSearches() {
        _uiState.value = _uiState.value.copy(recentSearches = emptyList())
    }

    // Main search function
    fun searchProducts(query: String) {
        if (query.isBlank()) {
            setSearchActive(false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                status = LoadStatus.Loading(),
                isSearchActive = true
            )

            try {
                // Add to recent searches
                addToRecentSearches(query)

                // Normalize query for case-insensitive search
                val normalizedQuery = query.lowercase().trim()

                val categoryResult = db.collection("category").get().await()
                val categoryMap =
                    categoryResult.associateBy({ it.id }, { it.getString("name") ?: "" })


                // Get all products
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

                // Filter products by query (case-insensitive)
                productList = productList.filter { product ->
                    product.name?.lowercase()?.contains(normalizedQuery) == true ||
                            product.description?.lowercase()?.contains(normalizedQuery) == true
                }

                // Update products with variant prices and favorite status
                val userId = tokenManager.getUserUid()
                val updatedProductList = if (userId != null && userId.isNotEmpty()) {
                    // Get user favorites
                    val favoriteResult = db.collection("favorite")
                        .whereEqualTo("user_id", userId)
                        .get()
                        .await()

                    val favoriteProductIds = favoriteResult.mapNotNull { document ->
                        document.get("product_id")?.toString()?.toIntOrNull()
                    }

                    // Update products with favorite status and variant prices
                    productList.map { product ->
                        val isFavorite = product.id != null && product.id in favoriteProductIds
                        if (product.isVariant == true) {
                            fetchVariantPriceWithCategory(product).copy(isFavorite = isFavorite)
                        } else {
                            product.copy(isFavorite = isFavorite)
                        }
                    }
                } else {
                    // Just update variant prices if user not logged in
                    productList.map { product ->
                        if (product.isVariant == true) {
                            fetchVariantPriceWithCategory(product)
                        } else {
                            product
                        }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    searchResults = updatedProductList,
                    totalResults = updatedProductList.size,
                    status = LoadStatus.Success()
                )

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Search error: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error(e.message ?: "Lỗi tìm kiếm sản phẩm")
                )
            }
        }
    }

    private suspend fun fetchVariantPriceWithCategory(product: ProductWithCategory): ProductWithCategory {
        return try {
            val result = db.collection("product_variant")
                .whereEqualTo("product_id", product.id.toString())
                .limit(1)
                .get()
                .await()

            if (!result.isEmpty) {
                val variant = result.documents[0].toObject(ProductVariant::class.java)
                product.copy(price = variant?.price)
            } else {
                product
            }
        } catch (e: Exception) {
            log?.e("Error fetching variant price: ${e.message}", msg = TODO())
            product // Return original product if error
        }
    }

    // Toggle favorite status for a product
    fun toggleFavorite(product: ProductWithCategory) {
        viewModelScope.launch {
            val userId = tokenManager.getUserUid()
            if (userId != null && product.id != null) {
                val currentState = _uiState.value
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
                    } else {
                        // Add to favorites
                        db.collection("favorite").add(
                            hashMapOf(
                                "user_id" to userId,
                                "product_id" to product.id.toString()
                            )
                        ).await()
                    }

                    // Update search results with new favorite status
                    val updatedResults = currentState.searchResults.map {
                        if (it.id == product.id) it.copy(isFavorite = !isCurrentlyFavorite) else it
                    }

                    _uiState.value = currentState.copy(searchResults = updatedResults)

                } catch (e: Exception) {
                    Log.e("SearchViewModel", "Error toggling favorite: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(
                        status = LoadStatus.Error("Lỗi cập nhật yêu thích")
                    )
                }
            } else {
                Log.e("SearchViewModel", "User ID or Product ID is null")
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error("Vui lòng đăng nhập để thêm vào yêu thích")
                )
            }
        }
    }

    // Clear search results
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            isSearchActive = false,
            totalResults = 0
        )
    }
}