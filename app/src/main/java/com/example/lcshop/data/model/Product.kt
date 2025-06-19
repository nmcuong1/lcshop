    package com.example.lcshop.data.model


    data class CreateProductResponse(
        val message: String,
        val fullProduct: Product
    )
    data class ProductImage(
        val id: Int,
        val image_url: String,
        val is_primary: Boolean
    )

    data class Product(
        val id: Int? = null,
        val product_name: String,
        val category_id: Int? = null,
        val brand_id: Int? = null,
        val img_url: String? = null,
        val price: Int? = null,
        val description: String? = null,
        val status: Int? = 1,
        val created_at: String? = null,
        val updated_at: String? = null,
        val deleted_at: String? = null,
        val images: List<ProductImage> = emptyList(),
        val category: Category? = null,
        val brandss: Brand? = null, // Lưu ý: JSON dùng "brandss" thay vì "brand"
        val variants: List<ProductVariant>? = emptyList()
    )




    data class ProductCreateRequest(
        val product_name: String,
        val price: Int? = null,
        val description: String?,
        val category_id: Int,
        val brand_id: Int,
        val img_url: String,
        val variants: List<ProductVariant>
    )
