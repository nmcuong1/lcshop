package com.example.lcshop.data.model


data class CartItem(
    val id: Int,
    val cart_id: Int,
    val product_id: Int,
    val product_variant_id: Int,
    val quantity: Int,
    val total_price: String,
    val created_at: String,
    val updated_at: String,
    val Product: ProductInCart,
    val ProductVariant: ProductVariant
)

data class ProductInCart(
    val id: Int,
    val product_name: String,
    val category_id: Int,
    val brand_id: Int,
    val price: String,
    val description: String,
    val status: Int,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String?,
    val images: List<ProductImage>
)

data class CartRequest(
    val product_id: Int,
    val product_variant_id: Int,
    val quantity: Int
)

//data class CartResponse(
//    val message: String,
//    val cartItem: CartItem
//)
data class CartResponse(
    val id: Int,
    val user_id: Int,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String?,
    val CartItems: List<CartItem>
)


data class CartItemUpdateRequest(
    val cart_item_id: Int,
    val quantity: Int
)