package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.ui.components.ShoppingTrolleyLoading
import com.example.ui.theme.PremiumDesign
import com.example.ui.viewmodel.PricePilotViewModel

@Composable
fun HomeScreen(viewModel: PricePilotViewModel, onNavigateToResults: () -> Unit, onNavigateToSearch: () -> Unit, onNavigateToWishlist: () -> Unit, onNavigateToSettings: () -> Unit, onNavigateToRecents: () -> Unit, onNavigateToAi: () -> Unit = {}, onNavigateToAlerts: () -> Unit = {}, onNavigateToDeals: () -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    var extracting by remember { mutableStateOf(false) }
    val recents by viewModel.recentComparisons.collectAsState()
    val progress by animateFloatAsState(if (extracting) 0.72f else 0f, tween(700, easing = FastOutSlowInEasing), label = "searchProgress")
    fun search() { if (query.isBlank()) return; extracting = true; viewModel.searchProducts(query.trim()); onNavigateToSearch() }
    Scaffold(containerColor = PremiumDesign.Page, bottomBar = {
        NavigationBar(containerColor = PremiumDesign.Surface, tonalElevation = 0.dp, modifier = Modifier.shadow(14.dp)) {
            PremiumNavItem(Icons.Default.Home, "Home", true) {}
            PremiumNavItem(Icons.Default.LocalOffer, "Deals", false, onNavigateToDeals)
            PremiumNavItem(Icons.Default.NotificationsNone, "Alerts", false, onNavigateToAlerts)
            PremiumNavItem(Icons.Default.FavoriteBorder, "Watchlist", false, onNavigateToWishlist)
            PremiumNavItem(Icons.Default.PersonOutline, "Profile", false, onNavigateToSettings)
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            Column(Modifier.padding(horizontal = PremiumDesign.PagePadding, vertical = 12.dp)) {
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text("Hello, Lakshy 👋", fontWeight = FontWeight.ExtraBold, color = PremiumDesign.Ink, style = MaterialTheme.typography.titleLarge); Text("Find the best deals for you", color = PremiumDesign.Muted, style = MaterialTheme.typography.bodySmall) }
                    Box(Modifier.size(46.dp).clip(CircleShape).background(PremiumDesign.PurpleSoft).clickable(onClick = onNavigateToAi), contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoAwesome, "AI Shopping", tint = PremiumDesign.Purple) }
                }
                Spacer(Modifier.height(16.dp))
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = PremiumDesign.Surface, shadowElevation = 3.dp) {
                    OutlinedTextField(value = query, onValueChange = { query = it; extracting = false }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search or paste any product link", color = PremiumDesign.Muted) }, leadingIcon = { Icon(Icons.Default.Search, null, tint = PremiumDesign.Purple) }, trailingIcon = { IconButton(onClick = { search() }) { Icon(Icons.Default.Mic, "Search by voice", tint = PremiumDesign.Purple) } }, singleLine = true, shape = RoundedCornerShape(22.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PremiumDesign.Purple, unfocusedBorderColor = Color.Transparent, focusedContainerColor = PremiumDesign.Surface, unfocusedContainerColor = PremiumDesign.Surface), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { search() }))
                }
                AnimatedVisibility(extracting, enter = fadeIn(tween(250)) + slideInVertically(tween(350)) { -it / 4 }) {
                    Card(Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(PremiumDesign.CardRadius), colors = CardDefaults.cardColors(containerColor = PremiumDesign.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(46.dp).clip(CircleShape).background(PremiumDesign.PurpleSoft), contentAlignment = Alignment.Center) { Icon(Icons.Default.Link, null, tint = PremiumDesign.Purple) }
                                Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("Extracting product information", fontWeight = FontWeight.ExtraBold, color = PremiumDesign.Ink); Text(query, maxLines = 1, color = PremiumDesign.Purple, style = MaterialTheme.typography.bodySmall) }; Text("${(progress * 100).toInt()}%", color = PremiumDesign.Purple, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(12.dp)); ShoppingTrolleyLoading(Modifier.fillMaxWidth(), progress = progress); Spacer(Modifier.height(10.dp)); ExtractionStep("Initializing link", true); ExtractionStep("Extracting product information", progress > 0.2f); ExtractionStep("Comparing stores and prices", progress > 0.65f)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp)); Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(14.dp)) { CategoryChip("All", Icons.Default.ShoppingBag, PremiumDesign.Purple); CategoryChip("Electronics", Icons.Default.Headphones, PremiumDesign.Blue); CategoryChip("Fashion", Icons.Default.Checkroom, PremiumDesign.Pink); CategoryChip("Home & Kitchen", Icons.Default.Home, PremiumDesign.Green); CategoryChip("More", Icons.Default.MoreHoriz, PremiumDesign.Orange) }
                Spacer(Modifier.height(20.dp)); PremiumHeroCard(onClick = onNavigateToDeals)
                Spacer(Modifier.height(22.dp)); SectionHeader("Trending Deals", "View all", onNavigateToDeals); Spacer(Modifier.height(12.dp)); Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) { DealMiniCard("Wireless Earbuds", "₹1,299", "12% OFF", Icons.Default.Headphones); DealMiniCard("Smart Watch", "₹2,499", "28% OFF", Icons.Default.Watch); DealMiniCard("Laptop", "₹49,990", "18% OFF", Icons.Default.Laptop) }
                Spacer(Modifier.height(22.dp)); SectionHeader("Top Categories", "View all", onNavigateToSearch); Spacer(Modifier.height(12.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { MiniCategory("Mobiles", Icons.Default.PhoneAndroid, PremiumDesign.Blue); MiniCategory("Laptops", Icons.Default.Laptop, PremiumDesign.Purple); MiniCategory("TVs", Icons.Default.Tv, PremiumDesign.Pink); MiniCategory("Audio", Icons.Default.Headphones, PremiumDesign.Orange) }
                Spacer(Modifier.height(20.dp)); Card(Modifier.fillMaxWidth().clickable(onClick = onNavigateToAi), shape = RoundedCornerShape(PremiumDesign.CardRadius), colors = CardDefaults.cardColors(containerColor = PremiumDesign.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(54.dp).clip(RoundedCornerShape(18.dp)).background(PremiumDesign.PurpleSoft), contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoAwesome, null, tint = PremiumDesign.Purple) }; Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text("AI Smart Shopper", fontWeight = FontWeight.ExtraBold, color = PremiumDesign.Ink); Text("Get smart price insights and buying suggestions.", color = PremiumDesign.Muted, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Default.ChevronRight, null, tint = PremiumDesign.Purple) } }
                if (recents.isNotEmpty()) { Spacer(Modifier.height(22.dp)); SectionHeader("Recent searches", "View all", onNavigateToRecents); recents.take(3).forEach { recent -> Card(Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(PremiumDesign.SmallRadius), colors = CardDefaults.cardColors(containerColor = PremiumDesign.Surface)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(PremiumDesign.PurpleSoft), contentAlignment = Alignment.Center) { Icon(Icons.Default.History, null, tint = PremiumDesign.Purple) }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(recent.title, maxLines = 1, fontWeight = FontWeight.Bold, color = PremiumDesign.Ink); Text("Best ₹${recent.lowestPrice}", color = PremiumDesign.Green, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Default.ChevronRight, null, tint = PremiumDesign.Muted) } } } }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun PremiumNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.width(72.dp).clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(12.dp)).background(if (selected) PremiumDesign.PurpleSoft else Color.Transparent),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = label, tint = if (selected) PremiumDesign.Purple else PremiumDesign.Muted) }
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) PremiumDesign.Purple else PremiumDesign.Muted)
    }
}

