package com.example.lcshop.screens.admin.Product

import BrandsRepository
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lcshop.config.Constants.BASE_URL_IMG
import com.example.lcshop.data.model.Brand
import com.example.lcshop.data.model.Category
import com.example.lcshop.data.model.Product
import com.example.lcshop.data.model.ProductCreateRequest
import com.example.lcshop.data.model.ProductVariant
import com.example.lcshop.repository.CategoryRepository
import com.example.lcshop.repository.ProductRepository
import com.example.lcshop.repository.UploadRepository
import com.example.lcshop.screens.admin.Brands.BrandsAdminViewModel
import com.example.lcshop.screens.admin.Brands.BrandsAdminViewModelFactory
import com.example.lcshop.screens.admin.Categories.CategoriesAdminViewModel
import com.example.lcshop.screens.admin.Categories.CategoriesAdminViewModelFactory
import com.example.lcshop.util.FileUtils
import com.example.lcshop.viewmodel.ProductViewModel
import com.example.lcshop.viewmodel.ProductViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductScreen(
    productId: Int,
    onProductUpdated: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // UI States
    var selectedTab by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Form validation states
    var productNameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    // Product basic info states
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedBrand by remember { mutableStateOf<Brand?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }

    // Product variants state
    var variants by remember { mutableStateOf(listOf(ProductVariant(null, "", "", "", 0))) }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uploadedImageUrl = null
        errorMessage = null
    }

    // ViewModels
    val brandsViewModel: BrandsAdminViewModel = viewModel(
        factory = BrandsAdminViewModelFactory(BrandsRepository())
    )
    val categoriesViewModel: CategoriesAdminViewModel = viewModel(
        factory = CategoriesAdminViewModelFactory(CategoryRepository())
    )
    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(ProductRepository())
    )

    // Lấy dữ liệu
    val brands by brandsViewModel.brands.collectAsState()
    val categories by categoriesViewModel.categories.collectAsState()
    val product by productViewModel.productDetail.collectAsState()

    // Tải dữ liệu khi khởi tạo
    LaunchedEffect(productId) {
        coroutineScope.launch {
            brandsViewModel.fetchBrands()
            categoriesViewModel.fetchCategories()

        }
        productViewModel.getProductById(productId)
    }
Log.d("UpdateProductScreen", "Fetched product: $product")
    // Cập nhật state khi có dữ liệu sản phẩm
    LaunchedEffect(product) {
        product?.let {
            productName = it.product_name ?: ""
            price = it.price.toString()
            description = it.description ?: ""
            selectedCategory = categories.firstOrNull { category -> category.id == it.category_id }
            selectedBrand = brands.firstOrNull { brand -> brand.id == it.brand_id }
            uploadedImageUrl = it.images?.firstOrNull { img -> img.is_primary }?.image_url
            variants = it.variants?.map { variant ->
                ProductVariant(
                    id = variant.id,
                    color = variant.color,
                    size = variant.size,
                    material = variant.material,
                    stock_quantity = variant.stock_quantity
                )
            } ?: listOf(ProductVariant(null, "", "", "", 0))
            Log.d("UpdatePro_ductScreen", "Uploaded image URL: $uploadedImageUrl")
        }
    }

    // Validation functions
    fun validateBasicInfo(): Boolean {
        var isValid = true

        if (productName.isBlank()) {
            productNameError = "Tên sản phẩm không được để trống"
            isValid = false
        } else {
            productNameError = null
        }

        if (price.isBlank()) {
            priceError = "Giá sản phẩm không được để trống"
            isValid = false
        } else if (price.toIntOrNull() == null || price.toInt() <= 0) {
            priceError = "Giá sản phẩm phải là số dương"
            isValid = false
        } else {
            priceError = null
        }


        return isValid && selectedCategory != null && selectedBrand != null
    }

    // Update product function
    fun updateProduct() {
        if (!validateBasicInfo()) {
            errorMessage = "Vui lòng kiểm tra lại thông tin cơ bản"
            return
        }

        val validVariants = variants.filter {
            it.color.isNotBlank() && it.size.isNotBlank() && it.stock_quantity > 0
        }

        if (validVariants.isEmpty()) {
            errorMessage = "Vui lòng thêm ít nhất một biến thể hợp lệ"
            return
        }

        coroutineScope.launch {
            try {
                isLoading = true
                errorMessage = null

                // Upload ảnh nếu có
                if (selectedImageUri != null) {
                    val uri = selectedImageUri
                    val imageUrl = uploadImage(context, uri!!, productId)
                    if (imageUrl != null) {
                        uploadedImageUrl = imageUrl
                    } else {
                        errorMessage = "Cập nhật sản phẩm thành công, nhưng tải ảnh thất bại."
                    }
                }

                // Tạo ProductCreateRequest
                val productRequest = ProductCreateRequest(
                    product_name = productName.trim(),
                    price = price.toInt(),
                    description = description.trim().ifEmpty { null },
                    category_id = selectedCategory!!.id,
                    brand_id = selectedBrand!!.id,
                    img_url = uploadedImageUrl ?: "",
                    variants = validVariants
                )

                val json = Gson().toJson(productRequest)
                Log.d("UpdatePro_ductScreen", "Request JSON: $json")

                val productRepo = ProductRepository()
                val updatedProduct = productRepo.updateProduct(productId, productRequest)
                Log.d("UpdatePro_ductScreen", "API Response: $updatedProduct")

                if (updatedProduct != null) {
                    showSuccessMessage = true
                    kotlinx.coroutines.delay(1500)
                    onProductUpdated()
                } else {
                    errorMessage = "Không thể cập nhật sản phẩm. Vui lòng thử lại."
                }
            } catch (e: Exception) {
                Log.e("UpdatePro_ductScreen", "Error updating product: ${e.message}", e)
                errorMessage = "Đã có lỗi xảy ra: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cập nhật sản phẩm", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isLoading) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Error message
                errorMessage?.let { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Success message
                if (showSuccessMessage) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "✓ Cập nhật sản phẩm thành công!",
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Thông tin cơ bản") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Biến thể sản phẩm") }
                    )
                }

                // Tab Content
                when (selectedTab) {
                    0 -> BasicInfoTab(
                        productName = productName,
                        onProductNameChange = {
                            productName = it
                            productNameError = null
                        },
                        productNameError = productNameError,
                        price = price,
                        onPriceChange = {
                            price = it
                            priceError = null
                        },
                        priceError = priceError,
                        description = description,
                        onDescriptionChange = { description = it },
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        selectedBrand = selectedBrand,
                        onBrandSelected = { selectedBrand = it },
                        selectedImageUri = selectedImageUri,
                        onImageSelected = { imagePickerLauncher.launch("image/*") },
                        categories = categories,
                        brands = brands,
                        onContinue = {
                            if (validateBasicInfo()) {
                                selectedTab = 1
                            }
                        },
                        isLoading = isLoading
                    )
                    1 -> VariantsTab(
                        variants = variants,
                        onVariantsChange = { variants = it },
                        onCreateProduct = ::updateProduct,
                        isLoading = isLoading
                    )
                }
            }

            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Đang cập nhật sản phẩm...",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}