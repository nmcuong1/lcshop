package com.example.lcshop

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.data.model.CartItem
import com.example.lcshop.data.model.CartItemUpdateRequest
import com.example.lcshop.repository.CartRepository
import com.example.lcshop.viewmodel.CartViewModel
import com.example.lcshop.viewmodel.CartViewModelFactory
import com.google.android.gms.analytics.ecommerce.Product

// Utility function to format price
fun formatPrice(price: String): String {
    return try {
        val number = price.toDouble()
        "%,.0f₫".format(number)
    } catch (e: Exception) {
        "${price}₫"
    }
}

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(
                color = if (checked) Color(0xFF001F5B) else Color.White,
                shape = CircleShape
            )
            .border(2.dp, Color(0xFF001F5B), CircleShape)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Text(
                "✓",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CartScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(CartRepository(RetrofitInstance.cartApi))
    )

    val cart by viewModel.cart.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedAll by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }

    LaunchedEffect(cart) {
        cart?.CartItems?.let { items ->
            val newSelectedItems = mutableMapOf<Int, Boolean>()
            items.forEach { item ->
                newSelectedItems[item.id] = selectedItems[item.id] ?: false
            }
            selectedItems = newSelectedItems
        }
    }
    Log.d("cart"," CartScreen recomposed with cart: $cart, loading: $loading, error: $error")
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
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LC Shop", color = Color.White, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            }
        }

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF001F5B))
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Lỗi: $error", color = Color.Red)
                        Button(
                            onClick = { viewModel.fetchCart() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B))
                        ) {
                            Text("Thử lại", color = Color.White)
                        }
                    }
                }
            }

            cart == null || cart!!.CartItems.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Giỏ hàng trống", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Thêm sản phẩm để bắt đầu mua sắm", color = Color.Gray)
                    }
                }
            }

            else -> {
                val cartItems = cart!!.CartItems

                // Select All + Delete
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundCheckbox(
                        checked = selectedAll,
                        onCheckedChange = { isChecked ->
                            selectedAll = isChecked
                            val newSelectedItems = selectedItems.toMutableMap()
                            cartItems.forEach { item ->
                                newSelectedItems[item.id] = isChecked
                            }
                            selectedItems = newSelectedItems
                        }
                    )
                    Text("  Tất cả", modifier = Modifier.weight(1f))
                    IconButton(onClick = {
//                        selectedItems.entries.filter { it.value }.forEach { (itemId, _) ->
//                            viewModel.removeFromCart(itemId)
//                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }

                // Cart Items List
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(cartItems) { index, item ->
                        CartItemRow(
                            item = item,
                            isSelected = selectedItems[item.id] ?: false,
                            onSelectionChange = { isSelected ->
                                selectedItems = selectedItems.toMutableMap().also {
                                    it[item.id] = isSelected
                                }
                                selectedAll = selectedItems.values.all { it }
                            },
                            onQuantityChange = { newQuantity ->
//                                viewModel.updateCartItem(
//                                    CartItemUpdateRequest(
//                                        cart_item_id = item.id,
//                                        quantity = newQuantity
//                                    )
//                                )
                            }
                        )

                        if (index < cartItems.size - 1) {
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color.LightGray,
                                thickness = 1.dp
                            )
                        }
                    }
                }

                // Checkout Section
                val totalPrice = cartItems.filter { selectedItems[it.id] == true }
                    .sumOf { it.total_price.toDoubleOrNull() ?: 0.0 }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formatPrice(totalPrice.toString()),
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF001F5B)
                    )
                    Button(
                        onClick = { navController.navigate("checkout/${totalPrice}") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B)),
                        enabled = selectedItems.values.any { it }
                    ) {
                        Text("Thanh toán", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    val primaryImage = item.Product.images.find { it.is_primary }
        ?: item.Product.images.firstOrNull()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundCheckbox(
            checked = isSelected,
            onCheckedChange = onSelectionChange
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Product Image
        AsyncImage(
            model = "https://your-base-url.com${primaryImage?.image_url}",
            contentDescription = item.Product.product_name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                item.Product.product_name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                "Màu: ${item.ProductVariant.color} | Size: ${item.ProductVariant.size}",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                formatPrice(item.Product.price),
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Quantity Controls
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (item.quantity > 1) {
                        onQuantityChange(item.quantity - 1)
                    }
                },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("-", fontSize = 16.sp, color = Color.Black)
            }

            Text(
                item.quantity.toString(),
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { onQuantityChange(item.quantity + 1) },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B))
            ) {
                Text("+", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}