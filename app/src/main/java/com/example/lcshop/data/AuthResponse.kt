package com.example.lcshop.data
// AuthResponse.kt


data class AuthResponse(
    val user: User,
    val token: String
)

data class AuthData(
    val user: User,
    val token: String
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val full_name: String,
    val phone: String,
    val address:String,
    val role_id: Int
)
