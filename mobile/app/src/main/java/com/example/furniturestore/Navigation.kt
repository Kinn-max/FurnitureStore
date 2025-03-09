package com.example.furniturestore

import androidx.navigation.compose.NavHost
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.furniturestore.ui.screens.HomeScreen
import com.example.furniturestore.ui.screens.HomeViewModel


sealed class Screen(val route:String){

    object Home:Screen("home")
    object Search:Screen("search")
    object Profile:Screen("profile")
    object Login:Screen("login")
    object Register:Screen("register")
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val mainState = mainViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(mainState.value.error) {
        if (mainState.value.error != null && mainState.value.error != "") {
            Toast.makeText(context, mainState.value.error, Toast.LENGTH_LONG).show()
            mainViewModel.setError("")
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = navController.currentDestination?.route == Screen.Home.route,
                    onClick = { navController.navigate(Screen.Home.route) },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home Icon") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == Screen.Search.route,
                    onClick = { navController.navigate(Screen.Search.route) },
                    label = { Text("Search") },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == Screen.Profile.route,
                    onClick = {navController.navigate(Screen.Profile.route) },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile Icon")  }
                )
            }
        }
    ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        navController = navController,
                        viewModel = homeViewModel,
                        mainViewModel = mainViewModel
                    )
                }
                composable(Screen.Search.route) {
                    Text("Search Screen")
                }
            }
}}