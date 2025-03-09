package com.example.furniturestore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.furniturestore.MainViewModel
import com.example.furniturestore.R


@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
    mainViewModel: MainViewModel
) {

    Scaffold (
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top =  30.dp)
                    .height(56.dp)
                    .background(Color(0xFFFFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "app logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 100.dp, height = 56.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .clickable {
                              //
                            }
                    ){
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                        ){
                            Text("Đăng nhập",
                                style = TextStyle(
                                    fontWeight = FontWeight.W500,
                                    fontSize = 18.sp,
                                    lineHeight = 21.sp,
                                ),)
                        }
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "User avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)
            .background(Color(0xFFF5F5FA))) {
            Column (modifier = Modifier.padding(10.dp).background(Color(0xFFF5F5FA))){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            "Just for you",
                            style = TextStyle(
                                fontWeight = FontWeight.W700,
                                fontSize = 20.sp,
                                lineHeight = 21.sp,
                                color = Color(0xFF27272A)
                            ),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(2f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "< >",
                            style = TextStyle(
                                fontWeight = FontWeight.W400,
                                fontSize = 16.sp,
                                lineHeight = 21.sp,
                                color = Color(0xFF27272A)
                            ),
                        )
                    }
                }
                LazyRow(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color(0xFFF5F5FA))
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(5) { item ->
                        ListCard()
                    }
                }
            }
            Column (modifier = Modifier.padding(10.dp).background(Color(0xFFF5F5FA))){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            "Deals",
                            style = TextStyle(
                                fontWeight = FontWeight.W700,
                                fontSize = 20.sp,
                                lineHeight = 21.sp,
                                color = Color(0xFF27272A)
                            ),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(2f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "View all",
                            style = TextStyle(
                                fontWeight = FontWeight.W400,
                                fontSize = 16.sp,
                                lineHeight = 21.sp,
                                color = Color(0xFF27272A)
                            ),
                        )
                    }
                }

            }

        }
    }
}
@Composable
fun ListCard(){
    Card(
        modifier = Modifier
            .height(357.dp)
            .width(206.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.anh2),
            contentDescription = "ảnh sản phẩm tủ phòng khách",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 206.dp, height = 253.dp)
        )
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "Tủ phòng khách",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tủ",
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "$265.99",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}