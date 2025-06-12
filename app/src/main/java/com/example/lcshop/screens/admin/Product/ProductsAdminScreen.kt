package com.example.lcshop.screens.admin.Product

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lcshop.config.Constants.BASE_URL_IMG
import com.example.lcshop.data.model.Product
import com.example.lcshop.repository.ProductRepository
import com.example.lcshop.viewmodel.ProductViewModel
import com.example.lcshop.viewmodel.ProductViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsAdminScreen(
    onNavigateToAddProduct: () -> Unit,
    onNavigateToUpdateProduct:(Int)-> Unit
) {
    val repository = remember { ProductRepository() }
    val productViewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(repository))

    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    // Search and filter states
    var searchQuery by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        productViewModel.refreshProducts()
    }

    // Filter products based on search query
    val filteredProducts = products.filter { product ->
        product.product_name.contains(searchQuery, ignoreCase = true) ||
                product.description?.contains(searchQuery, ignoreCase = true) == true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Quản lý sản phẩm",
                    fontWeight = FontWeight.Bold,
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        // TODO: Navigate to add product screen
                        onNavigateToAddProduct()
                        Log.d("ProductAdmin", "Add product clicked")
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Thêm sản phẩm",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tìm kiếm sản phẩm...") },
                trailingIcon  = {
                    Icon(Icons.Default.Search, contentDescription = "Tìm kiếm")
                },
                leadingIcon= {
                    Row {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Xóa")
                            }
                        }
//                        IconButton(onClick = { showFilterMenu = true }) {
//                            Icon(Icons.Default.FilterList, contentDescription = "Lọc")
//                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                ),
                singleLine = true
            )

            // Filter dropdown menu
            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                val filterOptions = listOf("Tất cả", "Giá cao", "Giá thấp", "Mới nhất")
                filterOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedFilter = option
                            showFilterMenu = false
                            // TODO: Implement filter logic
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product count
            Text(
                text = "Tìm thấy ${filteredProducts.size} sản phẩm",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Lỗi: $error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inventory,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                if (searchQuery.isEmpty()) "Không có sản phẩm nào" else "Không tìm thấy sản phẩm",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductAdminItem(
                                product = product,
                                onEdit = {
                                    // TODO: Navigate to edit product screen
                                    onNavigateToUpdateProduct(product.id ?: 0)
                                    Log.d("ProductAdmin", "Edit product: ${product}")
                                },
                                onDelete = {
                                    // TODO: Show delete confirmation dialog
                                    Log.d("ProductAdmin", "Delete product: ${product}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductAdminItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Lấy ảnh chính từ danh sách images
    val primaryImageUrl = product.images
        ?.firstOrNull { it.is_primary }
        ?.image_url
        ?.let { "${BASE_URL_IMG}$it" }
        ?: "https://via.placeholder.com/150"

    Log.d("ProductAdminItem", "Primary Image URL: $primaryImageUrl")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image
            val imagePainter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(primaryImageUrl)
                    .crossfade(true)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .build()
            )

            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Image(
                    painter = imagePainter,
                    contentDescription = product.product_name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.product_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Giá: ${formatPrice(product.price.toString())}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.description ?: "Không có mô tả",
                    fontSize = 12.sp,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun formatPrice(price: Any): String {
    return try {
        val number = when (price) {
            is Int -> price.toDouble()
            is Double -> price
            is Float -> price.toDouble()
            is String -> price.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
        val formatter = java.text.DecimalFormat("#,###")
        "${formatter.format(number)}₫"
    } catch (e: Exception) {
        "${price}₫"
    }
}