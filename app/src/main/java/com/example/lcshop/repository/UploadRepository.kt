package com.example.lcshop.repository



import android.content.Context
import com.example.lcshop.config.RetrofitInstance
import com.example.lcshop.data.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class UploadRepository(private val context: Context) {
    private val api = RetrofitInstance.productApi

    suspend fun uploadImage(productId: Int, imagePart: MultipartBody.Part, isPrimary: Boolean = true): Response<UploadResponse> {
        val productIdPart = createRequestBody(productId.toString())
        val isPrimaryPart = createRequestBody(isPrimary.toString())
        return api.uploadImage(imagePart, productIdPart, isPrimaryPart)
    }

    private fun createRequestBody(value: String): RequestBody {
        return RequestBody.create(MultipartBody.FORM, value)
    }
}
