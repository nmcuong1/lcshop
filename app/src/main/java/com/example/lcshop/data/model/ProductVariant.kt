package com.example.lcshop.data.model

data class ProductVariant(
    val id: Int ?=null,
    val color: String,
    val size: String,
    val material: String,
    val stock_quantity: Int
)

data class ProductVariantResponse(
    val productVariants: List<ProductVariant>
)
data class ProductVariantRequest(
    val color: String,
    val size: String,
    val material: String,
    val stock_quantity: Int
)