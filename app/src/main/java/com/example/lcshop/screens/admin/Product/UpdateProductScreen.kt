package com.example.lcshop.screens.admin.Product

import BrandsRepository
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.lcshop.config.Constants.BASE_URL_IMG
import com.example.lcshop.data.model.Brand
import com.example.lcshop.data.model.Category
import com.example.lcshop.data.model.ProductCreateRequest
import com.example.lcshop.data.model.ProductVariantRequest
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

    // Khai báo state
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedBrand by remember { mutableStateOf<Brand?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Launcher để chọn ảnh
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uploadedImageUrl = null
        errorMessage = null
    }


    // Khởi tạo ViewModel
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
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    // Tải dữ liệu khi khởi tạo
    LaunchedEffect(productId) {
        coroutineScope.launch {
            brandsViewModel.fetchBrands()
            categoriesViewModel.fetchCategories()
        }
        productViewModel.getProductById(productId)
    }
    // Cập nhật state khi có dữ liệu
    LaunchedEffect(product) {
        product?.let {
            productName = it.product_name ?: ""
            price = it.price.toString()
            description = it.description ?: ""
            selectedCategory = categories.firstOrNull { category -> category.id == it.category_id }
            selectedBrand = brands.firstOrNull { brand -> brand.id == it.brand_id }
            uploadedImageUrl = it.images?.firstOrNull { img -> img.is_primary }?.image_url
Log.d("uploadedImageUrl","uploadedImageUrl: $uploadedImageUrl")
        }
    }
    // Giao diện
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cập nhật sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null || errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = error ?: errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Tên sản phẩm") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
                )
                CategoryDropdown(categories, selectedCategory) { selectedCategory = it }
                BrandDropdown(brands, selectedBrand) { selectedBrand = it }
                Box(
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clickable { imagePickerLauncher.launch("image/*") }
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Ảnh đã chọn",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (uploadedImageUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter("${BASE_URL_IMG}$uploadedImageUrl"),
                            contentDescription = "Ảnh đã upload",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(uploadedImageUrl),
                            contentDescription = "Ảnh mặc định",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Log.d("UpdateProductScreen1", "Hiển thị ảnh đã tải lên: $uploadedImageUrl")
                val variantList = remember { mutableStateListOf(ProductVariantRequest("", "", "", 0)) }

                variantList.forEachIndexed { index, variant ->
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = variant.color,
                            onValueChange = { variantList[index] = variant.copy(color = it) },
                            label = { Text("Màu sắc") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = variant.size,
                            onValueChange = { variantList[index] = variant.copy(size = it) },
                            label = { Text("Kích cỡ") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = variant.material,
                            onValueChange = { variantList[index] = variant.copy(material = it) },
                            label = { Text("Chất liệu") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = variant.stock_quantity.toString(),
                            onValueChange = {
                                variantList[index] = variant.copy(stock_quantity = it.toIntOrNull() ?: 0)
                            },
                            label = { Text("Số lượng") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row {
                            if (index > 0) {
                                Button(
                                    onClick = { variantList.removeAt(index) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Xóa")
                                }
                            }
                        }
                    }
                }

// Nút thêm biến thể
                Button(onClick = {
                    variantList.add(ProductVariantRequest("", "", "", 0))
                }) {
                    Text("Thêm biến thể")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (selectedCategory != null && selectedBrand != null) {
//                                val priceInt = price.toIntOrNull() ?: run {
//                                    errorMessage = "Giá phải là số hợp lệ"
//                                    return@launch
//                                }
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

                                val updatedProductRequest = ProductCreateRequest(
                                    product_name = productName,
                                    price = price.toInt(),
                                    description = description.ifEmpty { null },
                                    category_id = selectedCategory!!.id,
                                    brand_id = selectedBrand!!.id,
                                    image_url = uploadedImageUrl ?: "",

                                )
                                productViewModel.updateProduct(productId, updatedProductRequest) { success ->
                                    if (success) {
                                        onProductUpdated() // Hoàn tất sau khi cập nhật dữ liệu
                                        Log.d("UpdateProductScreen", "Cập nhật sản phẩm thành công")
                                    } else {
                                        errorMessage = "Cập nhật sản phẩm thất bại: ${productViewModel.error.value}"
                                    }
                                }
                            } else {
                                errorMessage = "Vui lòng chọn danh mục và thương hiệu"
                            }
                        }
                    },
                    enabled = selectedCategory != null && selectedBrand != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cập nhật sản phẩm")
                    Log.d("UpdateProductScreen", "Cập nhật sản phẩm: $productName, $price, $description, ${selectedCategory?.category_name}, ${selectedBrand?.brand_name}, $uploadedImageUrl")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selected: Category?,
    onSelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            value = selected?.category_name ?: "",
            onValueChange = {},
            label = { Text("Danh mục") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach {
                DropdownMenuItem(
                    text = { Text(it.category_name) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDropdown(
    brands: List<Brand>,
    selected: Brand?,
    onSelected: (Brand) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            value = selected?.brand_name ?: "",
            onValueChange = {},
            label = { Text("Thương hiệu") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            brands.forEach {
                DropdownMenuItem(
                    text = { Text(it.brand_name) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}