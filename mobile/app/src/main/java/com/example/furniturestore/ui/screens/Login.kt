package com.example.furniturestore

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen() {
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
            text = "-------- sign in with Email --------",
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
        TextField()

        //password
        Text(
            text = "Password",
            fontSize = 16.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        TextField2()

        //button sign in
        Button(
            onClick = { },
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

        //Create Account
        TextButton(onClick = {}) {
            Text(
                text = "Create Account ?",
                fontSize = 18.sp,
                color = Color.Black
            )

        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}