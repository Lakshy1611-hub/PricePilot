package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.PricePilotDatabase
import com.example.database.RecentComparisonEntity
import com.example.database.WishlistEntity
import com.example.model.ProductDetails
import com.example.model.ProductOffer
import com.example.repository.PricePilotRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PricePilotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PricePilotRepository

    init {
        val db = PricePilotDatabase.getDatabase(application)
        repository = PricePilotRepository(db.wishlistDao(), db.recentComparisonDao())
    }

    val wishlist: StateFlow<List<WishlistEntity>> = repository.wishlistFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentComparisons: StateFlow<List<RecentComparisonEntity>> = repository.recentComparisonsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentComparison = MutableStateFlow<ProductDetails?>(null)
    val currentComparison: StateFlow<ProductDetails?> = _currentComparison.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProductDetails>>(emptyList())
    val searchResults: StateFlow<List<ProductDetails>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _themeMode = MutableStateFlow("System Default") // System Default, Light, Dark
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun compareProduct(queryOrUrl: String) {
        if (queryOrUrl.isBlank()) {
            _errorMessage.value = "Please enter a valid product link or name."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.compareProduct(queryOrUrl)
                _currentComparison.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Couldn't load product comparison. Please check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val results = repository.searchProducts(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _errorMessage.value = "Search failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleWishlist(product: ProductDetails, bestOffer: ProductOffer) {
        viewModelScope.launch {
            val isAlreadySaved = wishlist.value.any { it.productId == product.productId }
            if (isAlreadySaved) {
                repository.removeFromWishlist(product.productId)
            } else {
                val entity = WishlistEntity(
                    productId = product.productId,
                    title = product.title,
                    imageUrl = product.imageUrl,
                    currentPrice = bestOffer.currentPrice,
                    lowestPrice = product.offers.minOfOrNull { it.currentPrice } ?: bestOffer.currentPrice,
                    storeName = bestOffer.storeName,
                    productUrl = bestOffer.productUrl,
                    priceDropStatus = if (bestOffer.discount > 15) "🔥 ${bestOffer.discount}% Price Drop!" else "Stable Price"
                )
                repository.addToWishlist(entity)
            }
        }
    }

    fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            repository.removeFromWishlist(productId)
        }
    }

    fun clearWishlist() {
        viewModelScope.launch {
            repository.clearWishlist()
        }
    }

    fun clearRecentComparisons() {
        viewModelScope.launch {
            repository.clearRecents()
        }
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
