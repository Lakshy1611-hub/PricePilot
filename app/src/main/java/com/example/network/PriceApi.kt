package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class LivePriceSearchResponse(
    val products: List<LiveProductDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class LiveProductDto(
    val id: String? = null,
    val storeName: String,
    val productTitle: String,
    val productUrl: String,
    val imageUrl: String? = null,
    val currentPrice: Double,
    val originalPrice: Double? = null,
    val discount: Int? = null,
    val currency: String = "₹",
    val availability: String = "Unknown",
    val sellerName: String = "Unknown seller",
    val rating: Float? = null,
    val lastUpdated: String = "Just now",
    val matchConfidence: String = "Possible Match",
    val variantInfo: String = "Standard",
    val brand: String = "",
    val model: String = "",
    val category: String = "General",
    val description: String = "",
    val priceHistory: List<LiveHistoryDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class LiveHistoryDto(
    val date: String,
    val price: Double,
    val storeName: String
)

interface PriceApiService {
    @GET("search")
    suspend fun search(@Query("q") query: String): LivePriceSearchResponse
}

object PriceApiFactory {
    fun create(): PriceApiService {
        val baseUrl = BuildConfig.PRICE_API_BASE_URL.trim().trimEnd('/')
        require(baseUrl.isNotEmpty() && !baseUrl.contains("YOUR_")) {
            "Live price API is not configured. Set PRICE_API_BASE_URL in .env."
        }

        return Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PriceApiService::class.java)
    }
}
