package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun SearchScreen(viewModel: PricePilotViewModel, onNavigateHome: () -> Unit, onNavigateWishlist: () -> Unit, onNavigateSettings: () -> Unit, onNavigateResults: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedStores by remember { mutableStateOf(setOf<String>()) }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Priority stores first; each other store belongs to one section only.
    val priorityStores = listOf("AJIO", "Amazon", "Flipkart", "Meesho", "Myntra")
    val fashionStores = listOf("Nykaa")
    val marketplaceStores = listOf("Snapdeal")
    val electronicsStores = listOf("Croma", "Reliance Digital", "Tata CLiQ")
    val otherStores = listOf("Other stores")

    val visibleResults = searchResults.filter { product ->
        selectedStores.isEmpty() || product.offers.any { offer ->
            selectedStores.any { selected -> selected == "Other stores" || selected.equals(offer.storeName, true) }
        }
    }

    fun submitSearch() { if (searchQuery.isNotBlank()) viewModel.searchProducts(searchQuery.trim()) }
    fun toggle(store: String) { selectedStores = if (store in selectedStores) selectedStores - store else selectedStores + store }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Find the best deal", fontWeight = FontWeight.ExtraBold) }, actions = { IconButton(onClick = { showFilters = true }) { Icon(Icons.Default.FilterList, "Filters") } }) },
        bottomBar = { NavigationBar {
            NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = false, onClick = onNavigateHome)
            NavigationBarItem(icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") }, selected = true, onClick = {})
            NavigationBarItem(icon = { Icon(Icons.Default.Bookmark, null) }, label = { Text("Wishlist") }, selected = false, onClick = onNavigateWishlist)
            NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }, selected = false, onClick = onNavigateSettings)
        }}
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search a product…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { IconButton(onClick = { submitSearch() }, enabled = searchQuery.isNotBlank()) { Icon(Icons.Default.ArrowForward, "Search") } },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { submitSearch() })
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Store filters", fontWeight = FontWeight.ExtraBold)
                    Text(if (selectedStores.isEmpty()) "All platforms" else "${selectedStores.size} selected", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = { showFilters = true }) { Icon(Icons.Default.FilterList, null); Spacer(Modifier.width(4.dp)); Text("Filter") }
            }
            if (selectedStores.isNotEmpty()) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                selectedStores.forEach { store -> AssistChip(onClick = { toggle(store) }, label = { Text(store) }, trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }) }
            }
            Spacer(Modifier.height(10.dp))
            if (isLoading) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(); Spacer(Modifier.height(12.dp)); Text("Finding live offers…", fontWeight = FontWeight.Bold); Text("Checking available stores", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (visibleResults.isEmpty()) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Search, null, Modifier.size(58.dp), tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(12.dp)); Text(if (searchQuery.isBlank()) "Search a product to compare prices." else "No matching products found.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(visibleResults) { _, product ->
                        AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn()) {
                            val best = product.offers.minByOrNull { it.currentPrice } ?: return@AnimatedVisibility
                            Card(Modifier.fillMaxWidth().clickable { viewModel.compareProduct(product.title); onNavigateResults() }, shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(product.imageUrl, product.title, Modifier.size(82.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                                    Spacer(Modifier.width(14.dp))
                                    Column(Modifier.weight(1f)) { Text(product.title, fontWeight = FontWeight.Bold, maxLines = 2); Spacer(Modifier.height(6.dp)); Text("₹${best.currentPrice}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold); Text("Best on ${best.storeName}", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    Icon(Icons.Default.ChevronRight, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilters) ModalBottomSheet(onDismissRequest = { showFilters = false }) {
        LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 20.dp), contentPadding = PaddingValues(bottom = 30.dp)) {
            item { Text("Choose platforms", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold); Text("Priority stores first, then more platforms", color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(18.dp)) }
            item { FilterSection("⭐ Priority stores", priorityStores, selectedStores, ::toggle) }
            item { FilterSection("👗 Fashion & beauty", fashionStores, selectedStores, ::toggle) }
            item { FilterSection("🛒 Marketplaces", marketplaceStores, selectedStores, ::toggle) }
            item { FilterSection("📱 Electronics", electronicsStores, selectedStores, ::toggle) }
            item { FilterSection("🌐 Other platforms", otherStores, selectedStores, ::toggle) }
            item { Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = { selectedStores = emptySet(); showFilters = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Clear filters") }; Spacer(Modifier.height(8.dp)); Button(onClick = { showFilters = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Apply ${if (selectedStores.isEmpty()) "all stores" else "${selectedStores.size} stores"}") } }
        }
    }
}

@Composable
private fun FilterSection(title: String, stores: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        stores.distinct().forEach { store ->
            Card(Modifier.fillMaxWidth().padding(vertical = 3.dp), shape = RoundedCornerShape(15.dp)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (store == "Other stores") Icon(Icons.Default.Storefront, store, Modifier.size(30.dp), tint = MaterialTheme.colorScheme.primary)
                    else {
                        val domain = when (store) {
                            "AJIO" -> "ajio.com"; "Amazon" -> "amazon.in"; "Flipkart" -> "flipkart.com"; "Meesho" -> "meesho.com"; "Myntra" -> "myntra.com"; "Nykaa" -> "nykaa.com"; "Snapdeal" -> "snapdeal.com"; "Croma" -> "croma.com"; "Reliance Digital" -> "reliancedigital.in"; "Tata CLiQ" -> "tatacliq.com"; else -> ""
                        }
                        AsyncImage("https://www.google.com/s2/favicons?domain=$domain&sz=64", store, Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)))
                    }
                    Spacer(Modifier.width(10.dp)); Text(store, Modifier.weight(1f), fontWeight = FontWeight.SemiBold); Checkbox(checked = store in selected, onCheckedChange = { onToggle(store) })
                }
            }
        }
    }
}
