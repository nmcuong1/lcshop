package com.example.lcshop.viewmodel

// ProductViewModel.kt
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lcshop.data.model.Product
import com.example.lschop.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                _products.value = repository.getProducts()
                Log.e("ProductViewModel", "sản phẩm: ${_products.value.size} sản phẩm đã được tải")
            } catch (e: Exception) {
                // Log hoặc xử lý lỗi tùy ý
                Log.e("ProductViewModel", "Lỗi khi fetch sản phẩm: ${e.message}")
            }
        }
    }

}