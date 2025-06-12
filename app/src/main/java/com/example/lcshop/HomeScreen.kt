package com.example.lcshop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.lcshop.repository.ProductRepository
import com.example.lcshop.viewmodel.ProductViewModel
import com.example.lcshop.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberAsyncImagePainter
import com.example.lcshop.config.Constants.BASE_URL_IMG

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Log.d("HomeScreen", "HomeScreen started")

    val pagerState = rememberPagerState() // Số lượng quảng cáo
    val adImages = listOf("Quảng cáo 1", "Quảng cáo 2", "Quảng cáo 3")

    val context = LocalContext.current

    val repository = remember { ProductRepository() }
    val productViewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(repository))

    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        productViewModel.refreshProducts()
    }

    // Tự động trượt quảng cáo
    LaunchedEffect(Unit) {
        while (true) {
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
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text("Menu") },
                    selected = false,
                    onClick = { /* TODO */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    label = { Text("Deals") },
                    selected = false,
                    onClick = { /* TODO */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Cart") },
                    selected = false,
                    onClick = { navController.navigate("cart") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
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
            // Hiển thị trạng thái tải hoặc lỗi
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Error: $error", color = Color.Red)
                }
            } else {
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
            }

            // Danh sách sản phẩm dạng lưới
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    Card(
                        modifier = Modifier
                            .height(250.dp)
                            .clickable {
                                navController.navigate("product_detail/${product.id}")
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(8.dp)
                        ) {
                            // Hiển thị ảnh sản phẩm
                            val primaryImageUrl = product.images
                                ?.firstOrNull { img -> img.is_primary }
                                ?.image_url
                                ?.let { "${BASE_URL_IMG}$it" }
                                ?: "https://via.placeholder.com/150"
                            Image(
                                painter = rememberAsyncImagePainter(primaryImageUrl),
                                contentDescription = "Ảnh sản phẩm",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Tên sản phẩm
                            Text(
                                text = product.product_name,
                                fontSize = 16.sp,
                                color = Color.Black,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Giá và nút giỏ hàng
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${product.price} VNĐ",
                                    fontSize = 14.sp,
                                    color = Color(0xFF1976D2),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { /* TODO: Add to cart */ }) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = "Add to cart",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
                if (products.isEmpty() && !isLoading && error == null) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(text = "No products available", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}