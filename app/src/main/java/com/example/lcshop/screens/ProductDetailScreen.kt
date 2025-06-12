// ProductDetailScreen.kt
package com.example.lcshop.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lcshop.data.model.Product
import com.example.lcshop.repository.ProductRepository
import com.example.lcshop.viewmodel.ProductViewModel
import com.example.lcshop.viewmodel.ProductViewModelFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.lcshop.config.Constants.BASE_URL_IMG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavHostController, productId: Int) {
    Log.d("ProductDetailScreen", "Loading product with ID: $productId")

    val repository = remember { ProductRepository() }
    val productViewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(repository))

    // Lấy dữ liệu sản phẩm theo ID
    LaunchedEffect(Unit) {
        productViewModel.getProductById(productId)
    }

    val product by productViewModel.productDetail.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    var quantity by remember { mutableStateOf(1) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedSize by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sản phẩm", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF001F5B))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Lỗi: $error", color = Color.Red)
                }
            } else if (product != null) {
                product?.let { p ->
                    // Product Image Placeholder
                    val primaryImageUrl = p.images
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
                            .height(200.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Product Info
                    Text(
                        text = p.product_name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Mã SP: ${p.id}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Giá: ${p.price} đ",
                        fontSize = 20.sp,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Colors
                    Text("Màu sắc", fontSize = 16.sp)
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        p.ProductVariants?.mapNotNull { it.color }?.distinct()?.forEach { color ->
                            val isSelected = selectedColor == color
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        when (color.lowercase()) {
                                            "đen" -> Color.Black
                                            "trắng" -> Color.White
                                            "xanh đậm" -> Color(0xFF003087)
                                            "xám" -> Color.Gray
                                            else -> Color.LightGray
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF1976D2) else Color.Gray,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        selectedColor = if (isSelected) null else color
                                        // Reset size khi thay đổi màu
                                        selectedSize = null
                                        quantity = 1
                                    }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } ?: run {
                            Text("Không có màu sắc", fontSize = 14.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sizes
                    Text("Kích thước", fontSize = 16.sp)
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        p.ProductVariants?.mapNotNull { it.size }?.distinct()?.forEach { size ->
                            val isSelected = selectedSize == size
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        if (isSelected) Color(0xFFBBDEFB) else Color(0xFFF2F2F2),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        selectedSize = if (isSelected) null else size
                                        // Cập nhật quantity dựa trên stock của variant
                                        val variant = p.ProductVariants?.find {
                                            it.color == selectedColor && it.size == size
                                        }
                                        quantity = if (variant?.stock_quantity ?: 0 > 0) 1 else 0
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = size,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color(0xFF1976D2) else Color.Black
                                )
                            }
                        } ?: run {
                            Text("Không có kích thước", fontSize = 14.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity & Cart
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "-",
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .clickable {
                                        if (quantity > 1) quantity--
                                    }
                                    .padding(horizontal = 8.dp)
                            )
                            Text(
                                text = quantity.toString(),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Text(
                                text = "+",
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .clickable {
                                        val variant = p.ProductVariants?.find {
                                            it.color == selectedColor && it.size == selectedSize
                                        }
                                        if (quantity < (variant?.stock_quantity ?: 0)) {
                                            quantity++
                                        }
                                    }
                                    .padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { /* TODO: Thêm vào giỏ hàng */ },
                            modifier = Modifier.height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            enabled = selectedColor != null && selectedSize != null && quantity > 0
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Thêm vào giỏ",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thêm vào giỏ", color = Color.White, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buy Now Button
                    Button(
                        onClick = { /* TODO: Mua ngay */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B)),
                        enabled = selectedColor != null && selectedSize != null && quantity > 0
                    ) {
                        Text("Mua ngay", color = Color.White, fontSize = 16.sp)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy sản phẩm", color = Color.Red)
                }
            }
        }
    }
}