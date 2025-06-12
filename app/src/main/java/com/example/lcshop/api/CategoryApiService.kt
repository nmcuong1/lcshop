package com.example.lcshop.api

import com.example.lcshop.data.model.Category
import com.example.lcshop.data.model.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApiService {
    @GET("categories") // điều chỉnh nếu endpoint khác
    suspend fun getAllCategories(): Response<CategoryResponse>
}
