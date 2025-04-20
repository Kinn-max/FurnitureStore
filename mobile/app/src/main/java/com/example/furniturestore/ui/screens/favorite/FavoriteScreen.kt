package com.example.furniturestore.ui.screens.favorite

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.furniturestore.R
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.model.ProductWithCategory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
    navController: NavController
) {
    val userProfile = "hihi"
    val customFont = FontFamily(Font(R.font.lora))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My favorite",
                        fontFamily = customFont,
                        color = Color(0xFF3A3A3A),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.White),
            )
        }
    ) { padding ->
        when (uiState.status) {
            is LoadStatus.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LoadStatus.Error -> {
                LaunchedEffect(uiState.status) {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            }
            is LoadStatus.Success -> {
                if (userProfile == null) {
                    LaunchedEffect(uiState.status) {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                } else {
                    if (uiState.searchResults.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .background(Color(0xFFFFFFFF)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Bạn chưa có sản phẩm yêu thích nào.",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .background(Color(0xFFFFFFFF))
                        ) {
                            items(uiState.searchResults) { product ->
                                ProductItem(product = product,navController,viewModel)
                                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ProductItem(
    product: ProductWithCategory,
    navController: NavController,
    viewModel: FavoriteViewModel
) {
    val customFont = FontFamily(Font(R.font.lora))
    val customInter = FontFamily(Font(R.font.inter))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(
                onClick = {
                    product.id?.let { id ->
                        navController.navigate("product-detail/$id")
                    }
                },
                onClickLabel = "View product ${product.name ?: "details"}"
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.anh2),
                error = painterResource(R.drawable.anh2)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Product Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.name ?: "Unknown",
                fontFamily = customFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = product.category ?: "Unknown",
                fontFamily = customInter,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "4.25", // Replace with actual rating if available
                    fontFamily = customInter,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            val formattedPrice = NumberFormat.getCurrencyInstance(Locale.US).format(product.price ?: 0.0)
            Text(
                text = formattedPrice,
                fontFamily = customFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Favorite Button
        Icon(
            imageVector = if (product.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (product.isFavorite == true) Color.Red else Color.Gray,
            modifier = Modifier
                .clickable {
                    viewModel.toggleFavorite(product)
                }
        )
    }
}