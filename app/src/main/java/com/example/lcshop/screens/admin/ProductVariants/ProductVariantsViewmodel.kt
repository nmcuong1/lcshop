package com.example.lcshop.screens.admin.ProductVariants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lcshop.data.model.ProductVariant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.lcshop.repository.ProductVariantsRepository

class ProductVariantsViewModel(private val productVariantsRepository: ProductVariantsRepository) : ViewModel() {
    private val _productVariants = MutableStateFlow<List<ProductVariant>>(emptyList())
    val productVariants: StateFlow<List<ProductVariant>> = _productVariants

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getProductVariants() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val variants = productVariantsRepository.getProductVariants()
                _productVariants.value = variants
                Log.d("ProductVariantsViewModel", "Product variants loaded: $variants")
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải biến thể sản phẩm: ${e.message}"
                Log.e("ProductVariantsViewModel", "Lỗi: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun addProductVariant(productVariant: ProductVariant) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val newVariant = productVariantsRepository.addProductVariant(productVariant)
                val currentVariants = _productVariants.value.toMutableList()
                currentVariants.add(newVariant)
                _productVariants.value = currentVariants
                Log.d("ProductVariantsViewModel", "Product variant added: $newVariant")
            } catch (e: Exception) {
                _error.value = "Lỗi khi thêm biến thể: ${e.message}"
                Log.e("ProductVariantsViewModel", "Lỗi: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProductVariant(id: Int, productVariant: ProductVariant) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val updatedVariant = productVariantsRepository.updateProductVariant(id, productVariant)
                val currentVariants = _productVariants.value.toMutableList()
                val index = currentVariants.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentVariants[index] = updatedVariant
                    _productVariants.value = currentVariants
                }
                Log.d("ProductVariantsViewModel", "Product variant updated: $updatedVariant")
            } catch (e: Exception) {
                _error.value = "Lỗi khi cập nhật biến thể: ${e.message}"
                Log.e("ProductVariantsViewModel", "Lỗi: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProductVariant(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                productVariantsRepository.deleteProductVariant(id)
                val currentVariants = _productVariants.value.toMutableList()
                currentVariants.removeAll { it.id == id }
                _productVariants.value = currentVariants
                Log.d("ProductVariantsViewModel", "Product variant deleted: $id")
            } catch (e: Exception) {
                _error.value = "Lỗi khi xóa biến thể: ${e.message}"
                Log.e("ProductVariantsViewModel", "Lỗi: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class ProductVariantsViewModelFactory(private val productVariantsRepository: ProductVariantsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductVariantsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductVariantsViewModel(productVariantsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}