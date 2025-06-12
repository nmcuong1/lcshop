    package com.example.lcshop.data.model

    import com.google.gson.annotations.SerializedName

//    data class ProductImage(
//        val id: Int,
//        @SerializedName("image_url") val imageUrl: String,
//        @SerializedName("is_primary") val isPrimary: Boolean
//    )
//
//    data class Product(
//        val id: Int? = null,
//        @SerializedName("product_name") val productName: String,
//        @SerializedName("category_id") val categoryId: Int? = null,
//        @SerializedName("brand_id") val brandId: Int? = null,
//        @SerializedName("img_url") val imgUrl: String? = null,
//        val price: String, // Sử dụng String vì JSON trả về dạng "1299000.00"
//        val description: String? = null,
//        val status: Int? = 1,
//        @SerializedName("created_at") val createdAt: String? = null,
//        @SerializedName("updated_at") val updatedAt: String? = null,
//        @SerializedName("deleted_at") val deletedAt: String? = null,
//        val images: List<ProductImage> = emptyList(),
//        val category: Category? = null,
//        @SerializedName("brandss") val brandss: Brand? = null, // Sửa "brand" thành "brandss"
//        @SerializedName("ProductVariants") val productVariants: List<ProductVariant>? = emptyList()
//    )
//
//    data class ProductCreateRequest(
//        @SerializedName("product_name") val productName: String,
//        val price: String,
//        val description: String?,
//        @SerializedName("category_id") val categoryId: Int,
//        @SerializedName("brand_id") val brandId: Int,
//        @SerializedName("image_url") val imageUrl: String? = null // Có thể null nếu upload sau
//    )

    data class CreateProductResponse(
        val message: String,
        val product: Product
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
        val ProductVariants: List<ProductVariant>? = emptyList()
    )


    data class ProductCreateRequest(
        val product_name: String,
        val price: Int? = null,
        val description: String?,
        val category_id: Int,
        val brand_id: Int,
        val image_url: String, // ảnh chính, có thể từ productImage.image_url
        //val productVariants: List<ProductVariantRequest>
    )
