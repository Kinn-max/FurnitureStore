package com.example.furniturestore.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.furniturestore.R
import com.example.furniturestore.common.status.LoadStatus
import com.example.furniturestore.ui.screens.cart.CartViewModel
import com.example.furniturestore.ui.screens.home.HomeViewModel

@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val product = uiState.selectedProduct
    val variants = uiState.productVariants
    val context = LocalContext.current
    var selectOptionIndex = remember { mutableStateOf(0) }

    // Gọi loadProductDetail khi vào màn hình
    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(56.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Trở về",
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .size(28.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Yêu thích",
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable {
                                if (uiState.name == "") {
                                    Toast.makeText(
                                        context,
                                        "Please log in!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                } else {
                                    if (product != null) {
                                        viewModel.toggleFavorite(product)
                                    }
                                }
                            }
                            .size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (uiState.status) {
            is LoadStatus.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is LoadStatus.Success -> {
                if (product != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(innerPadding)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = product.image ?: R.drawable.anh2
                                        ),
                                        contentDescription = product.name,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxHeight()
                                    )
                                }
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text(
                                        product.name ?: "Unknown product",
                                        style = TextStyle(
                                            fontFamily = FontFamily(Font(R.font.lora)),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.W600
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        product.category ?: "",
                                        style = TextStyle(
                                            fontFamily = FontFamily(Font(R.font.inter)),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W500,
                                            color = Color.Black.copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                                    )

                                    Row {
                                        Text(
                                            text = "* 4.25",
                                            style = TextStyle(
                                                fontFamily = FontFamily(Font(R.font.inter)),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.W600
                                            )
                                        )
                                        Text(
                                            text = "12 Reviews",
                                            style = TextStyle(
                                                fontFamily = FontFamily(Font(R.font.inter)),
                                                fontSize = 14.sp,
                                                color = Color(0xFFA6A6AA)
                                            ),
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                    }

                                    Text(
                                        text = "Options",
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(Font(R.font.inter)),
                                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                    )

                                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                        variants.forEachIndexed { index, variant ->
                                            OptionCard(
                                                title = variant.type ?: "Option ${index + 1}",
                                                price = "$${variant.price ?: product.price ?: 0.0}",
                                                stockStatus = "In Stock",
                                                isSelected = index == selectOptionIndex.value,
                                                onClick = {
                                                    selectOptionIndex.value = index
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        if (variants.isEmpty()) {
                                            OptionCard(
                                                title = "Default",
                                                price = "$${product.price ?: 0.0}",
                                                stockStatus = "In Stock",
                                                isSelected = true,
                                                onClick = { }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = product.description ?: "No description available",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xffECBF3E))
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "With your options",
                                        color = Color(0xff616161),
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "$${variants.getOrNull(selectOptionIndex.value)?.price ?: product.price ?: 0.0}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xff3A3A3A)
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (uiState.name == "") {
                                            Toast.makeText(
                                                context,
                                                "Please log in!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate("login") {
                                                popUpTo("home") { inclusive = false }
                                            }
                                        } else {
                                            val selectedPrice =
                                                variants.getOrNull(selectOptionIndex.value)?.price ?: product.price ?: 0.0
                                            cartViewModel.addToCart(
                                                productId = product.id.toString(),
                                                total = selectedPrice
                                            )
                                            Toast.makeText(
                                                context,
                                                "Added to cart successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                                ) {
                                    Text(text = "Add to cart", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            is LoadStatus.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${(uiState.status as LoadStatus.Error)}")
                }
            }

            else -> {}
        }
    }
}


@Composable
fun OptionCard(
    title: String,
    price: String,
    stockStatus: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xffE4BF55) else Color.LightGray
    val backgroundColor = if (isSelected) Color(0xFFFFF8EF) else Color.White
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val cardWidth = screenWidth * 0.41f
    val cardHeight = screenHeight * 0.18f

    Card(
        modifier = Modifier
            .size(width = cardWidth, height = cardHeight)
            .clickable { onClick() }
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = backgroundColor,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
            Divider(color = Color.LightGray)
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(text = price, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = stockStatus,
                    color = Color(0xFF2E7D32),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}