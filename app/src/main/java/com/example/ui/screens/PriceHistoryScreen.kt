package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.PricePilotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceHistoryScreen(
    viewModel: PricePilotViewModel,
    onBack: () -> Unit
) {
    val product = viewModel.currentComparison.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Price History & Trends") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (product == null || product.priceHistory.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Price history will appear once enough data is available.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val prices = product.priceHistory.map { it.price }
            val lowest = prices.minOrNull() ?: 0.0
            val highest = prices.maxOrNull() ?: 0.0
            val average = if (prices.isNotEmpty()) prices.average() else 0.0

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Lowest Price", style = MaterialTheme.typography.labelMedium)
                                    Text(text = "₹$lowest", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = com.example.ui.theme.BestPriceEmerald)
                                }
                                Column {
                                    Text(text = "Highest Price", style = MaterialTheme.typography.labelMedium)
                                    Text(text = "₹$highest", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                }
                                Column {
                                    Text(text = "Average", style = MaterialTheme.typography.labelMedium)
                                    Text(text = "₹${formatAmount(average)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Recorded Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(product.priceHistory) { point ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = point.date, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = point.storeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = "₹${point.price}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatAmount(value: Double): String {
    return String.format(java.util.Locale.getDefault(), "%.0f", value)
}
