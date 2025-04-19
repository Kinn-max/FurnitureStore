package com.example.furniturestore.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.furniturestore.R
import com.example.furniturestore.ui.screens.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    navController: NavController,
    viewModel2: ProfileViewModel,
    onBackClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val signOutEvent by viewModel.signOutEvent.collectAsState()
    val uiState by viewModel2.uiState.collectAsState()
    val customFont = FontFamily(Font(R.font.lora))
    val smallFont = FontFamily(Font(R.font.inter))
    LaunchedEffect(signOutEvent) {
        if (signOutEvent) {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }

    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
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
            // Hiển thị thông tin khi người dùng đã đăng nhập
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9F9FC))
                    .padding(top = 70.dp, bottom = 20.dp, start = 20.dp, end = 20.dp )
            ) {

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4AABD2)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))


                        Column(modifier = Modifier.weight(1f)) {
                            Text("Trần Chung Kiên",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                fontFamily = customFont
                            )

                            Text("@Itunoluwa", color = Color.White.copy(alpha = 0.8f), fontFamily = smallFont)
                        }


                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                }

                // --- Settings List ---
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Tài khoản",
                    subtitle = "Make changes to your account",
                    warning = true,
                    navController,
                    "account"
                )
                SettingsItem(
                    icon = Icons.Default.Inbox,
                    title = "Đơn hàng đã đặt",
                    subtitle = "Manage your orders",
                    false,
                    navController,
                    ""
                )
                ToggleItem(
                    icon = Icons.Default.Fingerprint,
                    title = "Face ID / Touch ID",
                    subtitle = "Manage your device security"
                )
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Two-Factor Authentication",
                    subtitle = "Further secure your account for safety",
                    false,
                    navController,"??"
                )
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = "Log out",
                    subtitle = "Further secure your account for safety",
                    false,
                    navController,
                    "logout"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("More", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    subtitle = "",
                    false,
                    navController,
                    "//"
                )
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About App",
                    subtitle = "",
                    false,
                    navController,
                    "??"
                )
            }
        }
    }
}
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    warning: Boolean = false,
    navController: NavController,
    link:Any
) {
    val customFont = FontFamily(Font(R.font.lora))
    val smallFont = FontFamily(Font(R.font.inter))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable {
                if(link.toString().length > 2){
                    if(link.toString().equals("logout")){
//                      navController.navigate()
                    }
                    navController.navigate(link.toString())
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF2196F3),
            modifier = Modifier
                .size(30.dp)
                .background(Color(0x1A2196F3), shape = CircleShape)
                .padding(5.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title,
                fontWeight = FontWeight.W600,
                fontFamily = customFont,
                fontSize = 18.sp
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = smallFont
                )
            }
        }
        if (warning) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Next",
            tint = Color.Gray
        )
    }
}
@Composable
fun ToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF3E3CFF)
            )
        )
    }
}