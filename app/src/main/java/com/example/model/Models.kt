package com.example.model

import java.io.Serializable

data class ProductOffer(
    val id: String = java.util.UUID.randomUUID().toString(),
    val storeName: String,
    val productTitle: String,
    val productUrl: String,
    val imageUrl: String,
    val currentPrice: Double,
    val originalPrice: Double,
    val discount: Int, // percentage e.g. 25
    val currency: String = "₹",
    val availability: String, // "In Stock", "Out of Stock", "Limited Stock"
    val sellerName: String,
    val rating: Float = 4.5f,
    val lastUpdated: String = "Just now",
    val matchConfidence: String = "Likely Same Product", // "Likely Same Product", "Possible Match"
    val variantInfo: String = "Standard"
) : Serializable

data class PriceHistoryPoint(
    val date: String,
    val price: Double,
    val storeName: String
) : Serializable

data class ProductDetails(
    val productId: String,
    val title: String,
    val brand: String,
    val model: String,
    val imageUrl: String,
    val category: String,
    val offers: List<ProductOffer>,
    val priceHistory: List<PriceHistoryPoint>,
    val description: String
) : Serializable
