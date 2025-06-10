package com.example.lschop.repository

import android.content.Context
import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.data.model.Product

class ProductRepository() {
    private val apiService = RetrofitInstance.productApi
   // private val preferenceManager = PreferenceManager(context)

    suspend fun getProducts(): List<Product> {
       // val token = preferenceManager.getUserToken()
//        if (token.isNullOrEmpty()) {
//            throw Exception("Token is null or empty")
//        }

//        return apiService.getAllProducts("Bearer $token")
        return apiService.getAllProducts().data
    }
}

