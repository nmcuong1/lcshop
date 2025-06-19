package com.example.lcshop.repository

import com.example.lcshop.api.CartApi
import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.data.model.CartItem
import com.example.lcshop.data.model.CartResponse
import com.example.lcshop.data.model.CartRequest
import com.example.lcshop.data.model.CartRequestUpdate

class CartRepository(private val api: CartApi) {
    private val CartApi = RetrofitInstance.cartApi

    suspend fun getCart(): CartResponse {
        return api.getCart()
    }

    suspend fun addToCart( request: CartRequest): Result<CartItem> {
        return try {
            val response = api.addToCart(request)
            if (response.isSuccessful) {
                Result.success(response.body()!!.cartItem)
            } else {
                Result.failure(Exception("Add to cart failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
//
    suspend fun updateCartItem(request: CartRequestUpdate): Result<CartItem> {
        return try {
            val response = api.updateCartItem(request)
            if (response.isSuccessful) {
                Result.success(response.body()!!.cartItem)
            } else {
                Result.failure(Exception("Update failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun removeFromCart(cartItemId: Int): Result<String> {
        return try {
            val response = api.removeFromCart(cartItemId)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Đã xoá thành công"
                Result.success(message)
            } else {
                Result.failure(Exception("Xoá thất bại: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}

}