package com.example.cryptoexchange.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (apiKey.isNotBlank()) {
            builder.addHeader("x-cg-demo-api-key", apiKey)
        }
        return chain.proceed(builder.build())
    }
}