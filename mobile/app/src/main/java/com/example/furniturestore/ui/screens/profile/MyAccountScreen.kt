package com.example.furniturestore.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.furniturestore.R

//
//@Composable
//fun MyAccountScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(padding)
//            .background(Color(0xFFFFFFFF)),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Box {
//                Image(
//                    painter = rememberAsyncImagePainter(userProfile?.photoUrl ?: "${R.drawable.user}"),
//                    contentDescription = "user",
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clip(CircleShape)
//                        .background(Color.LightGray)
//                )
//                Icon(
//                    painter = painterResource(id = R.drawable.user),
//                    contentDescription = "Edit photo",
//                    tint = Color.Black,
//                    modifier = Modifier
//                        .align(Alignment.BottomEnd)
//                        .size(24.dp)
//                        .background(Color.White, CircleShape)
//                        .padding(4.dp)
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Hiển thị các thông tin như Name, Email, Date of Birth
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        ) {
//            Text(
//                text = "NAME",
//                fontSize = 12.sp,
//                color = Color.Gray,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            TextField(
//                value = "${userProfile?.displayName}",
//                onValueChange = { },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent
//                ),
//                readOnly = true
//            )
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        ) {
//            Text(
//                text = "EMAIL",
//                fontSize = 12.sp,
//                color = Color.Gray,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            TextField(
//                value = "${userProfile?.email}",
//                onValueChange = { },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent
//                ),
//                readOnly = true
//            )
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Date of Birth or Phone Number
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        ) {
//            Text(
//                text = "DATE OF BIRTH",
//                fontSize = 12.sp,
//                color = Color.Gray,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            TextField(
//                value = userProfile?.phoneNumber ?: "Đéo có",
//                onValueChange = { /* Xử lý thay đổi giá trị nếu cần */ },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
//                trailingIcon = {
//                    Icon(
//                        painter = painterResource(id = R.drawable.user),
//                        contentDescription = "Dropdown",
//                        tint = Color.Black
//                    )
//                },
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent
//                ),
//                readOnly = true
//            )
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // Đăng xuất
//        Button(
//            onClick = { viewModel.signOut(context) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 16.dp)
//                .height(50.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color(0xFF2196F3)
//            ),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Text(
//                text = "Đăng xuất",
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}