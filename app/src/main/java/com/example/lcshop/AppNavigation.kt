package com.example.lcshop

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lcshop.screens.ProductDetailScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            LCShopSplashScreen(navController)
        }

        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toInt()
            ProductDetailScreen(navController, productId.takeIf { it != null } ?: 0) // Default to 0 if null
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgetpassword") },
                onNavigateToHome = { navController.navigate("home") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("forgetpassword") {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onSendCode = { phone ->
                    // TODO: Gửi mã xác nhận đến số điện thoại
                }
            )
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("cart") {
            CartScreen(navController = navController)
        }

        composable("checkout/{total}") { backStackEntry ->
            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
            CheckoutScreen(navController = navController, total = total)
        }
        composable("select_address"){
            SelectAddressScreen(navController=navController)
        }
    }
}
