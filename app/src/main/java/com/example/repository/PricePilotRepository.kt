package com.example.repository

import com.example.database.RecentComparisonDao
import com.example.database.RecentComparisonEntity
import com.example.database.WishlistDao
import com.example.database.WishlistEntity
import com.example.model.PriceHistoryPoint
import com.example.model.ProductDetails
import com.example.model.ProductOffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PricePilotRepository(
    private val wishlistDao: WishlistDao,
    private val recentComparisonDao: RecentComparisonDao
) {

    val wishlistFlow: Flow<List<WishlistEntity>> = wishlistDao.getAllWishlist()
    val recentComparisonsFlow: Flow<List<RecentComparisonEntity>> = recentComparisonDao.getAllRecents()

    fun isWishlisted(productId: String): Flow<Boolean> = wishlistDao.isWishlisted(productId)

    suspend fun addToWishlist(item: WishlistEntity) {
        wishlistDao.insertWishlist(item)
    }

    suspend fun removeFromWishlist(productId: String) {
        wishlistDao.removeWishlist(productId)
    }

    suspend fun clearWishlist() {
        wishlistDao.clearWishlist()
    }

    suspend fun addRecent(queryOrUrl: String, title: String, lowestPrice: Double, storeName: String, imageUrl: String) {
        recentComparisonDao.insertRecent(
            RecentComparisonEntity(
                queryOrUrl = queryOrUrl,
                title = title,
                lowestPrice = lowestPrice,
                storeName = storeName,
                imageUrl = imageUrl
            )
        )
    }

    suspend fun clearRecents() {
        recentComparisonDao.clearRecents()
    }

    // Comprehensive product comparison provider with smart matching
    suspend fun compareProduct(queryOrUrl: String): ProductDetails {
        val normalized = queryOrUrl.trim().lowercase()

        // Match against catalog or generate structured mock comparative data based on query
        val catalog = getSampleCatalog()
        val match = catalog.find {
            normalized.contains(it.title.lowercase()) ||
            it.brand.lowercase().contains(normalized) ||
            normalized.contains(it.model.lowercase()) ||
            it.category.lowercase().contains(normalized)
        } ?: generateDynamicProduct(queryOrUrl)

        // Save to recent comparisons
        val cheapest = match.offers.minByOrNull { it.currentPrice } ?: match.offers.first()
        addRecent(
            queryOrUrl = queryOrUrl,
            title = match.title,
            lowestPrice = cheapest.currentPrice,
            storeName = cheapest.storeName,
            imageUrl = match.imageUrl
        )

        return match
    }

    suspend fun searchProducts(query: String): List<ProductDetails> {
        val q = query.trim().lowercase()
        val catalog = getSampleCatalog()
        if (q.isEmpty()) return catalog
        return catalog.filter {
            it.title.lowercase().contains(q) ||
            it.brand.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.model.lowercase().contains(q)
        }.ifEmpty {
            listOf(generateDynamicProduct(query))
        }
    }

    private fun generateDynamicProduct(query: String): ProductDetails {
        val title = if (query.isNotBlank()) query.replaceFirstChar { it.uppercase() } else "Generic Smart Product"
        val basePrice = 14999.00
        return ProductDetails(
            productId = "dyn_" + title.hashCode(),
            title = title,
            brand = "Top Brand",
            model = "Model X",
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
            category = "General",
            description = "High quality product compared across top verified e-commerce platforms with secure warranty and fast delivery.",
            offers = listOf(
                ProductOffer(
                    storeName = "Amazon",
                    productTitle = "$title (Verified Store Offer)",
                    productUrl = "https://www.amazon.in",
                    imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    currentPrice = basePrice,
                    originalPrice = basePrice * 1.25,
                    discount = 20,
                    availability = "In Stock",
                    sellerName = "Cloudtail India",
                    rating = 4.6f,
                    matchConfidence = "Likely Same Product",
                    variantInfo = "Standard Edition"
                ),
                ProductOffer(
                    storeName = "Flipkart",
                    productTitle = "$title - Fast Delivery Deal",
                    productUrl = "https://www.flipkart.com",
                    imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    currentPrice = basePrice * 0.95,
                    originalPrice = basePrice * 1.20,
                    discount = 21,
                    availability = "In Stock",
                    sellerName = "RetailNet",
                    rating = 4.5f,
                    matchConfidence = "Likely Same Product",
                    variantInfo = "Standard Edition"
                ),
                ProductOffer(
                    storeName = "Croma",
                    productTitle = "$title (Brand Warranty)",
                    productUrl = "https://www.croma.com",
                    imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    currentPrice = basePrice * 1.05,
                    originalPrice = basePrice * 1.20,
                    discount = 12,
                    availability = "In Stock",
                    sellerName = "Croma Retail",
                    rating = 4.7f,
                    matchConfidence = "Possible Match",
                    variantInfo = "Store Warranty Bundle"
                )
            ),
            priceHistory = listOf(
                PriceHistoryPoint("1 Aug", basePrice * 1.15, "Amazon"),
                PriceHistoryPoint("8 Aug", basePrice * 1.05, "Amazon"),
                PriceHistoryPoint("14 Aug", basePrice, "Amazon")
            )
        )
    }

    private fun getSampleCatalog(): List<ProductDetails> {
        return listOf(
            ProductDetails(
                productId = "prod_iphone16",
                title = "Apple iPhone 16 (128 GB) - Ultramarine",
                brand = "Apple",
                model = "iPhone 16",
                imageUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80",
                category = "Smartphones",
                description = "Apple iPhone 16 with Camera Control, 48MP Fusion camera, and A18 chip. 128GB Storage variant.",
                offers = listOf(
                    ProductOffer(
                        storeName = "Amazon",
                        productTitle = "Apple iPhone 16 (128 GB) - Ultramarine",
                        productUrl = "https://www.amazon.in/dp/B0DGJ7489X",
                        imageUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 74900.0,
                        originalPrice = 79900.0,
                        discount = 6,
                        availability = "In Stock",
                        sellerName = "Appario Retail Private Ltd",
                        rating = 4.8f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "128 GB | Ultramarine"
                    ),
                    ProductOffer(
                        storeName = "Flipkart",
                        productTitle = "Apple iPhone 16 (Ultramarine, 128 GB)",
                        productUrl = "https://www.flipkart.com/apple-iphone-16-ultramarine-128-gb/p/itm123",
                        imageUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 73999.0,
                        originalPrice = 79900.0,
                        discount = 7,
                        availability = "In Stock",
                        sellerName = "Omnitech Retail",
                        rating = 4.7f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "128 GB | Ultramarine"
                    ),
                    ProductOffer(
                        storeName = "Croma",
                        productTitle = "Apple iPhone 16 (128GB, Ultramarine)",
                        productUrl = "https://www.croma.com/apple-iphone-16-128gb-ultramarine/p/274000",
                        imageUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 74900.0,
                        originalPrice = 79900.0,
                        discount = 6,
                        availability = "In Stock",
                        sellerName = "Croma Direct",
                        rating = 4.9f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "128 GB | Ultramarine"
                    ),
                    ProductOffer(
                        storeName = "Reliance Digital",
                        productTitle = "Apple iPhone 16 128GB Ultramarine",
                        productUrl = "https://www.reliancedigital.in/apple-iphone-16-128gb",
                        imageUrl = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 75490.0,
                        originalPrice = 79900.0,
                        discount = 5,
                        availability = "In Stock",
                        sellerName = "Reliance Retail",
                        rating = 4.6f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "128 GB | Ultramarine"
                    )
                ),
                priceHistory = listOf(
                    PriceHistoryPoint("Jul 1", 79900.0, "Amazon"),
                    PriceHistoryPoint("Jul 20", 77500.0, "Amazon"),
                    PriceHistoryPoint("Aug 5", 74900.0, "Amazon"),
                    PriceHistoryPoint("Aug 14", 73999.0, "Flipkart")
                )
            ),
            ProductDetails(
                productId = "prod_sonywh1000xm5",
                title = "Sony WH-1000XM5 Wireless Noise Cancelling Headphones",
                brand = "Sony",
                model = "WH-1000XM5",
                imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                category = "Audio",
                description = "Industry-leading noise canceling with two processors and 8 microphones for crystal clear hands-free calling.",
                offers = listOf(
                    ProductOffer(
                        storeName = "Amazon",
                        productTitle = "Sony WH-1000XM5 Wireless Headphones (Black)",
                        productUrl = "https://www.amazon.in/dp/B09XS7JYNW",
                        imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 26990.0,
                        originalPrice = 34990.0,
                        discount = 23,
                        availability = "In Stock",
                        sellerName = "Cloudtail India",
                        rating = 4.7f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "Black | ANC"
                    ),
                    ProductOffer(
                        storeName = "Flipkart",
                        productTitle = "SONY WH-1000XM5 Bluetooth Headset (Black, On the Ear)",
                        productUrl = "https://www.flipkart.com/sony-wh-1000xm5/p/itm456",
                        imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 27490.0,
                        originalPrice = 34990.0,
                        discount = 21,
                        availability = "In Stock",
                        sellerName = "RetailNet",
                        rating = 4.6f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "Black | ANC"
                    ),
                    ProductOffer(
                        storeName = "Croma",
                        productTitle = "Sony WH-1000XM5 Over-Ear Wireless Headphone",
                        productUrl = "https://www.croma.com/sony-wh-1000xm5/p/248550",
                        imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 29990.0,
                        originalPrice = 34990.0,
                        discount = 14,
                        availability = "In Stock",
                        sellerName = "Croma Retail",
                        rating = 4.8f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "Black | ANC"
                    ),
                    ProductOffer(
                        storeName = "Tata CLiQ",
                        productTitle = "Sony WH-1000XM5 Active Noise Cancelling Headphones",
                        productUrl = "https://www.tatacliq.com/sony-wh1000xm5",
                        imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 28990.0,
                        originalPrice = 34990.0,
                        discount = 17,
                        availability = "In Stock",
                        sellerName = "Tata CLIQ Luxury",
                        rating = 4.7f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "Black | ANC"
                    )
                ),
                priceHistory = listOf(
                    PriceHistoryPoint("Jul 1", 34990.0, "Amazon"),
                    PriceHistoryPoint("Jul 15", 29990.0, "Amazon"),
                    PriceHistoryPoint("Aug 10", 26990.0, "Amazon")
                )
            ),
            ProductDetails(
                productId = "prod_nikeshoes",
                title = "Nike Air Zoom Pegasus 41 Running Shoes",
                brand = "Nike",
                model = "Pegasus 41",
                imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                category = "Footwear",
                description = "Responsive cushioning in the Pegasus provides an energized ride for everyday road running.",
                offers = listOf(
                    ProductOffer(
                        storeName = "Myntra",
                        productTitle = "Nike Men Air Zoom Pegasus 41 Running Shoes",
                        productUrl = "https://www.myntra.com/shoes/nike/pegasus-41",
                        imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 10495.0,
                        originalPrice = 11995.0,
                        discount = 12,
                        availability = "In Stock",
                        sellerName = "Myntra Fashion",
                        rating = 4.6f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "UK 9 / US 10"
                    ),
                    ProductOffer(
                        storeName = "Ajio",
                        productTitle = "Nike Pegasus 41 Sports Shoes",
                        productUrl = "https://www.ajio.com/nike-pegasus-41/p/469",
                        imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 9999.0,
                        originalPrice = 11995.0,
                        discount = 16,
                        availability = "In Stock",
                        sellerName = "Reliance Brands",
                        rating = 4.5f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "UK 9 / US 10"
                    ),
                    ProductOffer(
                        storeName = "Amazon",
                        productTitle = "Nike Unisex Pegasus 41 Running Shoe",
                        productUrl = "https://www.amazon.in/dp/B0D123",
                        imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                        currentPrice = 11495.0,
                        originalPrice = 11995.0,
                        discount = 4,
                        availability = "In Stock",
                        sellerName = "Cloudtail India",
                        rating = 4.7f,
                        matchConfidence = "Likely Same Product",
                        variantInfo = "UK 9 / US 10"
                    )
                ),
                priceHistory = listOf(
                    PriceHistoryPoint("Jul 1", 11995.0, "Myntra"),
                    PriceHistoryPoint("Aug 1", 10495.0, "Myntra")
                )
            )
        )
    }
}
