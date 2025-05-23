package com.example.furniturestore

import androidx.navigation.compose.NavHost
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.furniturestore.ui.screens.cart.CartScreen
import com.example.furniturestore.ui.screens.home.HomeScreen
import com.example.furniturestore.ui.screens.home.HomeViewModel
import com.example.furniturestore.ui.screens.ProductDetailScreen
import com.example.furniturestore.ui.screens.profile.ProfileScreen
import com.example.furniturestore.ui.screens.auth.AuthViewModel
import com.example.furniturestore.ui.screens.cart.CartViewModel
import com.example.furniturestore.ui.screens.checkout.CartCheckoutScreen
import com.example.furniturestore.ui.screens.checkout.CheckoutViewModel
import com.example.furniturestore.ui.screens.favorite.FavoriteScreen
import com.example.furniturestore.ui.screens.favorite.FavoriteViewModel
import com.example.furniturestore.ui.screens.profile.MyAccountScreen
import com.example.furniturestore.ui.screens.profile.OrderListScreen
import com.example.furniturestore.ui.screens.profile.ProfileViewModel
import com.example.furniturestore.ui.screens.search.SearchScreen
import com.example.furniturestore.ui.screens.search.SearchViewModel


sealed class Screen(val route: String) {

    object Home : Screen("home")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object MyAccount : Screen("account")
    object Wishlist : Screen("favorite")
    object Cart : Screen("cart")
    object Login : Screen("login")
    object Register : Screen("register")
    object ProductDetail : Screen("product-detail/{productId}")
    object CheckoutScreen : Screen("checkout")
    object OrderlistScreen : Screen("orderlist")
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

    val navBackStackEntry = navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry.value?.destination?.route ?: Screen.Home.route // Giá trị mặc định là Home


    // Các màn hình mà bạn muốn hiển thị bottomBar
    val screensWithBottomBar = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Wishlist.route,
        Screen.Cart.route,
        Screen.Profile.route

    )

    Scaffold(
        bottomBar = {
            if (currentRoute in screensWithBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color.White),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == Screen.Home.route,
                        onClick = { navController.navigate(Screen.Home.route) },
                        label = { Text("Home", color = Color.Black) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Home,
                                contentDescription = "Home",
                                tint = Color.Black
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == Screen.Wishlist.route,
                        onClick = { navController.navigate(Screen.Wishlist.route) },
                        label = { Text("Wishlist", color = Color.Gray) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = Color.Gray
                            )
                        }
                    )
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFF6D56E), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == Screen.Cart.route,
                        onClick = { navController.navigate(Screen.Cart.route) },
                        label = { Text("Cart", color = Color.Gray) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.Gray
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == Screen.Profile.route,
                        onClick = { navController.navigate(Screen.Profile.route) },
                        label = { Text("Profile", color = Color.Gray) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Profile",
                                tint = Color.Gray
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val viewModel: AuthViewModel = viewModel()
        val profileViewModel: ProfileViewModel = hiltViewModel()
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
                val searchViewModel: SearchViewModel = hiltViewModel()
                SearchScreen(navController = navController, searchViewModel)
            }
            composable(
                Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(navController = navController, productId = productId)
            }
            composable(Screen.Register.route) {
                CreateAccount(navController)
            }
            composable(Screen.Wishlist.route) {
                val favoriteViewModel: FavoriteViewModel = hiltViewModel()
                FavoriteScreen(
                    favoriteViewModel,navController
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(viewModel, navController) {
                    navController.navigate("profile")
                }
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    profileViewModel,
                    viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.MyAccount.route) {
                MyAccountScreen(  profileViewModel,
                    navController = navController,)
            }
            composable(Screen.OrderlistScreen.route) {
                OrderListScreen(  profileViewModel,
                    navController = navController,)
            }
            composable(Screen.Cart.route) {
                val cartViewModel: CartViewModel = hiltViewModel()
                CartScreen(navController, cartViewModel)
            }
            composable(Screen.CheckoutScreen.route) {
                val checkoutViewModel: CheckoutViewModel = hiltViewModel()
                CartCheckoutScreen(
                    navController = navController,
                    viewModel = checkoutViewModel
                )
            }
        }
    }
}