// RetrofitInstance.kt
package com.example.lcshop.config

import android.content.Context
import com.example.lcshop.AuthApi
import com.example.lcshop.ProductApi
import com.example.lcshop.api.BrandApiService
import com.example.lcshop.api.CartApi
import com.example.lcshop.api.CategoryApiService
import com.example.lcshop.api.ProductVariantsApi
import com.example.lcshop.data.AuthInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log body cho debug
    }
    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

//    private val client = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(context)) // Thêm AuthInterceptor
            .build()
    }

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

    private val retrofitCart: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.CART_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }


    val api: AuthApi by lazy {
        retrofitAuth.create(AuthApi::class.java)
    }

    val productApi: ProductApi by lazy {
        retrofitProduct.create(ProductApi::class.java)
    }

    val brandApi: BrandApiService by lazy {
        retrofitProduct.create(BrandApiService::class.java)
    }

    val categoryApi: CategoryApiService by lazy {
        retrofitProduct.create(CategoryApiService::class.java)
    }

    val cartApi: CartApi by lazy {
        retrofitCart.create(CartApi::class.java)
    }

    val productVariantsApi: ProductVariantsApi by lazy {
        retrofitProduct.create(ProductVariantsApi::class.java)
    }
}