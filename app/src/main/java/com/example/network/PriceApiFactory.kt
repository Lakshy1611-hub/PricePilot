package com.example.network

import com.example.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object PriceApiFactory {
    fun create(): PriceApiService {
        val baseUrl = BuildConfig.PRICE_API_BASE_URL.trimEnd('/') + "/"
        require(baseUrl.startsWith("https://")) { "PRICE_API_BASE_URL must use HTTPS" }

        // Product search should fail fast instead of leaving the UI spinning for ~45s.
        // The backend can still return results normally, while transient/unavailable
        // providers surface a retryable error to the app.
        val client = OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(18, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .callTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .build()
            .create(PriceApiService::class.java)
    }
}
