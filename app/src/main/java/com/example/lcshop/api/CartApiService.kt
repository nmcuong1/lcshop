// CartApi Interface
package com.example.lcshop.api

import com.example.lcshop.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface CartApi {
    @GET("cart")
    suspend fun getCart(): CartResponse

//    @POST("cart/add")
//    suspend fun addToCart(@Body request: CartRequest): Response<Any>
//
//    @PUT("cart/update")
//    suspend fun updateCartItem(@Body request: CartItemUpdateRequest): Response<Any>

//    @DELETE("cart/remove/{cart_item_id}")
//    suspend fun removeFromCart(@Path("cart_item_id") cartItemId: Int): Response<Any>

}
