package com.example.lcshop.screens.admin.Brands

import BrandsRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lcshop.data.model.Brand
import com.example.lcshop.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BrandsAdminViewModel( private val repository: BrandsRepository) : ViewModel() {

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands


    init {
        fetchBrands()
    }

    fun fetchBrands() {
        viewModelScope.launch {
            try {
                val result = repository.getBrands()
                if (result != null) {
                    _brands.value = result
                } else {
                    // Xử lý nếu null
                    _brands.value = emptyList()
                }
                Log.d("BrandsAdminViewModel", "Fetched brands: ${_brands.value.size} items")
            } catch (e: Exception) {
                e.printStackTrace()
                _brands.value = emptyList()
            }
        }
    }

//    fun addBrand(name: String, onSuccess: () -> Unit = {}) {
//        viewModelScope.launch {
//            try {
//                val newBrand = Brand(id = 0, name = name) // ID sẽ được backend gán
//                val response = repository.createBrand(newBrand)
//                if (response != null) {
//                    fetchBrands() // Refresh danh sách sau khi thêm
//                    onSuccess()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun deleteBrand(brandId: Int, onSuccess: () -> Unit = {}) {
//        viewModelScope.launch {
//            try {
//                val isDeleted = repository.deleteBrand(brandId)
//                if (isDeleted) {
//                    fetchBrands()
//                    onSuccess()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}
class BrandsAdminViewModelFactory(
    private val repository: BrandsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrandsAdminViewModel::class.java)) {
            return BrandsAdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
