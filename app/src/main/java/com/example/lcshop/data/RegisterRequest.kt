package com.example.lcshop.data

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val full_name: String,
    val phone: String
)