package com.example.furniturestore

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.furniturestore.ui.screens.auth.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavController,  onSignInSuccess: () -> Unit) {
    val context = LocalContext.current
    val isSignedIn by viewModel.isSignedIn.collectAsState()
    val user by viewModel.user.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.handleSignInResult(result.data)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            onSignInSuccess()
        }
    }
    Column(
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Image(
            painter = painterResource(id = R.drawable.container),
            contentDescription = "Amazon Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            contentScale = ContentScale.FillWidth
        )

        // Title
        Text(
            text = "Welcome",
            fontSize = 24.sp,
            fontFamily = LoraFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Subtitle
        Text(
            text = "Find the things that you love!",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        Text(
            text = "-------- Sign in with Email --------",
            color = Color.Gray,
            fontSize = 12.sp
        )

        //email
        Text(
            text = "Email",
            fontSize = 16.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 13.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(0xFFB1B2B2),
                unfocusedContainerColor = Color(0xFF3300)
            )
        )

        //password
        Text(
            text = "Password",
            fontSize = 16.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        PasswordTextField(
            value = password,
            onValueChange = { password = it }
        )

        //button sign in
        Button(
            onClick = {
                viewModel.loginWithEmail(
                    email = email,
                    password = password,
                    onFailure = { errorMsg ->
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3267C8)),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .padding(top = 50.dp)
                .height(50.dp)
                .fillMaxWidth()
        ) {
            Text("Sign in", fontSize = 20.sp)
        }
        //

        Text(modifier = Modifier.padding(top = 20.dp, bottom = 15.dp),
            text = buildAnnotatedString {
                append("By continuing you accept our standard ")

                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("terms and conditions  ")
                }

                append(" and our ")

                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("privacy policy.")
                }
            }, style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 13.sp
            )
        )
        Box(
            modifier = Modifier
                .clickable { viewModel.signIn(launcher) }
                .width(250.dp)
                .height(50.dp)
                .padding(4.dp)
                .background(Color(0xFFD5EDFF), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(width = 15.dp, height = 20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "SIGN IN WITH GOOGLE",
                    color = Color(0xFF130160),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        //Create Account
        TextButton(onClick = {navController.navigate("register")}) {
            Text(
                text = "Create Account ?",
                fontSize = 18.sp,
                color = Color.Black
            )

        }

    }

}

