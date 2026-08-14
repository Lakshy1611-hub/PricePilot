package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.viewmodel.PricePilotViewModel

private fun storeDomain(name: String): String = when (name.lowercase()) {
    "ajio" -> "ajio.com"
    "amazon", "amazon.in" -> "amazon.in"
    "flipkart" -> "flipkart.com"
    "meesho" -> "meesho.com"
    "myntra" -> "myntra.com"
    "croma" -> "croma.com"
    "reliance digital" -> "reliancedigital.in"
    "tata cliq" -> "tatacliq.com"
    else -> name.lowercase().replace(" ", "") + ".com"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonResultsScreen(viewModel: PricePilotViewModel, onBack: () -> Unit, onNavigateToDetails: () -> Unit, onNavigateToHistory: () -> Unit) {
    val product = viewModel.currentComparison.collectAsState().value
    val wishlist by viewModel.wishlist.collectAsState()
    val context = LocalContext.current
    val isWishlisted = product?.let { p -> wishlist.any { it.productId == p.productId } } ?: false

    Scaffold(topBar = {
        TopAppBar(title = { Text("Price Comparison", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, actions = {
            if (product != null) IconButton({ viewModel.toggleWishlist(product, product.offers.minBy { it.currentPrice }) }) { Icon(if (isWishlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, "Wishlist", tint = MaterialTheme.colorScheme.primary) }
        })
    }) { padding ->
        if (product == null) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No comparison selected") }
        else {
            val best = product.offers.minBy { it.currentPrice }
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), elevation = CardDefaults.cardElevation(6.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(product.imageUrl, product.title, Modifier.size(92.dp).clip(RoundedCornerShape(18.dp)), contentScale = ContentScale.Crop)
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(product.brand, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(product.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, maxLines = 2)
                                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Star, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(4.dp)); Text("${best.rating}", style = MaterialTheme.typography.bodySmall) }
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(onNavigateToDetails, Modifier.weight(1f), shape = RoundedCornerShape(15.dp)) { Text("Details") }
                                OutlinedButton(onNavigateToHistory, Modifier.weight(1f), shape = RoundedCornerShape(15.dp)) { Icon(Icons.Default.History, null, Modifier.size(17.dp)); Spacer(Modifier.width(4.dp)); Text("History") }
                            }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(24.dp)) {
                        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("CHEAPEST LIVE OFFER", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                Text("₹${best.currentPrice}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                                Text("${best.storeName} • ${best.discount}% off", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(onClick = { if (best.productUrl.isNotBlank()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(best.productUrl))) }, shape = RoundedCornerShape(15.dp)) { Icon(Icons.Default.OpenInNew, null); Spacer(Modifier.width(5.dp)); Text("Buy") }
                        }
                    }
                }
                item { Text("Compare stores", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp)) }
                itemsIndexed(product.offers.sortedBy { it.currentPrice }) { index, offer ->
                    AnimatedVisibility(true, enter = fadeIn(tween(300, index * 45)) + slideInVertically(tween(300, index * 45)) { it / 4 }) {
                        val cheapest = offer.id == best.id
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), border = if (cheapest) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null, elevation = CardDefaults.cardElevation(3.dp)) {
                            Column(Modifier.padding(15.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage("https://www.google.com/s2/favicons?domain=${storeDomain(offer.storeName)}&sz=128", offer.storeName, Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)))
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) { Text(offer.storeName, fontWeight = FontWeight.ExtraBold); Text(offer.variantInfo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    if (cheapest) Text("BEST PRICE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                                }
                                Spacer(Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.weight(1f)) {
                                        Text("₹${offer.currentPrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                        if (offer.originalPrice > offer.currentPrice) Text("₹${offer.originalPrice}  •  ${offer.discount}% OFF", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${offer.availability} • ${offer.lastUpdated}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Button(onClick = { if (offer.productUrl.isNotBlank()) context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(offer.productUrl))) }, shape = RoundedCornerShape(14.dp)) { Text("View Deal") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
