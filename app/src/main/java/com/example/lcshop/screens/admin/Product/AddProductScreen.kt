package com.example.lcshop.screens.admin.Product

import BrandsRepository
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.lcshop.data.model.Brand
import com.example.lcshop.data.model.Category
import com.example.lcshop.data.model.ProductCreateRequest
import com.example.lcshop.repository.CategoryRepository
import com.example.lcshop.repository.ProductRepository
import com.example.lcshop.repository.UploadRepository
import com.example.lcshop.screens.admin.Brands.BrandsAdminViewModel
import com.example.lcshop.screens.admin.Brands.BrandsAdminViewModelFactory
import com.example.lcshop.screens.admin.Categories.CategoriesAdminViewModel
import com.example.lcshop.screens.admin.Categories.CategoriesAdminViewModelFactory
import com.example.lcshop.viewmodel.ProductViewModel
import com.example.lcshop.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onProductAdded: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var productName by remember { mutableStateOf("Aó dài") }
    var price by remember { mutableStateOf("200000") }
    var description by remember { mutableStateOf("hahahah") }

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedBrand by remember { mutableStateOf<Brand?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uploadedImageUrl = null // Reset lại URL khi chọn ảnh mới
    }

    val brandsViewModel: BrandsAdminViewModel = viewModel(
        factory = BrandsAdminViewModelFactory(BrandsRepository())
    )

    val categoriesViewModel: CategoriesAdminViewModel = viewModel(
        factory = CategoriesAdminViewModelFactory(CategoryRepository())
    )

    val brands by brandsViewModel.brands.collectAsState()
    val categories by categoriesViewModel.categories.collectAsState()

    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(ProductRepository())
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

            // Ảnh sản phẩm
            Box(
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clickable { imagePickerLauncher.launch("image/*") },
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Chọn ảnh...", modifier = Modifier.align(Alignment.Center))
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        // 1. Gửi dữ liệu tạo sản phẩm trước (chưa có ảnh)
                        if (selectedCategory != null && selectedBrand != null) {
                            val productRequest = ProductCreateRequest(
                                product_name = productName,
                                price = price.toInt(),
                                description = description.ifEmpty { null },
                                category_id = selectedCategory!!.id,
                                brand_id = selectedBrand!!.id,
                                image_url = "" // Gửi null hoặc bỏ qua nếu backend cho phép
                            )

                            val productRepo = ProductRepository()
                            val createdProduct = productRepo.createProduct(productRequest)
                            Log.d("AddProduct", "Tạo sản phẩm: $productRequest")

                            val productId = createdProduct?.id

                            Log.d("AddProduct", "Tạo sản phẩm: $createdProduct")
                            if (productId != null) {
                                Log.d("AddProduct", "Tạo sản phẩm thành công: ${createdProduct.id}")

                                // 2. Upload ảnh nếu đã chọn ảnh
                                selectedImageUri?.let { uri ->
                                    val file = com.example.lcshop.util.FileUtils.getFileFromUri(
                                        context,
                                        uri
                                    )
                                    file?.let {
                                        // Kiểm tra định dạng file
                                        val mimeType = context.contentResolver.getType(uri)
                                        if (mimeType == null || !listOf(
                                                "image/jpeg",
                                                "image/jpg",
                                                "image/png",
                                                "image/gif"
                                            ).contains(mimeType)
                                        ) {
                                            Log.e(
                                                "Upload",
                                                "Định dạng file không hỗ trợ: $mimeType. Chỉ hỗ trợ jpeg, jpg, png, gif."
                                            )
                                            return@let
                                        }

                                        val requestFile = it.asRequestBody(
                                            mimeType?.toMediaTypeOrNull()
                                                ?: "image/jpeg".toMediaTypeOrNull()
                                        )
                                        val imagePart = MultipartBody.Part.createFormData(
                                            "image",
                                            it.name,
                                            requestFile
                                        )

                                        val uploadRepo = UploadRepository(context)
                                        val uploadResponse =
                                            uploadRepo.uploadImage(productId, imagePart)

                                        Log.d("uploadResponse", "Response: $uploadResponse")

                                        if (uploadResponse.isSuccessful) {
                                            uploadedImageUrl = uploadResponse.body()?.imageUrl
                                            Log.d(
                                                "Upload",
                                                "Upload ảnh thành công: $uploadedImageUrl"
                                            )
                                        } else {
                                            Log.e(
                                                "Upload",
                                                "Upload thất bại: ${
                                                    uploadResponse.errorBody()?.string()
                                                }"
                                            )
                                        }
                                    }
                                }
                                onProductAdded()
                            } else {
                                Log.e("AddProduct", "Tạo sản phẩm thất bại.")
                            }
                        } else {
                            Log.e("AddProduct", "Thiếu thông tin danh mục hoặc thương hiệu.")
                        }
                    }
                },
                enabled = selectedCategory != null && selectedBrand != null
            ) {
                Text("Thêm sản phẩm")
            }
        }
    }}


