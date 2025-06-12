package com.example.lcshop.data

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import com.example.lcshop.util.PreferenceManager // Giả sử bạn dùng PreferenceManager
import kotlinx.coroutines.runBlocking

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val preferenceManager = PreferenceManager(context)
        val token = preferenceManager.getUserToken() // Lấy token từ PreferenceManager
        Log.d("AuthInterceptor", "Token used: $token")
        val modifiedRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(modifiedRequest)
    }
}