// CartApi Interface
package com.example.lcshop.api

import com.example.lcshop.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface CartApi {
    @GET("cart")
    suspend fun getCart(): CartResponse

    @POST("cart")
    suspend fun addToCart(@Body request: CartRequest): Response<CartItemResponse>

    @PUT("cart")
    suspend fun updateCartItem(@Body request: CartRequestUpdate): Response<CartItemResponse>

//    @DELETE("cart/remove/{cart_item_id}")
//    suspend fun removeFromCart(@Path("cart_item_id") cartItemId: Int): Response<Any>
    @DELETE("cart/{id}")
    suspend fun removeFromCart(@Path("id") cartItemId: Int): Response<Message>

}
