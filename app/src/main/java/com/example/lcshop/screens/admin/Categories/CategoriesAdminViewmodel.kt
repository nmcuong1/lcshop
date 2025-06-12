package com.example.lcshop.screens.admin.Categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lcshop.data.model.Category
import com.example.lcshop.repository.CategoryRepository
import com.example.lcshop.repository.ProductRepository
import com.example.lcshop.screens.admin.Brands.BrandsAdminViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriesAdminViewModel( private val repository: CategoryRepository) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    //private val repository = CategoryRepository()

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = repository.getCategories()
                if (result != null) {
                    _categories.value = result
                } else {
                    _categories.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _categories.value = emptyList()
            }
        }
    }

//    fun addCategory(name: String, onSuccess: () -> Unit = {}) {
//        viewModelScope.launch {
//            try {
//                val newCategory = Category(id = 0, name = name) // ID do server gán
//                val response = repository.createCategory(newCategory)
//                if (response != null) {
//                    fetchCategories()
//                    onSuccess()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun deleteCategory(categoryId: Int, onSuccess: () -> Unit = {}) {
//        viewModelScope.launch {
//            try {
//                val isDeleted = repository.deleteCategory(categoryId)
//                if (isDeleted) {
//                    fetchCategories()
//                    onSuccess()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}

class CategoriesAdminViewModelFactory(
    private val repository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesAdminViewModel::class.java)) {
            return CategoriesAdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
