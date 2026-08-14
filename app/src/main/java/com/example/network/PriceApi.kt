package com.example.network

import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query

interface PriceApiService {
    @GET("search")
    suspend fun search(@Query("q") query: String): ResponseBody
}

data class LiveProductDto(
    val id: String? = null,
    val storeName: String = "Unknown Store",
    val productTitle: String = "Unknown Product",
    val productUrl: String = "",
    val imageUrl: String? = null,
    val currentPrice: Double = 0.0,
    val originalPrice: Double? = null,
    val discount: Int? = null,
    val currency: String = "₹",
    val availability: String = "Unknown",
    val sellerName: String = "",
    val rating: Float? = null,
    val lastUpdated: String = "Just now",
    val matchConfidence: String = "Likely Same Product",
    val variantInfo: String = "Standard",
    val brand: String = "",
    val model: String = "",
    val category: String = "",
    val description: String = "",
    val priceHistory: List<LivePriceHistoryDto> = emptyList()
)

data class LivePriceHistoryDto(
    val date: String,
    val price: Double,
    val storeName: String
)

object PriceResponseParser {
    fun parse(body: String): List<LiveProductDto> {
        val root = JSONObject(body)
        val array = findProductsArray(root) ?: return emptyList()
        return buildList {
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                parseProduct(item)?.let(::add)
            }
        }
    }

    private fun findProductsArray(root: JSONObject): JSONArray? {
        listOf("products", "results", "items", "offers", "data").forEach { key ->
            val value = root.opt(key)
            if (value is JSONArray) return value
            if (value is JSONObject) {
                val nested = findProductsArray(value)
                if (nested != null) return nested
            }
        }
        return null
    }

    private fun parseProduct(o: JSONObject): LiveProductDto? {
        val title = firstString(o, "productTitle", "title", "name") ?: return null
        val price = firstDouble(o, "currentPrice", "price", "salePrice", "amount") ?: return null
        if (price <= 0.0) return null

        val productUrl = firstString(o, "productUrl", "url", "link") ?: ""
        val rawStore = firstString(o, "storeName", "store", "retailer", "merchant") ?: "Unknown Store"
        // Prefer the product URL's domain when available. This prevents an API/provider
        // from labelling every result with one retailer even when the links are different.
        val store = inferStoreName(productUrl) ?: canonicalStoreName(rawStore)

        val history = o.optJSONArray("priceHistory")?.let { arr ->
            buildList {
                for (i in 0 until arr.length()) {
                    val h = arr.optJSONObject(i) ?: continue
                    val p = firstDouble(h, "price", "currentPrice") ?: continue
                    add(LivePriceHistoryDto(
                        date = firstString(h, "date", "timestamp") ?: "",
                        price = p,
                        storeName = inferStoreName(firstString(h, "productUrl", "url", "link") ?: "")
                            ?: canonicalStoreName(firstString(h, "storeName", "store") ?: store)
                    ))
                }
            }
        } ?: emptyList()

        return LiveProductDto(
            id = firstString(o, "id", "productId", "offerId"),
            storeName = store,
            productTitle = title,
            productUrl = productUrl,
            imageUrl = firstString(o, "imageUrl", "image", "thumbnail", "thumbnailUrl"),
            currentPrice = price,
            originalPrice = firstDouble(o, "originalPrice", "mrp", "listPrice") ?: price,
            discount = firstInt(o, "discount", "discountPercent") ?: 0,
            currency = firstString(o, "currency") ?: "₹",
            availability = firstString(o, "availability", "stock", "availabilityStatus") ?: "Unknown",
            sellerName = firstString(o, "sellerName", "seller") ?: "",
            rating = firstDouble(o, "rating", "reviewRating")?.toFloat() ?: 0f,
            lastUpdated = firstString(o, "lastUpdated", "updatedAt") ?: "Just now",
            matchConfidence = firstString(o, "matchConfidence") ?: "Likely Same Product",
            variantInfo = firstString(o, "variantInfo", "variant") ?: "Standard",
            brand = firstString(o, "brand", "brandName") ?: "",
            model = firstString(o, "model", "modelName") ?: "",
            category = firstString(o, "category") ?: "",
            description = firstString(o, "description") ?: "",
            priceHistory = history
        )
    }

    private fun inferStoreName(url: String): String? {
        val host = runCatching { java.net.URI(url).host?.lowercase() }.getOrNull() ?: return null
        return when {
            host.contains("amazon.") -> "Amazon"
            host.contains("flipkart.") -> "Flipkart"
            host.contains("ajio.") -> "AJIO"
            host.contains("meesho.") -> "Meesho"
            host.contains("myntra.") -> "Myntra"
            host.contains("croma.") -> "Croma"
            host.contains("reliancedigital.") -> "Reliance Digital"
            host.contains("tatacliq.") -> "Tata CLiQ"
            host.contains("snapdeal.") -> "Snapdeal"
            host.contains("nykaa.") -> "Nykaa"
            else -> null
        }
    }

    private fun canonicalStoreName(value: String): String = when (value.trim().lowercase()) {
        "amazon", "amazon.in" -> "Amazon"
        "flipkart", "flipkart.com" -> "Flipkart"
        "ajio", "ajio.com" -> "AJIO"
        "meesho", "meesho.com" -> "Meesho"
        "myntra", "myntra.com" -> "Myntra"
        "croma", "croma.com" -> "Croma"
        "reliance digital", "reliancedigital.in" -> "Reliance Digital"
        "tata cliq", "tatacliq.com" -> "Tata CLiQ"
        "snapdeal", "snapdeal.com" -> "Snapdeal"
        "nykaa", "nykaa.com" -> "Nykaa"
        else -> value
    }

    private fun firstString(o: JSONObject, vararg keys: String): String? = keys.firstNotNullOfOrNull { key ->
        if (!o.has(key) || o.isNull(key)) null else o.optString(key).takeIf { it.isNotBlank() && it != "null" }
    }

    private fun firstDouble(o: JSONObject, vararg keys: String): Double? = keys.firstNotNullOfOrNull { key ->
        if (!o.has(key) || o.isNull(key)) null else when (val v = o.opt(key)) {
            is Number -> v.toDouble()
            else -> v.toString().replace(",", "").replace(Regex("[^0-9.]"), "").toDoubleOrNull()
        }
    }

    private fun firstInt(o: JSONObject, vararg keys: String): Int? = firstDouble(o, *keys)?.toInt()
}
