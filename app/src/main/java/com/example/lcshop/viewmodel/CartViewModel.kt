package com.example.lcshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lcshop.data.model.*
import com.example.lcshop.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    private val _cart = MutableStateFlow<CartResponse?>(null)
    val cart: StateFlow<CartResponse?> = _cart

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _addToCartSuccess = MutableStateFlow(false)
    val addToCartSuccess: StateFlow<Boolean> = _addToCartSuccess


    init {
        fetchCart()
    }

    fun resetAddToCartSuccess() {
        _addToCartSuccess.value = false
    }

    fun fetchCart() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getCart()
                _cart.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("CartViewModel", "fetchCart error", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun addToCart(request: CartRequest) {
        viewModelScope.launch {
            try {
                repository.addToCart(request)
                _addToCartSuccess.value = true
                fetchCart()
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("CartViewModel", "addToCart error", e)
            }
        }
    }
//
    fun updateCartItem(request: CartRequestUpdate) {
        viewModelScope.launch {
            try {
                repository.updateCartItem(request)
                fetchCart() // Refresh cart after updating
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("CartViewModel", "updateCartItem error", e)
            }
        }
    }

    fun removeFromCart(cartItemId: Int) {
        viewModelScope.launch {
            try {
                repository.removeFromCart(cartItemId)
                fetchCart() // Refresh cart after removing
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("CartViewModel", "removeFromCart error", e)
            }
        }
    }
}

class CartViewModelFactory(private val repository: CartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}