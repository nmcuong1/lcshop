package com.example.lcshop

import com.example.lcshop.data.model.Product


import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.*

interface ProductApi {

    @GET("products")
    suspend fun getAllProducts(
        @Query("category_id") categoryId: Int? = null,
        @Query("brand_id") brandId: Int? = null,
        @Query("search") search: String? = null,
        @Query("status") status: Int? = null
    ): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Response<Product>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Void>

}