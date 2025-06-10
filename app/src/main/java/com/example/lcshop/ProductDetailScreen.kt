package com.example.lcshop
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductDetailScreen(
    navController: androidx.navigation.NavHostController,
    productId: Int? = null // Giả sử product
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedSize by remember { mutableStateOf("M") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF001F5B)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: handle back */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "LC Shop",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Product Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Product Info
            Text("Tên sản phẩm", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("MASP", fontSize = 14.sp)
            Text("0đ", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Colors
            Text("Màu sắc", fontSize = 14.sp)
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.Red, shape = RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sizes
            Text("Kích thước:", fontSize = 14.sp)
            Row(modifier = Modifier.padding(top = 4.dp)) {
                listOf("S", "M", "L", "XL").forEach { size ->
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                if (size == selectedSize) Color.Gray else Color(0xFFF2F2F2),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedSize = size }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(size, fontSize = 14.sp)
                    }
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
                            .clickable { if (quantity > 1) quantity-- }
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
                            .clickable { quantity++ }
                            .padding(horizontal = 8.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .clickable { /* TODO: thêm vào giỏ */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng", tint = Color.Black)
                }
            }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Buy Now Button
            Button(
                onClick = { /* TODO: Mua ngay */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF001F5B))
            ) {
                Text("Mua ngay", color = Color.White, fontSize = 16.sp)
            }
        }
    }
