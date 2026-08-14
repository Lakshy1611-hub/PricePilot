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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.PricePilotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PricePilotViewModel,
    onNavigateHome: () -> Unit,
    onNavigateSearch: () -> Unit,
    onNavigateWishlist: () -> Unit,
    onNavigateAbout: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
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
                    selected = false,
                    onClick = onNavigateSearch
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
                    selected = true,
                    onClick = {}
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Theme Mode", fontWeight = FontWeight.Bold)
                            Text(text = themeMode, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        androidx.compose.material3.FilterChip(
                            selected = themeMode == "System Default",
                            onClick = { viewModel.setThemeMode("System Default") },
                            label = { Text("System") }
                        )
                        androidx.compose.material3.FilterChip(
                            selected = themeMode == "Light",
                            onClick = { viewModel.setThemeMode("Light") },
                            label = { Text("Light") }
                        )
                        androidx.compose.material3.FilterChip(
                            selected = themeMode == "Dark",
                            onClick = { viewModel.setThemeMode("Dark") },
                            label = { Text("Dark") }
                        )
                    }
                }
            }

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Price Drop Alerts", fontWeight = FontWeight.Bold)
                        Text(text = "Get notified when items in wishlist drop in price", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) }
                    )
                }
            }

            Text(
                text = "Data Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.clearRecentComparisons() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(text = "Clear Recent Comparisons", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.clearWishlist() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(text = "Clear Wishlist", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }

            Text(
                text = "About & Legal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateAbout() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "About PricePilot", fontWeight = FontWeight.Bold)
                        Text(text = "Version 1.0 • Developer: Lakshy", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
