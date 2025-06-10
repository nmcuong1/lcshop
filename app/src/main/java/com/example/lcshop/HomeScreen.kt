package com.example.lcshop
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

import com.example.lcshop.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import com.google.accompanist.pager.*
import android.util.Log
import androidx.compose.foundation.clickable
import com.example.lcshop.viewmodel.ProductViewModelFactory
import com.example.lschop.repository.ProductRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Log.d("HomeScreen", "HomeScreen started")

    val pagerState = rememberPagerState()
    val adImages = listOf("Quảng cáo 1", "Quảng cáo 2", "Quảng cáo 3")

    val context = LocalContext.current
    val repository = remember { ProductRepository() }

    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(repository)
    )

    // Lấy danh sách sản phẩm khi mở màn hình
    LaunchedEffect(Unit) {
        productViewModel.fetchProducts()
    }

    val products by productViewModel.products.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % adImages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LC Shop", color = Color.White) },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F5B))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Menu, null) },
                    label = { Text("Menu") },
                    selected = false,
                    onClick = { /* TODO */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocationOn, null) },
                    label = { Text("Deals") },
                    selected = false,
                    onClick = { /* TODO */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, null) },
                    label = { Text("Cart") },
                    selected = false,
                    onClick = { navController.navigate("cart") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Account") },
                    selected = false,
                    onClick = { /* TODO */ }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Hỗ trợ khách hàng */ },
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Chăm sóc khách hàng",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // Quảng cáo trượt ngang
            HorizontalPager(
                count = adImages.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .background(Color(0xFFBBDEFB), shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(adImages[page], fontSize = 20.sp, color = Color.White)
                }
            }

            // Danh sách sản phẩm dạng lưới
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                items(products) { product ->
                    Box(
                        modifier = Modifier
                            .height(180.dp)
                            .background(Color.LightGray)
                            .clickable {
                                navController.navigate("product_detail/${product.id}")
                            },

                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = product.product_name,
                                fontSize = 16.sp,
                                color = Color.Black,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "${product.price} đ",
                                fontSize = 14.sp,
                                color = Color(0xFF1976D2)
                            )
                            IconButton(onClick = { /* TODO: Add to cart */ }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Add to cart")
                            }
                        }
                    }
                }
            }
        }
    }
}
