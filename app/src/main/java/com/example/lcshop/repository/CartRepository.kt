package com.example.lcshop.repository

import com.example.lcshop.api.CartApi
import com.example.lcshop.data.model.CartResponse
import com.example.lcshop.data.model.CartRequest
import com.example.lcshop.data.model.CartItemUpdateRequest

class CartRepository(private val api: CartApi) {
    suspend fun getCart(): CartResponse {
        return api.getCart()
    }

//    suspend fun addToCart(request: CartRequest) {
//        return api.addToCart(request)
//    }
//
//    suspend fun updateCartItem(request: CartItemUpdateRequest) {
//        return api.updateCartItem(request)
//    }
//
//    suspend fun removeFromCart(cartItemId: Int) {
//        return api.removeFromCart(cartItemId)
//    }
}