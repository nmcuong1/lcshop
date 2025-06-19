package com.example.lcshop.screens.admin.Product

import BrandsRepository
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.lcshop.data.model.Brand
import com.example.lcshop.data.model.Category
import com.example.lcshop.data.model.ProductCreateRequest
import com.example.lcshop.data.model.ProductVariant
import com.example.lcshop.repository.CategoryRepository
import com.example.lcshop.repository.ProductRepository
import com.example.lcshop.repository.UploadRepository
import com.example.lcshop.screens.admin.Brands.BrandsAdminViewModel
import com.example.lcshop.screens.admin.Brands.BrandsAdminViewModelFactory
import com.example.lcshop.screens.admin.Categories.CategoriesAdminViewModel
import com.example.lcshop.screens.admin.Categories.CategoriesAdminViewModelFactory
import com.example.lcshop.viewmodel.ProductViewModel
import com.example.lcshop.viewmodel.ProductViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import android.content.Context
import com.example.lcshop.util.FileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onProductAdded: () -> Unit,
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
    var productName by remember { mutableStateOf("aó len") }
    var price by remember { mutableStateOf("2097778") }
    var description by remember { mutableStateOf("nice") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedBrand by remember { mutableStateOf<Brand?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }

    // Product variants state
    var variants by remember {
        mutableStateOf(listOf(
            ProductVariant(0, "red", "x", "len", 3)
        ))
    }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uploadedImageUrl = null
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

    val brands by brandsViewModel.brands.collectAsState()
    val categories by categoriesViewModel.categories.collectAsState()

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

    // Create product function
    fun createProduct() {
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

                val productRequest = ProductCreateRequest(
                    product_name = productName.trim(),
                    price = price.toInt(),
                    description = description.trim().ifEmpty { null },
                    category_id = selectedCategory!!.id,
                    brand_id = selectedBrand!!.id,
                    img_url = uploadedImageUrl ?: "",
                    variants = validVariants // Convert to ProductVariantRequest
                )

                val productRepo = ProductRepository()
                val createdProduct = productRepo.createProduct(productRequest)
                val productId = createdProduct?.id
                Log.d("AddProduct", "Product created successfully: ${productRequest.img_url}")
                Log.d("AddProduct", "Final image URL: $productId")
                if (productId != null) {
                    // Upload ảnh nếu có
                    if (selectedImageUri != null) {
                        val uri = selectedImageUri // Lấy giá trị hiện tại
                        val file = FileUtils.getFileFromUri(context, uri!!) // Sử dụng !! vì đã kiểm tra null
                        file?.let {
                            val mimeType = context.contentResolver.getType(uri!!)
                            if (mimeType == null || !listOf("image/jpeg", "image/jpg", "image/png", "image/gif").contains(mimeType)) {
                                errorMessage = "Định dạng file không hỗ trợ"
                                return@launch
                            }
                            val requestFile = it.asRequestBody(mimeType?.toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull())
                            val imagePart = MultipartBody.Part.createFormData("image", it.name, requestFile)
                            val uploadRepo = UploadRepository(context)
                            val uploadResponse = uploadRepo.uploadImage(productId, imagePart)
                            if (uploadResponse.isSuccessful) {
                                uploadedImageUrl = uploadResponse.body()?.imageUrl
                                Log.d("Upload", "Upload ảnh thành công: $uploadedImageUrl")
                            } else {
                                errorMessage = "Upload ảnh thất bại: ${uploadResponse.message()}"
                                return@launch
                            }
                        } ?: run { errorMessage = "Không thể truy cập file" }
                    }
                    showSuccessMessage = true
                    // Delay to show success message
                    kotlinx.coroutines.delay(1500)
                    onProductAdded()
                } else {
                    errorMessage = "Không thể tạo sản phẩm. Vui lòng thử lại."
                }
            } catch (e: Exception) {
                Log.e("AddProduct", "Error creating product", e)
                errorMessage = "Đã có lỗi xảy ra: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm sản phẩm mới", fontWeight = FontWeight.Bold) },
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
                            text = "✓ Tạo sản phẩm thành công!",
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
                        onCreateProduct = ::createProduct,
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
                                text = "Đang tạo sản phẩm...",
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

