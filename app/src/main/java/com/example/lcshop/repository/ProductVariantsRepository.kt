package com.example.lcshop.repository

import com.example.lcshop.api.ProductVariantsApi
import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.data.model.ProductVariant

class ProductVariantsRepository {
    private val productVariantsApi: ProductVariantsApi = RetrofitInstance.productVariantsApi

    suspend fun getProductVariants(): List<ProductVariant> {
        return productVariantsApi.getProductVariants().productVariants
    }

    suspend fun addProductVariant(productVariant: ProductVariant): ProductVariant {
        return productVariantsApi.addProductVariant(productVariant)
    }

    suspend fun updateProductVariant(id: Int, productVariant: ProductVariant): ProductVariant {
        return productVariantsApi.updateProductVariant(id, productVariant)
    }

    suspend fun deleteProductVariant(id: Int) {
        productVariantsApi.deleteProductVariant(id)
    }
    // Các phương thức khác (getAllProducts, getProductById, v.v.) giữ nguyên
}