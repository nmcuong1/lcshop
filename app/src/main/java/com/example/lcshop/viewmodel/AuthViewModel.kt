package com.example.lcshop.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lcshop.data.AuthResponse

import com.example.lcshop.data.LoginRequest
import com.example.lcshop.data.RegisterRequest
import com.example.lcshop.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val authResponse = MutableLiveData<AuthResponse?>()
    val error = MutableLiveData<String?>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    authResponse.value = response.body()
                    error.value = null
                    Log.d("AuthViewModel", "Đăng nhập thành công: ${authResponse.value}")
                } else {
                    error.value = response.message() ?: "Lỗi không xác định"
                }
            } catch (e: Exception) {
                error.value = e.message ?: "Lỗi kết nối"
            }
        }
    }
    fun register(
        username: String,
        email: String,
        password: String,
        full_name: String,
        phone: String
    ) {
        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    full_name = full_name,
                    phone = phone
                )
                val response = repository.register(request)
                if (response.isSuccessful) {
                    authResponse.value = response.body()
                    error.value = null
                } else {
                    error.value = response.message() ?: "Lỗi không xác định"
                }
            } catch (e: Exception) {
                error.value = e.message ?: "Lỗi kết nối"
            }
        }
    }

}