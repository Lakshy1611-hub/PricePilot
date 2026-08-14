package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
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
    var showFilters by remember { mutableStateOf(false) }
    var selectedStores by remember { mutableStateOf(setOf<String>()) }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val priorityStores = listOf("AJIO", "Amazon", "Flipkart", "Meesho", "Myntra")
    val allStores = (priorityStores + listOf("Croma", "Reliance Digital", "Tata CLiQ", "Nykaa", "Snapdeal")).distinct()
    val visibleResults = searchResults.filter { product ->
        selectedStores.isEmpty() || product.offers.any { offer -> selectedStores.any { it.equals(offer.storeName, true) } }
    }

    fun submitSearch() {
        if (searchQuery.isNotBlank()) viewModel.searchProducts(searchQuery.trim())
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Search Products", fontWeight = FontWeight.Bold) }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = false, onClick = onNavigateHome)
                NavigationBarItem(icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Bookmark, null) }, label = { Text("Wishlist") }, selected = false, onClick = onNavigateWishlist)
                NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }, selected = false, onClick = onNavigateSettings)
            }
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search products…") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { submitSearch() })
                )
                Button(onClick = { submitSearch() }, enabled = searchQuery.isNotBlank(), shape = RoundedCornerShape(16.dp), modifier = Modifier.height(56.dp)) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Priority stores", fontWeight = FontWeight.ExtraBold)
                TextButton(onClick = { showFilters = true }) {
                    Icon(Icons.Default.FilterList, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Filter")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                priorityStores.forEach { store ->
                    FilterChip(
                        selected = store in selectedStores,
                        onClick = { selectedStores = if (store in selectedStores) selectedStores - store else selectedStores + store },
                        label = { Text(store, maxLines = 1) }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            if (isLoading) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Finding the best deals…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (visibleResults.isEmpty()) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Search, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    Text(if (searchQuery.isBlank()) "Search any product to compare prices." else "No matching products found.", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(visibleResults) { index, product ->
                        AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn()) {
                            val best = product.offers.minByOrNull { it.currentPrice } ?: product.offers.first()
                            Card(
                                Modifier.fillMaxWidth().clickable { viewModel.compareProduct(product.title); onNavigateResults() },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(model = product.imageUrl, contentDescription = product.title, modifier = Modifier.size(82.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                                    Spacer(Modifier.width(14.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(product.title, fontWeight = FontWeight.Bold, maxLines = 2)
                                        Spacer(Modifier.height(6.dp))
                                        Text("₹${best.currentPrice}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                                        Text("Best on ${best.storeName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilters) {
        ModalBottomSheet(onDismissRequest = { showFilters = false }) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Choose e-commerce platforms", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(14.dp))
                allStores.forEach { store ->
                    FilterChip(
                        selected = store in selectedStores,
                        onClick = { selectedStores = if (store in selectedStores) selectedStores - store else selectedStores + store },
                        label = { Text(store) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { selectedStores = emptySet(); showFilters = false }, modifier = Modifier.fillMaxWidth()) { Text("Clear filters") }
            }
        }
    }
}
