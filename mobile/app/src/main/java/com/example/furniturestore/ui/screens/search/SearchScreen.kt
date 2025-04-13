package com.example.furniturestore.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.model.Product
import com.example.furniturestore.model.ProductWithCategory
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.setSearchQuery(it) },
            onSearch = {
                viewModel.searchProducts(it)
                focusManager.clearFocus()
            },
            onClear = {
                viewModel.clearSearch()
            }
        )

        Divider()

        // Content based on search state
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.status is LoadStatus.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.status is LoadStatus.Error -> {
                    Text(
                        text = (uiState.status.toString()),
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                uiState.isSearchActive && uiState.searchResults.isNotEmpty() -> {
                    SearchResultsContent(
                        query = uiState.searchQuery,
                        results = uiState.searchResults,
                        totalResults = uiState.totalResults,
                        onProductClick = {
                            productId -> navController.navigate("product-detail/$productId")
                        },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) }
                    )
                }

                uiState.isSearchActive && uiState.searchResults.isEmpty() && uiState.status is LoadStatus.Success -> {
                    Text(
                        text = "Không tìm thấy kết quả cho \"${uiState.searchQuery}\"",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                !uiState.isSearchActive && uiState.recentSearches.isNotEmpty() -> {
                    RecentSearches(
                        searches = uiState.recentSearches,
                        onSearchClick = {
                            viewModel.setSearchQuery(it)
                            viewModel.searchProducts(it)
                        },
                        onClearAll = { viewModel.clearRecentSearches() }
                    )
                }

                else -> {
                    // Empty state - nothing to show yet
                    Text(
                        text = "Tìm kiếm sản phẩm",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search icon
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            tint = Color.Gray
        )

        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text("Tìm kiếm theo từ khóa hoặc danh mục") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(query)
                    focusManager.clearFocus()
                }
            )
        )

        // Only show clear button if there's text
        if (query.isNotEmpty()) {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search"
                )
            }
        }
    }
}

@Composable
fun SearchResultsContent(
    query: String,
    results: List<ProductWithCategory>,
    totalResults: Int,
    onProductClick: (String) -> Unit,
    onFavoriteToggle: (ProductWithCategory) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Results count and filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$totalResults Kết quả cho \"$query\"",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Button(
                onClick = { /* Handle filter click */ },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Lọc")
            }
        }

        // Product list
        LazyColumn {
            items(results) { product ->
                ProductListItem(
                    product = product,
                    onProductClick = { onProductClick(product.id.toString()) },
                    onFavoriteToggle = { onFavoriteToggle(product) }
                )
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: ProductWithCategory,
    onProductClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Product Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.name ?: "Unknown",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = product.category ?: "Unknown", // You might want to customize this based on your data model
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "4.25", // You might want to get this from product data
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            val formattedPrice = NumberFormat.getCurrencyInstance(Locale.US).format(product.price)
            Text(
                text = formattedPrice ?: "0",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Favorite Button
        IconButton(onClick = onFavoriteToggle) {
            Icon(
                imageVector = if (product.isFavorite == true)
                    Icons.Default.Favorite
                else
                    Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (product.isFavorite == true) Color.Red else Color.Gray
            )
        }
    }
}

@Composable
fun RecentSearches(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tìm kiếm gần đây",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = "Xóa tất cả",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onClearAll)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searches) { search ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onSearchClick(search) })
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(text = search, fontSize = 16.sp)
                }

                Divider()
            }
        }
    }
}