@Composable
fun BasicInfoTab(
    productName: String,
    onProductNameChange: (String) -> Unit,
    productNameError: String?,
    price: String,
    onPriceChange: (String) -> Unit,
    priceError: String?,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
    selectedBrand: Brand?,
    onBrandSelected: (Brand?) -> Unit,
    selectedImageUri: Uri?,
    onImageSelected: () -> Unit,
    categories: List<Category>,
    brands: List<Brand>,
    onContinue: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Product Name
        OutlinedTextField(
            value = productName,
            onValueChange = onProductNameChange,
            label = { Text("Tên sản phẩm *") },
            isError = productNameError != null,
            supportingText = productNameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Price
        OutlinedTextField(
            value = price,
            onValueChange = onPriceChange,
            label = { Text("Giá (VNĐ) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = priceError != null,
            supportingText = priceError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Mô tả sản phẩm") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Category Dropdown
        CategoryDropdown(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            enabled = !isLoading
        )

        // Brand Dropdown
        BrandDropdown(
            brands = brands,
            selectedBrand = selectedBrand,
            onBrandSelected = onBrandSelected,
            enabled = !isLoading
        )

        // Image Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable(enabled = !isLoading) { onImageSelected() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chọn ảnh sản phẩm",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Nhấn để chọn ảnh",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue Button
        Button(
            onClick = onContinue,
            enabled = !isLoading && selectedCategory != null && selectedBrand != null &&
                    productName.isNotBlank() && price.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tiếp tục đến biến thể", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun VariantsTab(
    variants: List<ProductVariant>,
    onVariantsChange: (List<ProductVariant>) -> Unit,
    onCreateProduct: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Biến thể sản phẩm",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Thêm các biến thể khác nhau của sản phẩm (màu sắc, kích thước, v.v.)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(variants) { index, variant ->
                VariantCard(
                    variant = variant,
                    onVariantChange = { updatedVariant ->
                        onVariantsChange(
                            variants.mapIndexed { i, v ->
                                if (i == index) updatedVariant else v
                            }
                        )
                    },
                    onRemove = {
                        if (variants.size > 1) {
                            onVariantsChange(variants.filterIndexed { i, _ -> i != index })
                        }
                    },
                    canRemove = variants.size > 1,
                    enabled = !isLoading
                )
            }

            item {
                OutlinedButton(
                    onClick = {
                        onVariantsChange(variants + ProductVariant(0, "", "", "", 0))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thêm biến thể mới")
                }
            }
        }

        // Create Product Button
        Button(
            onClick = onCreateProduct,
            enabled = !isLoading && variants.any {
                it.color.isNotBlank() && it.size.isNotBlank() && it.stock_quantity > 0
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isLoading) "Đang tạo..." else "Upload sản phẩm",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun VariantCard(
    variant: ProductVariant,
    onVariantChange: (ProductVariant) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Biến thể",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (canRemove) {
                    IconButton(
                        onClick = onRemove,
                        enabled = enabled
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Xóa biến thể",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = variant.color,
                    onValueChange = { onVariantChange(variant.copy(color = it)) },
                    label = { Text("Màu sắc *") },
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                )
                OutlinedTextField(
                    value = variant.size,
                    onValueChange = { onVariantChange(variant.copy(size = it)) },
                    label = { Text("Kích thước *") },
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = variant.material,
                    onValueChange = { onVariantChange(variant.copy(material = it)) },
                    label = { Text("Chất liệu") },
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                )
                OutlinedTextField(
                    value = variant.stock_quantity.toString(),
                    onValueChange = {
                        onVariantChange(variant.copy(stock_quantity = it.toIntOrNull() ?: 0))
                    },
                    label = { Text("Số lượng *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                )
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedCategory?.category_name ?: "",
            onValueChange = {},
            label = { Text("Danh mục *") },
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = { if (enabled) expanded = true }
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    text = { Text(category.category_name) }
                )
            }
        }
    }
}

@Composable
fun BrandDropdown(
    brands: List<Brand>,
    selectedBrand: Brand?,
    onBrandSelected: (Brand?) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedBrand?.brand_name ?: "",
            onValueChange = {},
            label = { Text("Thương hiệu *") },
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = { if (enabled) expanded = true }
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            brands.forEach { brand ->
                DropdownMenuItem(
                    onClick = {
                        onBrandSelected(brand)
                        expanded = false
                    },
                    text = { Text(brand.brand_name) }
                )
            }
        }
    }
}


// Upload image function
suspend fun uploadImage(context: Context, uri: Uri, productId: Int): String? {
    return try {
        val file = com.example.lcshop.util.FileUtils.getFileFromUri(context, uri)
        file?.let {
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType == null || !listOf(
                    "image/jpeg", "image/jpg", "image/png", "image/gif"
                ).contains(mimeType)
            ) {
                Log.e("Upload", "Unsupported file format: $mimeType")
                return null
            }

            val requestFile = it.asRequestBody(mimeType.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", it.name, requestFile)

            val uploadRepo = UploadRepository(context)
            val uploadResponse = uploadRepo.uploadImage(productId, imagePart)

            Log.d("Upload", "Upload response: $uploadResponse")
            if (uploadResponse.isSuccessful) {
                val url = uploadResponse.body()?.imageUrl
                Log.d("Upload", "Image uploaded successfully: $url")
                url
            } else {
                Log.e("Upload", "Upload failed: ${uploadResponse.errorBody()?.string()}")
                null
            }
        }
    } catch (e: Exception) {
        Log.e("Upload", "Error uploading image", e)
        null
    }
}
