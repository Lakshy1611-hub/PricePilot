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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.viewmodel.PricePilotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentComparisonsScreen(
    viewModel: PricePilotViewModel,
    onBack: () -> Unit,
    onNavigateResults: () -> Unit
) {
    val recents by viewModel.recentComparisons.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Comparisons") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (recents.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearRecentComparisons() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear Recents")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (recents.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No recent searches or comparisons",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recents) { recent ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.compareProduct(recent.queryOrUrl)
                                onNavigateResults()
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = recent.imageUrl,
                                contentDescription = recent.title,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(MaterialTheme.shapes.small),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = recent.title,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Lowest: ₹${recent.lowestPrice} (${recent.storeName})",
                                    color = com.example.ui.theme.BestPriceEmerald,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
