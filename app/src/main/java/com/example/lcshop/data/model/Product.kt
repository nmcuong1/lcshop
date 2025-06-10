package com.example.lcshop.data.model

data class Product(
    val id: Int,
    val product_name: String,
    val category_id: Int,
    val price: Int,
    val description: String,
    val img_url: String,
    val status: Int,
    val created_at: String,
    val deleted_at: String?
)
data class ProductResponse(
    val success: Boolean,
    val data: List<Product>
)