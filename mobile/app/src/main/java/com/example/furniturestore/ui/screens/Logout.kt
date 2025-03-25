package com.example.furniturestore

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.nio.file.WatchEvent

@Composable
fun LogoutScreen() {
    val context = LocalContext.current
    Column (
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Text("LOG OUT!", modifier =
            Modifier.padding(top = 50.dp, bottom = 10.dp),
            style = TextStyle(fontFamily = LoraFontFamily,
                color = Color(0xFF03A678), fontSize = 50.sp)
        )

        Text("You are log out now !",
            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        OutlinedButton(onClick = {},
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(width = 2.dp,
                color = Color(0xFF3267C8)
            ),
            modifier = Modifier
                .height(60.dp)
                .width(275.dp)
                .padding(top = 15.dp)
        )
        {
            Text("Log in",
                color = Color(0xFF3267C8),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
        }

        Button(
            onClick = {
                (context as? Activity)?.finish()
            },
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3267C8)),
            modifier = Modifier
                .padding(16.dp)
                .height(50.dp)
                .width(275.dp)
        ) {
            Text("close app",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp))
        }

        //
        Image(
            painter = painterResource(id = R.drawable.human),
            contentDescription = "human",
            modifier = Modifier
                .fillMaxHeight()
                .width(400.dp),
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLogoutScreen() {
    LogoutScreen()
}