package com.example.furniturestore.ui.screens.checkout

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.furniturestore.R
import com.example.furniturestore.common.status.LoadStatus
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    viewModel: CheckoutViewModel,
    navController: NavController
) {
    val interFont = FontFamily(Font(R.font.inter))
    val user by viewModel.user.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val orderStatus by viewModel.status.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val totalPrice = cartItems.sumOf { it.price * it.quantity }
    val context = LocalContext.current
    val customFont = FontFamily(Font(R.font.lora))

    LaunchedEffect(user) {
        user?.let {
            fullName = it.displayName ?: ""
            phone = it.phoneNumber ?: ""
            address = it.address ?: ""
            email = it.email ?: ""
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
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
                        text = "Checkout",
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
    ) { paddingValues ->
        when (orderStatus) {
            is LoadStatus.Innit, is LoadStatus.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadStatus.Error -> {
                val errorMessage = (orderStatus as LoadStatus.Error).error
                LaunchedEffect(errorMessage) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    delay(2000)
                    viewModel.resetStatus()
                }
            }

            is LoadStatus.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            "Billing details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = interFont
                        )
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full name", fontFamily = interFont) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone *", fontFamily = interFont) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address", fontFamily = interFont) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", fontFamily = interFont) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Your cart",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = interFont
                        )
                        Spacer(Modifier.height(8.dp))

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.imageRes,
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = interFont
                                    )
                                    Text(text = "x${item.quantity}", fontFamily = interFont)
                                }
                                Text(
                                    text = "$${String.format("%.2f", item.price * item.quantity)}",
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = interFont
                                )
                            }
                            Divider()
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Payment method",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = interFont
                        )
                        Spacer(Modifier.height(4.dp))
                        Row {
                            RadioButton(
                                selected = true,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(Color(0xFF1E64DA))
                            )
                            Text(" COD", fontSize = 14.sp, fontFamily = interFont)
                        }
                    }

                    // Bottom section: total & button
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = interFont
                            )
                            Text(
                                "$${String.format("%.2f", totalPrice)}",
                                fontWeight = FontWeight.Bold, fontFamily = interFont
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                user?.let {
                                    viewModel.placeOrder(
                                        cartItems = cartItems,
                                        userId = it.uid,
                                        name = fullName,
                                        phone = phone,
                                        email = email,
                                        address = address,
                                        onSuccess = {
                                            navController.navigate("home")
                                        },
                                        onFailure = {
                                            // handle failure
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E64DA)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Save and continue",
                                color = Color.White,
                                fontWeight = FontWeight.Bold, fontFamily = interFont
                            )
                        }
                    }
                }
            }
        }
    }
}
