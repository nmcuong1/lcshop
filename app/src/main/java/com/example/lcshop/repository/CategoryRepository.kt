package com.example.lcshop.repository


import com.example.lcshop.api.CategoryApiService
import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository {
    private val api = RetrofitInstance.categoryApi

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllCategories()
            if (response.isSuccessful) {
                response.body()?.categories ?: emptyList()
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
