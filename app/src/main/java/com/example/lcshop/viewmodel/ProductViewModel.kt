// ProductViewModel.kt
package com.example.lcshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lcshop.data.model.CartItem
import com.example.lcshop.data.model.Product
import com.example.lcshop.data.model.ProductCreateRequest
import com.example.lcshop.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _productDetail = MutableStateFlow<Product?>(null)
    val productDetail: StateFlow<Product?> = _productDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

     val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        refreshProducts()
    }

    fun refreshProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val productsList = repository.getProducts()
                _products.value = productsList

                Log.d("ProductViewModel", "Sản phẩm: ${productsList.toString()} sản phẩm đã được tải")
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải sản phẩm: ${e.message}"
                Log.e("ProductViewModel", "Lỗi: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getProductById(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val product = repository.getProductById(productId)
                _productDetail.value = product
                Log.d("ProductViewModel", "Sản phẩm ID $productId đã được tải")
                Log.d("ProductViewModel","${product.toString()}")
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải chi tiết sản phẩm: ${e.message}"
                Log.e("ProductViewModel", "Lỗi: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun createProduct(request: ProductCreateRequest, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val createdProduct = repository.createProduct(request)
                if (createdProduct != null) {
                    // Cập nhật danh sách sản phẩm nếu cần
                    _products.value = _products.value + createdProduct
                    onSuccess()
                    Log.d("ProductViewModel", "✅ Tạo sản phẩm thành công: ${createdProduct.product_name}")
                } else {
                    _error.value = "Tạo sản phẩm thất bại"
                    Log.e("ProductViewModel", "❌ Tạo sản phẩm thất bại")
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tạo sản phẩm: ${e.message}"
                Log.e("ProductViewModel", "❌ Lỗi: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProduct(id: Int, product: ProductCreateRequest, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updateProduct(id, product)
                if (response.isSuccessful) {
                    _productDetail.value = response.body()
                    _error.value = null
                    onComplete(true)
                } else {
                    _error.value = "Lỗi khi cập nhật: ${response.message()}"
                    onComplete(false)
                }
            } catch (e: Exception) {
                _error.value = "Lỗi kết nối: ${e.message}"
                onComplete(false)
            } finally {
                _isLoading.value = false
            }
        }
    }


}

