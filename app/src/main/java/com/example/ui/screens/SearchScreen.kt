package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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

private fun storeDomain(name: String): String = when (name.lowercase()) {
    "ajio" -> "ajio.com"
    "amazon", "amazon.in" -> "amazon.in"
    "flipkart", "flipkart.com" -> "flipkart.com"
    "meesho", "meesho.com" -> "meesho.com"
    "myntra", "myntra.com" -> "myntra.com"
    "nykaa", "nykaa.com" -> "nykaa.com"
    "snapdeal", "snapdeal.com" -> "snapdeal.com"
    "croma", "croma.com" -> "croma.com"
    "reliance digital", "reliancedigital.in" -> "reliancedigital.in"
    "tata cliq", "tatacliq.com" -> "tatacliq.com"
    else -> name.lowercase().replace(" ", "") + ".com"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: PricePilotViewModel, onNavigateHome: () -> Unit, onNavigateWishlist: () -> Unit, onNavigateSettings: () -> Unit, onNavigateResults: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedStores by remember { mutableStateOf(setOf<String>()) }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val priorityStores = listOf("AJIO", "Amazon", "Flipkart", "Meesho", "Myntra")
    // No duplicate store appears outside Priority stores.
    val storeCategories = linkedMapOf(
        "Beauty & Lifestyle" to listOf("Nykaa"),
        "Marketplaces" to listOf("Snapdeal"),
        "Electronics" to listOf("Croma", "Reliance Digital", "Tata CLiQ")
    )

    // When filters are active, cards and their best price are calculated ONLY from matching stores.
    val visibleResults = searchResults.mapNotNull { product ->
        val offers = if (selectedStores.isEmpty()) product.offers else product.offers.filter { offer ->
            selectedStores.any { selected -> selected.equals(offer.storeName, ignoreCase = true) }
        }
        if (offers.isEmpty()) null else product to offers
    }

    fun submitSearch() { if (searchQuery.isNotBlank()) viewModel.searchProducts(searchQuery.trim()) }
    fun toggle(store: String) {
        selectedStores = if (selectedStores.any { it.equals(store, true) }) {
            selectedStores.filterNot { it.equals(store, true) }.toSet()
        } else selectedStores + store
    }

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
            if (selectedStores.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    selectedStores.forEach { store ->
                        AssistChip(
                            onClick = { toggle(store) },
                            label = { Text(store) },
                            leadingIcon = { AsyncImage("https://www.google.com/s2/favicons?domain=${storeDomain(store)}&sz=64", store, Modifier.size(18.dp).clip(RoundedCornerShape(4.dp))) },
                            trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            if (isLoading) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(); Spacer(Modifier.height(12.dp)); Text("Finding live offers…", fontWeight = FontWeight.Bold); Text("Checking available platforms", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (visibleResults.isEmpty()) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Search, null, Modifier.size(58.dp), tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(12.dp)); Text(if (searchQuery.isBlank()) "Search a product to compare prices." else "No matching products found.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (selectedStores.isNotEmpty()) TextButton(onClick = { selectedStores = emptySet() }) { Text("Show all stores") }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(visibleResults) { index, pair ->
                        val product = pair.first
                        val matchingOffers = pair.second
                        AnimatedVisibility(visible = true, enter = fadeIn(tween(300, index * 45)) + scaleIn(tween(300, index * 45))) {
                            val best = matchingOffers.minByOrNull { it.currentPrice } ?: return@AnimatedVisibility
                            Card(Modifier.fillMaxWidth().clickable { viewModel.compareProduct(product.title); onNavigateResults() }, shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(product.imageUrl, product.title, Modifier.size(82.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                                    Spacer(Modifier.width(14.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(product.title, fontWeight = FontWeight.Bold, maxLines = 2)
                                        Spacer(Modifier.height(6.dp))
                                        Text("₹${best.currentPrice}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                                        Text("Best on ${best.storeName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        if (matchingOffers.size > 1) Text("${matchingOffers.size} selected-store offers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
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
            item {
                Text("Choose platforms", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("Priority stores first • select one or more", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(18.dp))
            }
            item { FilterSection("⭐ Priority stores", priorityStores, selectedStores, ::toggle) }
            storeCategories.forEach { (category, stores) -> item { FilterSection(category, stores, selectedStores, ::toggle) } }
            item {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { selectedStores = emptySet(); showFilters = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Clear filters") }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { showFilters = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Apply ${if (selectedStores.isEmpty()) "all stores" else "${selectedStores.size} stores"}") }
            }
        }
    }
}

@Composable
private fun FilterSection(title: String, stores: List<String>, selected: Set<String>, onToggle: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        stores.distinct().forEach { store ->
            val isSelected = selected.any { it.equals(store, true) }
            Card(Modifier.fillMaxWidth().padding(vertical = 3.dp), shape = RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage("https://www.google.com/s2/favicons?domain=${storeDomain(store)}&sz=64", store, Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)))
                    Spacer(Modifier.width(10.dp))
                    Text(store, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    Checkbox(checked = isSelected, onCheckedChange = { onToggle(store) })
                }
            }
        }
    }
}
