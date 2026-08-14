package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.PricePilotViewModel
import kotlin.math.abs
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceHistoryScreen(viewModel: PricePilotViewModel, onBack: () -> Unit) {
    val product = viewModel.currentComparison.collectAsState().value
    val context = LocalContext.current
    Scaffold(topBar = { TopAppBar(title = { Text("Price History", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, "Back") } }) }) { padding ->
        if (product == null || product.offers.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
                Text("No product price is available yet.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Search or compare a product first.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val actualPoints = product.priceHistory.sortedBy { it.date }
            val hasActualHistory = actualPoints.isNotEmpty()
            val current = product.offers.minOf { it.currentPrice }
            val estimatedPrices = listOf(1.08, 1.05, 1.03, 1.00, .98, 1.01, .97).map { current * it }
            val prices = if (hasActualHistory) actualPoints.map { it.price } else estimatedPrices
            val lowest = prices.min()
            val highest = prices.max()
            val average = prices.average()
            val recent = prices.takeLast(minOf(3, prices.size)).average()
            val old = prices.take(minOf(3, prices.size)).average()
            val dropping = recent < old
            val change = if (old == 0.0) 0.0 else ((recent - old) / old) * 100.0
            val animatedProgress by animateFloatAsState(1f, tween(900), label = "chartReveal")
            val prediction = when {
                hasActualHistory && prices.size >= 4 && dropping && abs(change) >= 3 -> "Recorded data shows a downward trend. A further drop is possible if this trend continues."
                hasActualHistory && prices.size >= 4 && !dropping && change >= 3 -> "Recorded data shows an upward trend. Consider buying if today's price is attractive."
                hasActualHistory -> "Recorded prices are relatively stable; there is no strong near-term direction."
                else -> "Estimated trend only. This is not a record of past prices and is not a guarantee of future prices."
            }

            fun openGoogleHistory() {
                val q = Uri.encode("${product.title} price history India ${product.offers.joinToString(" ") { it.storeName }}")
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$q")))
            }

            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), elevation = CardDefaults.cardElevation(5.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text(product.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, maxLines = 2)
                            Spacer(Modifier.height(10.dp))
                            Surface(color = if (hasActualHistory) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(14.dp)) {
                                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Info, null)
                                    Text(if (hasActualHistory) "Verified recorded price observations" else "Estimated price trend — not verified historical data", fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Stat(if (hasActualHistory) "LOWEST" else "EST. LOW", "₹${formatAmount(lowest)}", MaterialTheme.colorScheme.primary)
                                Stat(if (hasActualHistory) "HIGHEST" else "EST. HIGH", "₹${formatAmount(highest)}", MaterialTheme.colorScheme.error)
                                Stat(if (hasActualHistory) "AVERAGE" else "EST. AVG", "₹${formatAmount(average)}", MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(if (hasActualHistory) "Recorded price history" else "Estimated price trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                                AssistChip(onClick = { openGoogleHistory() }, label = { Text("Google check") }, leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(18.dp)) })
                            }
                            Spacer(Modifier.height(12.dp))
                            PriceChart(prices, animatedProgress)
                            Spacer(Modifier.height(8.dp))
                            Text("Current cheapest: ₹${formatAmount(current)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            if (!hasActualHistory) {
                                Spacer(Modifier.height(5.dp))
                                Text("Google can help you verify public price-history pages, but Google Search does not provide a reliable structured historical-price API. PricePilot will never label an estimate as verified history.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(10.dp))
                                OutlinedButton(onClick = { openGoogleHistory() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp)) {
                                    Icon(Icons.Default.OpenInNew, null)
                                    Spacer(Modifier.width(7.dp))
                                    Text("Check this product's history on Google")
                                }
                            }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (dropping) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(22.dp)) {
                        Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(if (dropping) Icons.Default.TrendingDown else Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.primary)
                            Column { Text(if (hasActualHistory) if (dropping) "Price is trending down" else "Price trend is not clearly falling" else "Estimated price direction", fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(4.dp)); Text(prediction, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
                if (hasActualHistory) {
                    item { Text("Recorded timeline", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) }
                    items(actualPoints) { point ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                            Row(Modifier.fillMaxWidth().padding(15.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column { Text(point.date, fontWeight = FontWeight.Bold); Text(point.storeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                Text("₹${formatAmount(point.price)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String, color: Color) {
    Column { Text(label, style = MaterialTheme.typography.labelSmall); Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = color) }
}

@Composable
private fun PriceChart(values: List<Double>, progress: Float) {
    val min = values.minOrNull() ?: 0.0
    val max = values.maxOrNull() ?: min + 1.0
    val chartBackground = MaterialTheme.colorScheme.surfaceVariant
    val chartColor = MaterialTheme.colorScheme.primary
    Canvas(Modifier.fillMaxWidth().height(190.dp).background(chartBackground, RoundedCornerShape(18.dp))) {
        if (values.size < 2) return@Canvas
        val range = (max - min).takeIf { it > 0 } ?: 1.0
        val usable = size.width - 24f
        val path = Path()
        values.forEachIndexed { i, value ->
            val x = 12f + i.toFloat() / (values.lastIndex.coerceAtLeast(1)) * usable
            val y = size.height - 12f - (((value - min) / range).toFloat() * (size.height - 24f))
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, chartColor.copy(alpha = progress), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 7f, cap = StrokeCap.Round))
        values.forEachIndexed { i, value ->
            val x = 12f + i.toFloat() / (values.lastIndex.coerceAtLeast(1)) * usable
            val y = size.height - 12f - (((value - min) / range).toFloat() * (size.height - 24f))
            drawCircle(chartColor.copy(alpha = progress), 6f, Offset(x, y))
        }
    }
}

private fun formatAmount(value: Double): String = String.format(Locale.getDefault(), "%.0f", value)
