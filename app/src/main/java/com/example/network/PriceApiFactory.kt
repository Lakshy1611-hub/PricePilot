package com.example.network

import com.example.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object PriceApiFactory {
    fun create(): PriceApiService {
        val baseUrl = BuildConfig.PRICE_API_BASE_URL.trimEnd('/') + "/"
        require(baseUrl.startsWith("https://")) { "PRICE_API_BASE_URL must use HTTPS" }

        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .build()
            .create(PriceApiService::class.java)
    }
}
