package com.example.lcshop.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Checkout : Screen("checkout/{total}") {
        fun createRoute(total: Int) = "checkout/$total"
    }
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
    object Admin : Screen("admin")
    object Brands : Screen("brands")
    object Products : Screen("products")
    object Categories : Screen("categories")
    object Variants : Screen("variants")
    object AddProduct : Screen("AddProduct")
    object UpdateProduct : Screen("UpdateProduct")

}