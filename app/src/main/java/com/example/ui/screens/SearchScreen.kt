package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.viewmodel.PricePilotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: PricePilotViewModel,
    onNavigateHome: () -> Unit,
    onNavigateWishlist: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateResults: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Products") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onNavigateHome
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Wishlist") },
                    label = { Text("Wishlist") },
                    selected = false,
                    onClick = onNavigateWishlist
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = onNavigateSettings
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchProducts(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search iPhone, running shoes, headphones...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (searchResults.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No products found. Try another search.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults) { product ->
                        val best = product.offers.minByOrNull { it.currentPrice } ?: product.offers.first()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.compareProduct(product.title)
                                    onNavigateResults()
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = product.imageUrl,
                                    contentDescription = product.title,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.title,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Best: ₹${best.currentPrice} (${best.storeName})",
                                        color = com.example.ui.theme.BestPriceEmerald,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
