package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.PricePilotDatabase
import com.example.database.RecentComparisonEntity
import com.example.database.WishlistEntity
import com.example.model.PriceHistoryPoint
import com.example.model.ProductDetails
import com.example.model.ProductOffer
import com.example.network.LiveProductDto
import com.example.network.PriceApiFactory
import com.example.network.PriceResponseParser
import com.example.repository.PricePilotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PricePilotViewModel(application: Application) : AndroidViewModel(application) {

    private val localRepository: PricePilotRepository
    private val priceApi = runCatching { PriceApiFactory.create() }.getOrNull()

    init {
        val db = PricePilotDatabase.getDatabase(application)
        localRepository = PricePilotRepository(db.wishlistDao(), db.recentComparisonDao())
    }

    val wishlist: StateFlow<List<WishlistEntity>> = localRepository.wishlistFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentComparisons: StateFlow<List<RecentComparisonEntity>> = localRepository.recentComparisonsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentComparison = MutableStateFlow<ProductDetails?>(null)
    val currentComparison: StateFlow<ProductDetails?> = _currentComparison.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProductDetails>>(emptyList())
    val searchResults: StateFlow<List<ProductDetails>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _themeMode = MutableStateFlow("System Default")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun compareProduct(queryOrUrl: String) {
        if (queryOrUrl.isBlank()) {
            _errorMessage.value = "Please enter a valid product name or link."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val products = fetchLiveProducts(queryOrUrl.trim())
                val grouped = groupProducts(products)
                val product = grouped
                    .maxByOrNull { it.offers.size }
                    ?: error("No live offers were found for this product")

                _currentComparison.value = product
                val cheapest = product.offers.minBy { it.currentPrice }
                localRepository.addRecent(
                    queryOrUrl,
                    product.title,
                    cheapest.currentPrice,
                    cheapest.storeName,
                    product.imageUrl
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Couldn't load live prices. Please try again."
                _currentComparison.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _errorMessage.value = null
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val products = fetchLiveProducts(query.trim())
                _searchResults.value = groupProducts(products)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _errorMessage.value = e.message ?: "Search failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchLiveProducts(query: String): List<LiveProductDto> {
        val api = priceApi ?: error("Live price API is not configured")
        val responseBody = api.search(query).string()
        if (responseBody.isBlank()) error("The price server returned an empty response")
        return PriceResponseParser.parse(responseBody)
            .filter { it.currentPrice > 0 && it.productTitle.isNotBlank() }
            .distinctBy { "${it.storeName}|${it.productTitle}|${it.currentPrice}|${it.productUrl}" }
    }

    private fun groupProducts(products: List<LiveProductDto>): List<ProductDetails> {
        if (products.isEmpty()) return emptyList()

        val groups = mutableListOf<MutableList<LiveProductDto>>()
        products.forEach { product ->
            val existing = groups.firstOrNull { group ->
                areSameProduct(group.first(), product)
            }
            if (existing != null) existing += product else groups += mutableListOf(product)
        }

        return groups
            .map { group ->
                val representative = group.minByOrNull { it.currentPrice } ?: group.first()
                ProductDetails(
                    productId = stableProductId(group),
                    title = representative.productTitle,
                    brand = representative.brand,
                    model = representative.model,
                    imageUrl = representative.imageUrl.orEmpty(),
                    category = representative.category,
                    offers = group.map { it.toProductOffer() }
                        .distinctBy { "${it.storeName}|${it.productUrl}|${it.currentPrice}" }
                        .sortedBy { it.currentPrice },
                    priceHistory = group.flatMap { dto ->
                        dto.priceHistory.map { PriceHistoryPoint(it.date, it.price, it.storeName) }
                    }.sortedByDescending { it.date },
                    description = representative.description
                )
            }
            .filter { it.offers.isNotEmpty() }
            .sortedWith(compareByDescending<ProductDetails> { it.offers.size }.thenBy { it.offers.minOf { offer -> offer.currentPrice } })
    }

    private fun areSameProduct(a: LiveProductDto, b: LiveProductDto): Boolean {
        val aModel = normalize(a.model)
        val bModel = normalize(b.model)
        if (aModel.isNotBlank() && bModel.isNotBlank() && aModel == bModel) return true

        val aTitle = normalize(a.productTitle)
        val bTitle = normalize(b.productTitle)
        if (aTitle == bTitle) return true

        val aBrand = normalize(a.brand)
        val bBrand = normalize(b.brand)
        if (aBrand.isNotBlank() && bBrand.isNotBlank() && aBrand != bBrand) return false

        val aTokens = aTitle.split(' ').filter { it.length > 2 }.toSet()
        val bTokens = bTitle.split(' ').filter { it.length > 2 }.toSet()
        if (aTokens.isEmpty() || bTokens.isEmpty()) return false
        val intersection = aTokens.intersect(bTokens).size
        val union = aTokens.union(bTokens).size
        return union > 0 && intersection.toDouble() / union >= 0.65
    }

    private fun normalize(value: String): String = value
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), " ")
        .trim()
        .replace(Regex("\\s+"), " ")

    private fun stableProductId(group: List<LiveProductDto>): String =
        group.map { "${normalize(it.brand)}|${normalize(it.model)}|${normalize(it.productTitle)}" }
            .sorted()
            .joinToString("||")
            .hashCode()
            .toString()

    fun toggleWishlist(product: ProductDetails, bestOffer: ProductOffer) {
        viewModelScope.launch {
            val isAlreadySaved = wishlist.value.any { it.productId == product.productId }
            if (isAlreadySaved) localRepository.removeFromWishlist(product.productId)
            else localRepository.addToWishlist(
                WishlistEntity(
                    productId = product.productId,
                    title = product.title,
                    imageUrl = product.imageUrl,
                    currentPrice = bestOffer.currentPrice,
                    lowestPrice = product.offers.minOf { it.currentPrice },
                    storeName = bestOffer.storeName,
                    productUrl = bestOffer.productUrl,
                    priceDropStatus = if (bestOffer.discount > 15) "🔥 ${bestOffer.discount}% Price Drop!" else "Stable Price"
                )
            )
        }
    }

    fun removeFromWishlist(productId: String) = viewModelScope.launch { localRepository.removeFromWishlist(productId) }
    fun clearWishlist() = viewModelScope.launch { localRepository.clearWishlist() }
    fun clearRecentComparisons() = viewModelScope.launch { localRepository.clearRecents() }
    fun setThemeMode(mode: String) { _themeMode.value = mode }
    fun toggleNotifications(enabled: Boolean) { _notificationsEnabled.value = enabled }
    fun clearError() { _errorMessage.value = null }
}

private fun LiveProductDto.toProductOffer(): ProductOffer = ProductOffer(
    id = id ?: "${storeName}_${productTitle}_${currentPrice}".hashCode().toString(),
    storeName = storeName,
    productTitle = productTitle,
    productUrl = productUrl,
    imageUrl = imageUrl.orEmpty(),
    currentPrice = currentPrice,
    originalPrice = originalPrice ?: currentPrice,
    discount = discount ?: 0,
    currency = currency,
    availability = availability,
    sellerName = sellerName,
    rating = rating ?: 0f,
    lastUpdated = lastUpdated,
    matchConfidence = matchConfidence,
    variantInfo = variantInfo
)
