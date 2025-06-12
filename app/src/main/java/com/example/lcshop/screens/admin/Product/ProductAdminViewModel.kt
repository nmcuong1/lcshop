//package com.example.lcshop.viewmodel
//
//import BrandsRepository
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.lcshop.data.model.Brand
//import com.example.lcshop.data.model.Category
//import com.example.lcshop.data.model.Product
//import com.example.lcshop.data.model.ProductCreateRequest
//import com.example.lcshop.repository.CategoryRepository
//import com.example.lcshop.repository.ProductRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class ProductAdminViewModel(private val repository: ProductRepository) : ViewModel() {
//
//    private val brandRepo = BrandsRepository()
//    private val categoryRepo = CategoryRepository()
//
//    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
//    val brands: StateFlow<List<Brand>> = _brands
//
//    private val _categories = MutableStateFlow<List<Category>>(emptyList())
//    val categories: StateFlow<List<Category>> = _categories
//
//    private val _products = MutableStateFlow<List<Product>>(emptyList())
//    val products: StateFlow<List<Product>> = _products
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error
//
//    private val _isProductCreated = MutableStateFlow(false)
//    val isProductCreated: StateFlow<Boolean> = _isProductCreated
//
//    fun createProduct(request: ProductCreateRequest, onSuccess: () -> Unit = {}) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//            try {
//                val product = repository.createProduct(request)
//                if (product != null) {
//                    _isProductCreated.value = true
//                    Log.d("ProductViewModel", "Product created: ${product.product_name}")
//                    onSuccess()
//                } else {
//                    _error.value = "Tạo sản phẩm thất bại"
//                }
//            } catch (e: Exception) {
//                _error.value = "Lỗi khi tạo sản phẩm: ${e.message}"
//                Log.e("ProductViewModel", "Error: ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    init {
//        loadBrands()
//        loadCategories()
//        refreshProducts()
//    }
//
//    private fun loadBrands() {
//        viewModelScope.launch {
//            _brands.value = brandRepo.getBrands()
//        }
//    }
//
//    private fun loadCategories() {
//        viewModelScope.launch {
//            _categories.value = categoryRepo.getCategories()
//        }
//    }
//
//    fun refreshProducts() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//            try {
//                val productsList = repository.getProducts()
//                _products.value = productsList
//                Log.d("ProductViewModel", "Products loaded: ${productsList.size}")
//            } catch (e: Exception) {
//                _error.value = "Lỗi khi tải sản phẩm: ${e.message}"
//                Log.e("ProductViewModel", "Error: ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//
//    class ProductAdminViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            if (modelClass.isAssignableFrom(ProductAdminViewModel::class.java)) {
//                @Suppress("UNCHECKED_CAST")
//                return ProductAdminViewModel(repository) as T
//            }
//            throw IllegalArgumentException("Unknown ViewModel class")
//        }
//    }
//
//    companion object {
//        fun Factory(repository: ProductRepository) = ProductAdminViewModelFactory(repository)
//    }
//}
