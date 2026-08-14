package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PricePilotViewModel

@Composable
fun HomeScreen(
    viewModel: PricePilotViewModel,
    onNavigateToResults: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRecents: () -> Unit
) {
    var queryInput by remember { mutableStateOf("") }
    val recents by viewModel.recentComparisons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val stores = listOf("Amazon", "Flipkart", "Myntra", "Ajio", "Croma", "Reliance Digital", "Tata CLiQ")

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = false,
                    onClick = onNavigateToSearch
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Wishlist") },
                    label = { Text("Wishlist") },
                    selected = false,
                    onClick = onNavigateToWishlist
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = onNavigateToSettings
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // App Bar Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PricePilot",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Compare before you buy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sell,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Section Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Find the best price",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = queryInput,
                        onValueChange = { queryInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Paste product link or search product...") },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (queryInput.isNotBlank()) {
                                    viewModel.compareProduct(queryInput)
                                    onNavigateToResults()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Compare, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compare")
                        }
                        Button(
                            onClick = {
                                if (queryInput.isNotBlank()) {
                                    viewModel.searchProducts(queryInput)
                                }
                                onNavigateToSearch()
                            },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Search")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Store Chips
            Text(
                text = "Supported Stores",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(stores) { store ->
                    Card(
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = store,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Recent Comparisons Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Comparisons",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (recents.isNotEmpty()) {
                    Text(
                        text = "View All",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onNavigateToRecents() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (recents.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No recent comparisons yet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                recents.take(3).forEach { recent ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.compareProduct(recent.queryOrUrl)
                                onNavigateToResults()
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = recent.title,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Best: ₹${recent.lowestPrice} (${recent.storeName})",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Compare,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
