// ProductRepository.kt
package com.example.lcshop.repository

import com.example.lcshop.data.model.Product
import com.example.lcshop.config.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository {
    private val productApi = RetrofitInstance.productApi

    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val response = productApi.getAllProducts()
            if (response.isSuccessful) {
                response.body() ?: emptyList() // Trả về trực tiếp mảng Product
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getProductById(id: Int): Product? = withContext(Dispatchers.IO) {
        try {
            val response = productApi.getProductById(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createProduct(product: Product): Product? = withContext(Dispatchers.IO) {
        try {
            val response = productApi.createProduct(product)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateProduct(id: Int, product: Product): Product? = withContext(Dispatchers.IO) {
        try {
            val response = productApi.updateProduct(id, product)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteProduct(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = productApi.deleteProduct(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}