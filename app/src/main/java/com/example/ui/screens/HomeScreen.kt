package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.viewmodel.PricePilotViewModel

private fun roundedClip(shape: androidx.compose.ui.graphics.Shape): Modifier = Modifier.clip(shape)

@Composable
fun HomeScreen(
    viewModel: PricePilotViewModel,
    onNavigateToResults: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRecents: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    val recents by viewModel.recentComparisons.collectAsState()
    val transition = rememberInfiniteTransition(label = "pricepilotHero")
    val floatY by transition.animateFloat(-8f, 8f, infiniteRepeatable(tween(2200), RepeatMode.Reverse), label = "float")
    val pulse by transition.animateFloat(.96f, 1.04f, infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "pulse")
    LaunchedEffect(Unit) { visible = true }

    fun search() { if (query.isNotBlank()) { viewModel.searchProducts(query.trim()); onNavigateToSearch() } }
    fun compare() { if (query.isNotBlank()) { viewModel.compareProduct(query.trim()); onNavigateToResults() } }

    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = true, onClick = {})
            NavigationBarItem(icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") }, selected = false, onClick = onNavigateToSearch)
            NavigationBarItem(icon = { Icon(Icons.Default.Bookmark, null) }, label = { Text("Wishlist") }, selected = false, onClick = onNavigateToWishlist)
            NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }, selected = false, onClick = onNavigateToSettings)
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(18.dp)) {
            AnimatedVisibility(visible, enter = fadeIn(tween(450)) + scaleIn(tween(450))) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Smart shopping starts here", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text("PricePilot", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                        }
                        Box(Modifier.size(54.dp).then(roundedClip(RoundedCornerShape(18.dp))).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.TrendingDown, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(30.dp).graphicsLayer { rotationZ = floatY })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Box(Modifier.fillMaxWidth().height(220.dp).then(roundedClip(RoundedCornerShape(30.dp))).background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primaryContainer))).padding(22.dp)) {
                        Box(Modifier.size(150.dp).offset(x = 190.dp, y = (-45).dp + floatY.dp).then(roundedClip(CircleShape)).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = .12f)))
                        Box(Modifier.size(95.dp).offset(x = 235.dp, y = 125.dp - floatY.dp).then(roundedClip(CircleShape)).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = .10f)))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(44.dp).then(roundedClip(CircleShape)).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = .16f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.TrendingDown, null, tint = MaterialTheme.colorScheme.onPrimary) }
                                Spacer(Modifier.width(10.dp)); Text("LIVE DEAL HUNT", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("Compare smarter.\nSave more.", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(10.dp))
                            Text("Find real prices across your favourite stores.", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .84f))
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), elevation = CardDefaults.cardElevation(7.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("What do you want to buy?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search a product or paste a link…") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(18.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { search() }))
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(onClick = { compare() }, enabled = query.isNotBlank(), modifier = Modifier.weight(1f).graphicsLayer { scaleX = pulse; scaleY = pulse }, shape = RoundedCornerShape(17.dp)) { Icon(Icons.Default.Compare, null); Spacer(Modifier.width(6.dp)); Text("Compare", fontWeight = FontWeight.Bold) }
                                Button(onClick = { search() }, enabled = query.isNotBlank(), modifier = Modifier.weight(1f), shape = RoundedCornerShape(17.dp)) { Icon(Icons.Default.Search, null); Spacer(Modifier.width(6.dp)); Text("Search", fontWeight = FontWeight.Bold) }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Tip: press Enter to search instantly", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Popular stores", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(10.dp))
                    val stores = listOf("AJIO" to "ajio.com", "Amazon" to "amazon.in", "Flipkart" to "flipkart.com", "Meesho" to "meesho.com", "Myntra" to "myntra.com")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(2.dp)) {
                        itemsIndexed(stores) { _, store ->
                            Card(shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(3.dp)) {
                                Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(model = "https://www.google.com/s2/favicons?domain=${store.second}&sz=128", contentDescription = store.first, modifier = Modifier.size(30.dp).then(roundedClip(CircleShape)))
                                    Spacer(Modifier.width(8.dp)); Text(store.first, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Recent comparisons", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        if (recents.isNotEmpty()) Text("View all", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigateToRecents() })
                    }
                    Spacer(Modifier.height(10.dp))
                    if (recents.isEmpty()) {
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(Modifier.fillMaxWidth().padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.History, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(8.dp)); Text("Your comparisons will appear here", fontWeight = FontWeight.Bold) }
                        }
                    } else {
                        recents.take(3).forEach { recent ->
                            Card(Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { viewModel.compareProduct(recent.queryOrUrl); onNavigateToResults() }, shape = RoundedCornerShape(20.dp)) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(recent.title, fontWeight = FontWeight.Bold); Text("Best ₹${recent.lowestPrice} • ${recent.storeName}", color = MaterialTheme.colorScheme.primary) }; Icon(Icons.Default.ArrowForward, null) }
                            }
                        }
                    }
                }
            }
        }
    }
}
