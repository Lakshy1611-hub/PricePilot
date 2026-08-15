package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PricePilotBlue
import com.example.ui.theme.PricePilotGreen
import com.example.ui.theme.PricePilotPink
import com.example.ui.theme.PricePilotPurple
import com.example.ui.theme.PricePilotYellow
import com.example.ui.viewmodel.PricePilotViewModel

@Composable
fun HomeScreen(
    viewModel: PricePilotViewModel,
    onNavigateToResults: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToAi: () -> Unit = {},
    onNavigateToAlerts: () -> Unit = {},
    onNavigateToDeals: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var extracting by remember { mutableStateOf(false) }
    val recents by viewModel.recentComparisons.collectAsState()

    fun search() {
        if (query.isBlank()) return
        extracting = true
        viewModel.searchProducts(query.trim())
        onNavigateToSearch()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.LocalOffer, null) }, label = { Text("Deals") }, selected = false, onClick = onNavigateToDeals)
                NavigationBarItem(icon = { Icon(Icons.Default.NotificationsNone, null) }, label = { Text("Alerts") }, selected = false, onClick = onNavigateToAlerts)
                NavigationBarItem(icon = { Icon(Icons.Default.FavoriteBorder, null) }, label = { Text("Watchlist") }, selected = false, onClick = onNavigateToWishlist)
                NavigationBarItem(icon = { Icon(Icons.Default.PersonOutline, null) }, label = { Text("Profile") }, selected = false, onClick = onNavigateToSettings)
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            Column(Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("PricePilot", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = PricePilotPurple)
                        Text("Compare prices. Save more.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    FilledTonalIconButton(onClick = onNavigateToAi) { Icon(Icons.Default.AutoAwesome, "AI Shopping") }
                }

                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it; extracting = false },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search or paste any product link") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = { IconButton(onClick = { search() }) { Icon(Icons.Default.Mic, "Search") } },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { search() })
                )

                if (extracting) {
                    Spacer(Modifier.height(12.dp))
                    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(48.dp).clip(CircleShape).background(PricePilotPurple.copy(.10f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Link, null, tint = PricePilotPurple)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Extracting product information", fontWeight = FontWeight.ExtraBold)
                                    Text(query, maxLines = 1, color = PricePilotPurple, style = MaterialTheme.typography.bodySmall)
                                }
                                CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 3.dp)
                            }
                            Spacer(Modifier.height(14.dp))
                            Text("●  Initializing link…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(6.dp))
                            Text("●  Extracting product information", color = PricePilotGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryChip("All", Icons.Default.ShoppingBag, PricePilotPurple)
                    CategoryChip("Electronics", Icons.Default.Headphones, PricePilotBlue)
                    CategoryChip("Fashion", Icons.Default.Checkroom, PricePilotPink)
                    CategoryChip("Home & Kitchen", Icons.Default.Home, PricePilotBlue)
                    CategoryChip("More", Icons.Default.MoreHoriz, PricePilotPurple)
                }

                Spacer(Modifier.height(18.dp))
                Card(Modifier.fillMaxWidth().height(184.dp).clickable(onClick = onNavigateToDeals), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = PricePilotPurple)) {
                    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(PricePilotPurple, PricePilotBlue)))) {
                        Column(Modifier.padding(20.dp)) {
                            Surface(color = Color.White.copy(.92f), shape = RoundedCornerShape(10.dp)) {
                                Text("MEGA DEALS", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = PricePilotPurple, fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("Best Prices\nOn Top Products", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(5.dp))
                            Text("Save more with handpicked deals", color = Color.White.copy(.82f))
                            Spacer(Modifier.height(10.dp))
                            Surface(color = Color.White, shape = RoundedCornerShape(14.dp), onClick = onNavigateToDeals) {
                                Text("Explore Deals  ›", Modifier.padding(horizontal = 14.dp, vertical = 8.dp), color = PricePilotPurple, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Top Categories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text("View all", color = PricePilotPurple, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToSearch))
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniCategory("Mobiles", Icons.Default.PhoneAndroid, PricePilotBlue)
                    MiniCategory("Laptops", Icons.Default.Laptop, PricePilotPurple)
                    MiniCategory("TVs", Icons.Default.Tv, PricePilotPink)
                    MiniCategory("Audio", Icons.Default.Headphones, PricePilotYellow)
                }

                Spacer(Modifier.height(22.dp))
                Card(Modifier.fillMaxWidth().clickable(onClick = onNavigateToAi), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).clip(CircleShape).background(PricePilotPurple.copy(.10f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoAwesome, null, tint = PricePilotPurple) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("PricePilot AI", fontWeight = FontWeight.ExtraBold)
                            Text("Smart insights to help you buy at the right price.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.ChevronRight, null)
                    }
                }

                if (recents.isNotEmpty()) {
                    Spacer(Modifier.height(22.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Recent searches", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Text("View all", color = PricePilotPurple, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToRecents))
                    }
                    recents.take(3).forEach { recent ->
                        Card(Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, null, tint = PricePilotPurple)
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) { Text(recent.title, fontWeight = FontWeight.Bold); Text("Best ₹${recent.lowestPrice}", color = PricePilotGreen) }
                                Icon(Icons.Default.ChevronRight, null)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(82.dp)) {
        Box(Modifier.size(52.dp).clip(CircleShape).background(tint.copy(.10f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
        Spacer(Modifier.height(5.dp))
        Text(label, maxLines = 1, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MiniCategory(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(54.dp).clip(RoundedCornerShape(18.dp)).background(tint.copy(.10f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
        Spacer(Modifier.height(6.dp)); Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}
