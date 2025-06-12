package com.example.lcshop.api

import com.example.lcshop.data.model.ProductVariant
import com.example.lcshop.data.model.ProductVariantResponse
import retrofit2.http.*

interface ProductVariantsApi {
    @GET("product-variants")
    suspend fun getProductVariants(): ProductVariantResponse

    @POST("product-variants")
    suspend fun addProductVariant(@Body productVariant: ProductVariant): ProductVariant

    @PUT("product-variants/{id}")
    suspend fun updateProductVariant(
        @Path("id") id: Int,
        @Body productVariant: ProductVariant
    ): ProductVariant

    @DELETE("product-variants/{id}")
    suspend fun deleteProductVariant(@Path("id") id: Int): Unit
}