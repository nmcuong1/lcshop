package com.example.lcshop

import com.example.lcshop.data.AuthResponse
import com.example.lcshop.data.LoginRequest
import com.example.lcshop.data.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}