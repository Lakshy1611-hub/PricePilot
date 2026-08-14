package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.ProductDetails
import com.example.ui.viewmodel.PricePilotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestDealsScreen(viewModel: PricePilotViewModel, onBack: () -> Unit, onProduct: () -> Unit) {
    val results by viewModel.searchResults.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val deals = results.flatMap { product ->
        product.offers.filter { it.currentPrice > 0 && it.productUrl.isNotBlank() }.map { offer -> product to offer }
    }.sortedByDescending { it.second.discount }.take(30)

    LaunchedEffect(Unit) {
        if (results.isEmpty() && !loading) viewModel.searchProducts("best deals")
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Column { Text("Best Deals", fontWeight = FontWeight.ExtraBold); Text("Handpicked from live results", style = MaterialTheme.typography.labelSmall) } },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
        )
    }) { padding ->
        if (loading && deals.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (deals.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocalOffer, null, Modifier.size(52.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    Text("No live deals found", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text("Try searching for a product to compare current prices.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, null, Modifier.size(34.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column { Text("Deals worth checking", fontWeight = FontWeight.ExtraBold); Text("Sorted by the biggest live discount.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
                items(deals) { (product, offer) ->
                    DealCard(product, offer.discount, offer.currentPrice, offer.originalPrice, offer.storeName, offer.rating, offer.productUrl) {
                        viewModel.run { /* keep the selected live product in the shared state */ }
                        onProduct()
                    }
                }
            }
        }
    }
}

@Composable
private fun DealCard(product: ProductDetails, discount: Int, current: Double, original: Double, store: String, rating: Float, url: String, onOpen: () -> Unit) {
    val context = LocalContext.current
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(3.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(product.imageUrl, product.title, Modifier.size(86.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) { Text(" $discount% OFF ", modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelSmall) }
                Spacer(Modifier.height(4.dp))
                Text(product.title, maxLines = 2, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) { Text(store, color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.width(7.dp)); Icon(Icons.Default.Star, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary); Text(" $rating", style = MaterialTheme.typography.bodySmall) }
                Spacer(Modifier.height(3.dp))
                Text("₹${current.toInt()}  ₹${original.toInt()}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { if (url.isNotBlank()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }) { Icon(Icons.Default.OpenInNew, "Open deal") }
        }
    }
}
