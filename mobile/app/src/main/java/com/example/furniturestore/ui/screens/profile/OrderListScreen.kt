package com.example.furniturestore.ui.screens.profile

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.furniturestore.R
import com.example.furniturestore.common.status.LoadStatus


@Composable
fun OrderListScreen(
    viewModel: ProfileViewModel,
    navController: NavController, ) {
    val uiState by viewModel.uiState.collectAsState()
    val customFont = FontFamily(Font(R.font.lora))
    val smallFont = FontFamily(Font(R.font.inter))
    val orders = uiState.orders
    val userProfile = uiState.userProfile
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFFFFF))
                    .padding(top = 10.dp)
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color(0xFF3A3A3A),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Order list",
                        fontFamily = customFont,
                        color = Color(0xFF3A3A3A),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
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
            is LoadStatus.Success -> {
                if (userProfile == null) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(Color(0xFFFFFFFF)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Please login.",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Button(
                            onClick = {
                                navController.navigate("login")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Login",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(Color(0xFFFFFFFF)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        orders.forEach { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {
                                    Text(order.orderItems.toString())
                                    Text(text = "Mã đơn hàng: ${order.id}", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Tổng tiền: ${order.totalAmount} VND", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Trạng thái: ${order.status}", style = MaterialTheme.typography.bodySmall)
                                    Text(text = "Ngày đặt: ${order.createdAt?.toDate()}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                }
            }
            is LoadStatus.Error -> {
                LaunchedEffect(uiState.status) {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            }
            else -> {
                // Handle initial state if needed
            }
        }
    }
}

