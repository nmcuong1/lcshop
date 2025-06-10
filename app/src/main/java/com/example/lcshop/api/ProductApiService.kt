package com.example.lcshop

import com.example.lcshop.data.model.ProductResponse

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.*

interface ProductApi {
    @GET("products") // Adjust if your endpoint is different, e.g., "api/products"
    suspend fun getAllProducts(): ProductResponse
//    suspend fun getAllProducts(
//        @Header("Authorization") token: String
//    ): List<Product>
    //fun getProducts(): Call<List<Product>>
//    @GET("products/{id}")
//    suspend fun getProductById(@Path("id") id: Int): Response<ProductResponse>
//
//    @POST("products")
//    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductResponse>

//    @PUT("products/{id}")
//    suspend fun updateProduct(
//        @Path("id") id: Int,
//        @Body request: UpdateProductRequest
//    ): Response<ProductResponse>
//
//    @DELETE("products/{id}")
//    suspend fun deleteProduct(@Path("id") id: Int): Response<ApiResponse<String>>

}