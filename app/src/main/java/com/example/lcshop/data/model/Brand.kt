package com.example.lcshop.data.model

data class Brand(
    val id: Int,
    val brand_name: String
)

data class BrandResponse(
    val brands: List<Brand>,
    val message: String
)