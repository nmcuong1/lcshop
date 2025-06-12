// ProductRepository.kt
package com.example.lcshop.repository

import android.util.Log
import com.example.lcshop.data.model.Product
import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.data.model.ProductCreateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

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

    suspend fun createProduct(product: ProductCreateRequest): Product? = withContext(Dispatchers.IO) {
        try {
            val response = productApi.createProduct(product)
            if (response.isSuccessful) {
                response.body()?.product
            } else {
                // Log lỗi chi tiết nếu cần
                println("❌ Failed to create product: ${response.code()} - ${response.message()}")
                Log.d("ProductRepository", "Failed to create product: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun updateProduct(id: Int, product: ProductCreateRequest): Response<Product> {
        return productApi.updateProduct(id, product)
    }

//    suspend fun updateProduct(id: Int, product: ProductCreateRequest): Product? = withContext(Dispatchers.IO) {
//        try {
//            val response = productApi.updateProduct(id, product)
//            if (response.isSuccessful) response.body() else null
//        } catch (e: Exception) {
//            null
//        }
//    }

    suspend fun deleteProduct(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = productApi.deleteProduct(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

