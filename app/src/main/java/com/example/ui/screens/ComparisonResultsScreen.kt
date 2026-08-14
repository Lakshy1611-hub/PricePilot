package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonResultsScreen(
    viewModel: PricePilotViewModel,
    onBack: () -> Unit,
    onNavigateToDetails: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val product = viewModel.currentComparison.collectAsState().value
    val wishlist by viewModel.wishlist.collectAsState()
    val context = LocalContext.current

    val isWishlisted = product?.let { p -> wishlist.any { it.productId == p.productId } } ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparison Results") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (product != null) {
                        IconButton(onClick = {
                            val cheapest = product.offers.minByOrNull { it.currentPrice } ?: product.offers.first()
                            viewModel.toggleWishlist(product, cheapest)
                        }) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No product comparison selected.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            val bestOffer = product.offers.minByOrNull { it.currentPrice } ?: product.offers.first()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product Header Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = product.imageUrl,
                                    contentDescription = product.title,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.brand,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = product.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = androidx.compose.ui.graphics.Color(0xFFF59E0B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${bestOffer.rating} (${bestOffer.sellerName})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onNavigateToDetails,
                                    modifier = Modifier.weight(1f),
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text("Product Details")
                                }
                                Button(
                                    onClick = onNavigateToHistory,
                                    modifier = Modifier.weight(1f),
                                    shape = MaterialTheme.shapes.small,
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Price History")
                                }
                            }
                        }
                    }
                }

                // Best Price Highlight Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BestPriceContainer),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "BEST PRICE FOUND",
                                    color = com.example.ui.theme.BestPriceEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${bestOffer.currentPrice}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = com.example.ui.theme.BestPriceEmerald
                                )
                                Text(
                                    text = "Store: ${bestOffer.storeName} (${bestOffer.discount}% OFF)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bestOffer.productUrl))
                                    context.startActivity(intent)
                                },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = com.example.ui.theme.BestPriceEmerald
                                )
                            ) {
                                Text("View Deal")
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "All Available Offers (${product.offers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Offer Cards
                items(product.offers.sortedBy { it.currentPrice }) { offer ->
                    val isCheapest = offer.storeName == bestOffer.storeName
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.medium,
                        border = if (isCheapest) androidx.compose.foundation.BorderStroke(2.dp, com.example.ui.theme.BestPriceEmerald) else null
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = offer.storeName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = offer.variantInfo,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isCheapest) {
                                    Box(
                                        modifier = Modifier
                                            .background(com.example.ui.theme.BestPriceContainer, MaterialTheme.shapes.small)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "BEST PRICE",
                                            color = com.example.ui.theme.BestPriceEmerald,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "₹${offer.currentPrice}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "₹${offer.originalPrice}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${offer.availability} • Seller: ${offer.sellerName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offer.productUrl))
                                        context.startActivity(intent)
                                    },
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text("View Deal")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
