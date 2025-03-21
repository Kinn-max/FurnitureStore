package com.example.furniturestore.ui.screens

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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.furniturestore.R

@Composable
fun ProductDetailScreen(navController: NavController) {
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
                            .clickable { }
                            .size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
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
                    Image(
                        painter = painterResource(id = R.drawable.anh2),
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.height(300.dp)
                    )
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "Bộ Bàn Ăn Gỗ Cao Su Tự Nhiên MOHO VLINE 601",
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.lora)),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W600
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            "Bose",
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
                            OptionCard(
                                title = "Black",
                                price = "$79.99",
                                stockStatus = "In Stock",
                                isSelected = true,
                                onClick = { }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OptionCard(
                                title = "Black",
                                price = "$79.99",
                                stockStatus = "In Stock",
                                isSelected = true,
                                onClick = { }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OptionCard(
                                title = "White",
                                price = "$79.99",
                                stockStatus = "In Stock",
                                isSelected = false,
                                onClick = { }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "more information ..",
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
                            text = "$79.99",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xff3A3A3A)
                        )
                    }

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                    ) {
                        Text(text = "Add to cart", color = Color.White)
                    }
                }
            }
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

    Card(
        modifier = Modifier
            .size(width = 160.dp, height = 120.dp)
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
            Text(text = title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
            Divider(color = Color.LightGray)
            Column(modifier = Modifier.padding(12.dp)) {
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