// RetrofitInstance.kt
package com.example.lcshop.config

import com.example.lcshop.AuthApi
import com.example.lcshop.ProductApi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log body cho debug
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofitAuth: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()) // Thêm cho AuthApi
            .client(client)
            .build()
    }

    private val retrofitProduct: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.PRODUCT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()) // Thêm cho ProductApi
            .client(client)
            .build()
    }

    val api: AuthApi by lazy {
        retrofitAuth.create(AuthApi::class.java)
    }

    val productApi: ProductApi by lazy {
        retrofitProduct.create(ProductApi::class.java)
    }
}