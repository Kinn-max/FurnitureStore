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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.compose.rememberNavController
import com.example.furniturestore.MainViewModel
import com.example.furniturestore.R
import com.example.furniturestore.ui.theme.FurnitureStoreTheme


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Tạo các mock/stub dependencies cho preview
    val navController = rememberNavController()
    val homeViewModel = HomeViewModel()
    val mainViewModel = MainViewModel() 

    FurnitureStoreTheme { 
        HomeScreen(
            navController = navController,
            viewModel = homeViewModel,
            mainViewModel = mainViewModel
        )
    }
}
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
                    .padding(top = 20.dp)
                    .height(56.dp)
                    .background(Color(0xFFFFFFFF)),
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
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Giỏ hàng",
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable {
                                //
                            }
                            .size(28.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .clickable {
                                 //
                            }
                    ) {
                        Text(
                            text = "Đăng nhập",
                            style = TextStyle(
                                fontWeight = FontWeight.W500,
                                fontSize = 18.sp,
                                lineHeight = 21.sp
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "User avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)
            .background(Color(0xFFF5F5FA))
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
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
                                fontWeight = FontWeight.W600,
                                fontSize = 25.sp,
                                lineHeight = 21.sp,
                                color = Color(0xFF27272A)
                            ),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            modifier = Modifier
                                .width(48.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Trước đó",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        // Xử lý khi nhấn mũi tên trái
                                    }
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Tiếp theo",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        // Xử lý khi nhấn mũi tên phải
                                    }
                            )
                        }

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
            Column (modifier = Modifier.padding(10.dp).background(Color(0xFFF5F5FA))) {
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
                                fontWeight = FontWeight.W600,
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
                                color = Color(0xFF9D9C9D)
                            ),
                        )
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Card(
                        modifier = Modifier
                            .height(218.dp)
                            .width(163.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anh2),
                            contentDescription = "ảnh sản phẩm tủ phòng khách",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 163.dp, height = 134.dp)
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Tủ phòng khách",
                                fontWeight = FontWeight.W600,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "* 4.25",
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$265.99",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .height(218.dp)
                            .width(163.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anh2),
                            contentDescription = "ảnh sản phẩm tủ phòng khách",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 163.dp, height = 134.dp)
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Tủ phòng khách",
                                fontWeight = FontWeight.W600,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "* 4.25",
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$265.99",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Column (modifier = Modifier.padding(10.dp).background(Color(0xFFF5F5FA))) {
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
                                fontWeight = FontWeight.W600,
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
                                color = Color(0xFF9D9C9D)
                            ),
                        )
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Card(
                        modifier = Modifier
                            .height(218.dp)
                            .width(163.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anh2),
                            contentDescription = "ảnh sản phẩm tủ phòng khách",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 163.dp, height = 134.dp)
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Tủ phòng khách",
                                fontWeight = FontWeight.W600,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "* 4.25",
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$265.99",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .height(218.dp)
                            .width(163.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anh2),
                            contentDescription = "ảnh sản phẩm tủ phòng khách",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 163.dp, height = 134.dp)
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Tủ phòng khách",
                                fontWeight = FontWeight.W600,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "* 4.25",
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$265.99",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                fontWeight = FontWeight.W600,
                fontSize = 20.sp,
                lineHeight = 21.sp,
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