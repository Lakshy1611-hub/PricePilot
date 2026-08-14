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
import com.example.network.PriceApiFactory
import com.example.network.LiveProductDto
import com.example.repository.PricePilotRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
                val api = priceApi ?: error("Live price API is not configured")
                val response = api.search(queryOrUrl.trim())
                val product = response.products
                    .map { it.toProductDetails() }
                    .takeIf { it.isNotEmpty() }
                    ?.let { products -> products.maxByOrNull { it.offers.size } }
                    ?: error("No live offers were found for this product")

                _currentComparison.value = product
                val cheapest = product.offers.minBy { it.currentPrice }
                localRepository.addRecent(queryOrUrl, product.title, cheapest.currentPrice, cheapest.storeName, product.imageUrl)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Couldn't load live prices. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val api = priceApi ?: error("Live price API is not configured")
                _searchResults.value = api.search(query.trim()).products.map { it.toProductDetails() }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Search failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

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

private fun LiveProductDto.toProductDetails(): ProductDetails {
    val offer = ProductOffer(
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
    return ProductDetails(
        productId = id ?: "${brand}_${model}_${productTitle}".hashCode().toString(),
        title = productTitle,
        brand = brand,
        model = model,
        imageUrl = imageUrl.orEmpty(),
        category = category,
        offers = listOf(offer),
        priceHistory = priceHistory.map { PriceHistoryPoint(it.date, it.price, it.storeName) },
        description = description
    )
}
