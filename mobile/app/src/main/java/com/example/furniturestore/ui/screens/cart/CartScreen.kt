package com.example.furniturestore.ui.screens.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.furniturestore.R
import com.example.furniturestore.common.enum.LoadStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val customFont = FontFamily(Font(R.font.lora))
    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cart",
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            when(uiState.status){
                is LoadStatus.Innit  -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is LoadStatus.Success -> {
                    if (cartItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Your cart is empty", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            items(cartItems) { item ->
                                CartItemRow(
                                    item = CartItem(
                                        id = item.id,
                                        name = item.name,
                                        price = item.price,
                                        quantity = item.quantity,
                                        imageRes = item.imageRes
                                    ),
                                    onQuantityChange = { newQuantity ->
                                        viewModel.updateQuantity(item, newQuantity)
                                    }
                                )
                                Divider(color = Color.LightGray, thickness = 1.dp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Cart total",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color(0xFF797A7B)
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", totalPrice)}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                                Button(
                                    onClick = { /* Handle checkout */ },
                                    modifier = Modifier
                                        .height(48.dp)
                                        .width(150.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF363939)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "CHECKOUT",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                is LoadStatus.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error loading cart: ${(uiState.status as LoadStatus.Error).error}", color = Color.Red)
                    }
                }
                is LoadStatus.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image
        Image(
            painter = rememberAsyncImagePainter(
                model = item.imageRes
            ),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
//        AsyncImage(
//            model = item.imageRes,
//            contentDescription = null,
//            modifier = Modifier
//                .size(64.dp)
//                .clip(RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )

        Spacer(modifier = Modifier.width(16.dp))

        // Product Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(Color(0xFFF5F5F5)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove, contentDescription = null
                    )
                }
                Text(
                    text = item.quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = null
                    )
                }
            }
        }

        // Price
        Text(
            text = "$${String.format("%.2f", item.price)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageRes: String
)