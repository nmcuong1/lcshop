package com.example.lcshop.data.model

//data class Product(
//    val id: Int? = null,
//    val product_name: String,
//    val category_id: Int? = null,
//    val brand_id: Int? = null,
//    val img_url: String? = null,
//    val price: Double,
//    val description: String? = null,
//    val status: Int? = 1,
//    val created_at: String? = null,
//    val updated_at: String? = null,
//    val deleted_at: String? = null
//)
data class Product(
    val id: Int? = null,
    val product_name: String,
    val category_id: Int? = null,
    val brand_id: Int? = null,
    val img_url: String? = null,
    val price: String, // Sử dụng String vì JSON trả về dạng "1299000.00"
    val description: String? = null,
    val status: Int? = 1,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val category: Category? = null,
    val brandss: Brand? = null, // Lưu ý: JSON dùng "brandss" thay vì "brand"
    val ProductVariants: List<ProductVariant>? = emptyList()
)
//data class ProductResponse(
//    val total: Int,
//    val pages: Int,
//    val currentPage: Int,
//    val hasMore: Boolean,
//    val products: List<Product>
//)
//data class ProductResponse(
//    val total: Int,
//    val pages: Int,
//    val currentPage: Int,
//    val products: List<Product>
//)