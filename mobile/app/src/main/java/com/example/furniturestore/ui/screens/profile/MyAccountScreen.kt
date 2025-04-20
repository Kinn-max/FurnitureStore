package com.example.furniturestore.ui.screens.profile

import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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


@Composable
fun MyAccountScreen(
    viewModel: ProfileViewModel,
    navController: NavController, ) {
    val uiState by viewModel.uiState.collectAsState()
    val customFont = FontFamily(Font(R.font.lora))
    val smallFont = FontFamily(Font(R.font.inter))
    Log.e("ProfileViewModel",uiState.userProfile.toString())
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
                        text = "My account",
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
                            text = "Bạn cần đăng nhập trước.",
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
                                text = "Đăng nhập ngay",
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        userProfile?.photoUrl ?: "${R.drawable.user}"
                                    ),
                                    contentDescription = "user",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.user),
                                    contentDescription = "Edit photo",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(24.dp)
                                        .background(Color.White, CircleShape)
                                        .padding(4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Hiển thị các thông tin như Name, Email, Date of Birth
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "NAME",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = "${userProfile?.displayName}",
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                readOnly = true
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "EMAIL",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = "${userProfile?.email}",
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                readOnly = true
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "SDT",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = userProfile?.phoneNumber ?: "Đéo có",
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                readOnly = true
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Address",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = userProfile?.address ?: "Đéo có",
                                onValueChange = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                readOnly = true
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Date of Birth or Phone Number
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "DATE OF BIRTH",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = userProfile?.phoneNumber ?: "Đéo có",
                                onValueChange = { /* Xử lý thay đổi giá trị nếu cần */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.user),
                                        contentDescription = "Dropdown",
                                        tint = Color.Black
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                readOnly = true
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))


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

