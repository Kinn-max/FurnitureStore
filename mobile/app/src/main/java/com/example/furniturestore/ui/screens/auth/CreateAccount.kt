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
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

val LoraFontFamily = FontFamily(
    Font(R.font.loraregular, FontWeight.Normal),
)

@Composable
fun CreateAccount (navController: NavController) {
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
                .padding(bottom = 30.dp)

            ,
            contentScale = ContentScale.FillWidth
        )

        // Title
        Text(
            text = "Create account",
            fontFamily = LoraFontFamily,
            fontSize = 24.sp,
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

        // Full Name Input
        Text(
            text = "Full name",
            fontSize = 16.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        TextField()

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

        //confirmpassword
        Text(
            text = "Confirm Password",
            fontSize = 16.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        TextField2()

        //newfield
        Text(
            text = "New field",
            fontSize = 16.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        TextField()

        //button sign up
        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3267C8)),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.height(50.dp).fillMaxWidth()
        ) {
            Text("Sign up", fontSize = 20.sp)
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
        //
        Text(modifier = Modifier.padding(top = 20.dp, bottom = 15.dp),
            text = buildAnnotatedString {
                append("Already have an account? ")

                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Log in")
                }
            }, style = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W400,
                fontSize = 18.sp
            )
        )
    }
}


@Composable
fun TextField()  {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(value = text , onValueChange = { text = it },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color(0xFFB1B2B2),
            unfocusedContainerColor = Color(0xFF3300)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 13.dp)
    )
}


@Composable
fun TextField2()  {
    var text by remember { mutableStateOf("") }
    var isShowPassword by remember { mutableStateOf(false) }

    OutlinedTextField(value = text, onValueChange = { text = it },
        colors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color(0xFFB1B2B2),
        unfocusedContainerColor = Color(0xFF3300)
    ),

        trailingIcon = {
            IconButton(onClick = {isShowPassword = !isShowPassword}) {
                Icon(Icons.Default.Lock, contentDescription = null)
            }
        },
        visualTransformation = if (isShowPassword) VisualTransformation.None
        else PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 13.dp))
}

@Composable
fun TextField3()  {
    var text by remember { mutableStateOf("") }
        OutlinedTextField(value = text , onValueChange = { text = it },
        label = {Text("Placeholder", color = Color(0xFFB1B2B2))},
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(0xFFB1B2B2),
                unfocusedContainerColor = Color(0xFF3300)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 13.dp)
    )
}

