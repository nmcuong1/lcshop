package com.example.lcshop.api

import com.example.lcshop.data.model.Brand
import com.example.lcshop.data.model.BrandResponse
import retrofit2.Response
import retrofit2.http.GET

interface BrandApiService {

    @GET("brands")
    suspend fun getAllBrands(): Response<BrandResponse>
}
