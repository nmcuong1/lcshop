package com.example.lcshop.repository


import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.data.LoginRequest
import com.example.lcshop.data.RegisterRequest

class AuthRepository {
    suspend fun login(request: LoginRequest) = RetrofitInstance.api.login(request)
    suspend fun register(request: RegisterRequest) = RetrofitInstance.api.register(request)
}