@Composable private fun ExtractionStep(text: String, done: Boolean) { Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) { Box(Modifier.size(8.dp).clip(CircleShape).background(if (done) PremiumDesign.Green else PremiumDesign.Border)); Spacer(Modifier.width(9.dp)); Text(text, color = if (done) PremiumDesign.Ink else PremiumDesign.Muted, style = MaterialTheme.typography.bodySmall) } }
@Composable private fun PremiumHeroCard(onClick: () -> Unit) { Card(Modifier.fillMaxWidth().height(188.dp).clickable(onClick = onClick), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = PremiumDesign.Purple)) { Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(PremiumDesign.PurpleDark, PremiumDesign.Purple, PremiumDesign.Blue)))) { Column(Modifier.padding(20.dp)) { Surface(color = Color.White.copy(alpha = .95f), shape = RoundedCornerShape(10.dp)) { Text("MEGA DEALS", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = PremiumDesign.Purple, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelMedium) }; Spacer(Modifier.height(10.dp)); Text("Best Prices\nOn Top Products", color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineSmall); Spacer(Modifier.height(5.dp)); Text("Handpicked deals across top stores", color = Color.White.copy(alpha = .82f), style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(10.dp)); Surface(color = Color.White, shape = RoundedCornerShape(14.dp)) { Text("Explore Deals  ›", Modifier.padding(horizontal = 14.dp, vertical = 8.dp), color = PremiumDesign.Purple, fontWeight = FontWeight.Bold) } } } } }
@Composable private fun SectionHeader(title: String, action: String, onClick: () -> Unit) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = PremiumDesign.Ink); Text(action, color = PremiumDesign.Purple, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onClick)) } }
@Composable private fun CategoryChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(84.dp)) { Box(Modifier.size(54.dp).clip(CircleShape).background(tint.copy(.11f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }; Spacer(Modifier.height(5.dp)); Text(label, maxLines = 1, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = PremiumDesign.Ink) } }
@Composable private fun MiniCategory(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(78.dp)) { Box(Modifier.size(54.dp).clip(RoundedCornerShape(18.dp)).background(tint.copy(.10f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }; Spacer(Modifier.height(6.dp)); Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = PremiumDesign.Ink) } }
@Composable private fun DealMiniCard(title: String, price: String, discount: String, icon: androidx.compose.ui.graphics.vector.ImageVector) { Card(Modifier.width(166.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = PremiumDesign.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) { Column(Modifier.padding(12.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(PremiumDesign.SurfaceSoft), contentAlignment = Alignment.Center) { Icon(icon, null, tint = PremiumDesign.Purple) }; Surface(color = PremiumDesign.GreenSoft, shape = RoundedCornerShape(8.dp)) { Text(discount, Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = PremiumDesign.Green, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) } }; Spacer(Modifier.height(10.dp)); Text(title, maxLines = 1, fontWeight = FontWeight.Bold, color = PremiumDesign.Ink); Text(price, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = PremiumDesign.Ink) } } }