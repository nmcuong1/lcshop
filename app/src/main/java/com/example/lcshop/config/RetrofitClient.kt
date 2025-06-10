package com.example.lcshop.config

// RetrofitInstance.kt

import com.example.lcshop.AuthApi
import com.example.lcshop.ProductApi
import com.example.lcshop.config.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val productApi: ProductApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.PRODUCT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApi::class.java)
    }
}