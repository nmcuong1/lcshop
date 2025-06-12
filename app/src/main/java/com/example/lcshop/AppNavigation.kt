package com.example.lcshop

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lcshop.screens.ProductDetailScreen

import com.example.lcshop.screens.admin.BrandsAdminScreen
import com.example.lcshop.screens.admin.CategoriesAdminScreen
import com.example.lcshop.screens.admin.Product.AddProductScreen
import com.example.lcshop.screens.admin.Product.ProductsAdminScreen

import com.example.lcshop.screens.admin.Product.UpdateProductScreen

import com.example.lcshop.screens.admin.VariantsAdminScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("splash") {
            LCShopSplashScreen(navController)
        }


        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgetpassword") },
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToAdmin = { navController.navigate("admin") }
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
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toInt()
            ProductDetailScreen(navController, productId.takeIf { it != null } ?: 0) // Default to 0 if null
        }
        composable("checkout/{total}") { backStackEntry ->
            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
            CheckoutScreen(navController = navController, total = total)
        }
        composable("select_address"){
            SelectAddressScreen(navController=navController)
        }



        composable("admin") {
            AdminScreen(
                onNavigateToBrandsAdminScreen = { navController.navigate("brands") },
                onNavigateToProductsAdminScreen = { navController.navigate("products") },
                onNavigateToCategoriesAdminScreen = { navController.navigate("categories") },
                onNavigateToVariantsAdminScreen = { navController.navigate("variants") }
            )
        }

        composable("brands") {
            BrandsAdminScreen()
        }
        composable("products") {
            ProductsAdminScreen(
                onNavigateToAddProduct = { navController.navigate("AddProduct") },
                onNavigateToUpdateProduct = { productId ->
                    navController.navigate("UpdateProduct/$productId")
                }
            )
        }
        composable("categories") {
            CategoriesAdminScreen()
        }
//        composable("variants") {
//            VariantsAdminScreen()
//        }
        composable("AddProduct") {
            AddProductScreen(
                onProductAdded = { navController.navigate("products") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("UpdateProduct/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()

            productId?.let {
                UpdateProductScreen(
                    productId = it,
                    onProductUpdated = { navController.navigate("products") },
                    onBack = { navController.popBackStack() }
                )
            }
        }

    }
}
