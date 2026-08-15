package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PriceCheck
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
import com.example.model.ProductOffer
import com.example.ui.viewmodel.PricePilotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(viewModel: PricePilotViewModel, onBack: () -> Unit, onNavigateToHistory: () -> Unit = {}) {
    val product by viewModel.currentComparison.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = { Text("Product Details", fontWeight = FontWeight.ExtraBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, actions = {
            product?.let { p ->
                val best = p.offers.filter { it.currentPrice > 0 }.minByOrNull { it.currentPrice }
                val saved = wishlist.any { it.productId == p.productId }
                IconButton(onClick = { if (best != null) viewModel.toggleWishlist(p, best) }) { Icon(if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, "Wishlist") }
            }
        })
    }) { padding ->
        if (product == null) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No product details available.") }
        else {
            val p = product!!
            val priced = p.offers.filter { it.currentPrice > 0 }
            val best = priced.minByOrNull { it.currentPrice } ?: p.offers.firstOrNull()
            if (best == null) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No live offers available.") }
            else {
                val highest = priced.maxOfOrNull { it.currentPrice } ?: best.currentPrice
                val savings = (highest - best.currentPrice).coerceAtLeast(0.0)
                val sortedOffers: List<ProductOffer> = p.offers.sortedWith(compareByDescending<ProductOffer> { it.currentPrice > 0 }.thenBy { it.currentPrice })
                LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item { AnimatedVisibility(true, enter = fadeIn(tween(420)) + scaleIn(tween(420), initialScale = .96f)) { Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), elevation = CardDefaults.cardElevation(6.dp)) { Column(Modifier.padding(16.dp)) { AsyncImage(p.imageUrl, p.title, Modifier.fillMaxWidth().height(245.dp).clip(RoundedCornerShape(22.dp)), contentScale = ContentScale.Fit); Spacer(Modifier.height(14.dp)); Text(p.brand.ifBlank { "Product" }, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold); Text(p.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold); if (best.rating > 0) Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Star, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(5.dp)); Text("${best.rating}", fontWeight = FontWeight.Bold) } } } } }
                    item { AnimatedVisibility(true, enter = fadeIn(tween(450, 80)) + slideInVertically(tween(450, 80)) { it / 4 }) { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(24.dp)) { Column(Modifier.padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.PriceCheck, null, Modifier.size(30.dp), tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text("BEST LIVE PRICE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold); Text("₹${best.currentPrice}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold); Text("${best.storeName} • ${best.discount}% OFF", color = MaterialTheme.colorScheme.onSurfaceVariant) } }; if (savings > 0) Text("Save up to ₹${savings.toInt()} compared with other offers", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp)); Spacer(Modifier.height(12.dp)); Button(onClick = { if (best.productUrl.isNotBlank()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(best.productUrl))) }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Icon(Icons.Default.OpenInNew, null); Spacer(Modifier.width(6.dp)); Text("Open Best Deal", fontWeight = FontWeight.Bold) } } } } }
                    item { OutlinedButton(onClick = onNavigateToHistory, Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Icon(Icons.Default.History, null); Spacer(Modifier.width(6.dp)); Text("View Price History", fontWeight = FontWeight.Bold) } }
                    item { Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(10.dp)); Column { Text("PricePilot AI", fontWeight = FontWeight.ExtraBold); Text(if (savings > 0) "This listing saves about ₹${savings.toInt()} versus the highest offer. Check price history before buying." else "Compare the live offers and price history before buying.", color = MaterialTheme.colorScheme.onSurfaceVariant) } } } }
                    item { Text("All live offers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) }
                    itemsIndexed(sortedOffers) { index, offer -> AnimatedVisibility(true, enter = fadeIn(tween(300, index * 45)) + slideInVertically(tween(300, index * 45)) { it / 6 }) { Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) { Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(offer.storeName, fontWeight = FontWeight.ExtraBold); Text(if (offer.currentPrice > 0) "₹${offer.currentPrice} • ${offer.discount}% OFF" else "Price unavailable", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold); Text(offer.availability, style = MaterialTheme.typography.bodySmall) }; OutlinedButton(onClick = { if (offer.productUrl.isNotBlank()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(offer.productUrl))) }, shape = RoundedCornerShape(13.dp)) { Text("Open") } } } } }
                    item { Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(18.dp)) { Text("Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); Text(p.description.ifBlank { "No description was provided by the live price source." }, color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(10.dp)); Text("Category: ${p.category.ifBlank { "Not provided" }}"); Text("Model: ${p.model.ifBlank { "Not provided" }}") } } }
                }
            }
        }
    }
}
