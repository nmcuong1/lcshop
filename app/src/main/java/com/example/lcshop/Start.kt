package com.example.lcshop
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import kotlinx.coroutines.delay

@Composable
fun LCShopSplashScreen(navController: NavHostController) {
    LaunchedEffect(true) {
        delay(1500) // Chờ 2.5 giây
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true } // Xoá splash khỏi backstack
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF001F5B)),
        contentAlignment = Alignment.Center
    ) {
        // Hình logo ở giữa
        Image(
            painter = painterResource(id = R.drawable.lg),
            contentDescription = "LC Shop Logo",
            modifier = Modifier
                .size(200.dp)
        )
    }
}
