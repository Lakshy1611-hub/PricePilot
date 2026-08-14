package com.example.ui.screens

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
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.PricePilotViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceHistoryScreen(viewModel: PricePilotViewModel, onBack: () -> Unit) {
    val product = viewModel.currentComparison.collectAsState().value
    Scaffold(topBar = { TopAppBar(title = { Text("Price History", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onBack) { Icon(Icons.Default.ArrowBack, "Back") } }) }) { padding ->
        if (product == null || product.priceHistory.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
                Text("Real price history will appear when the price service provides historical observations.", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("PricePilot does not invent historical prices.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val points = product.priceHistory.sortedBy { it.date }
            val prices = points.map { it.price }
            val lowest = prices.min()
            val highest = prices.max()
            val average = prices.average()
            val current = product.offers.minOf { it.currentPrice }
            val recent = prices.takeLast(minOf(3, prices.size)).average()
            val old = prices.take(minOf(3, prices.size)).average()
            val dropping = recent < old
            val change = if (old == 0.0) 0.0 else ((recent - old) / old) * 100.0
            val animatedProgress by animateFloatAsState(1f, tween(900), label = "chartReveal")
            val prediction = when {
                prices.size < 4 -> "Not enough history for a reliable prediction"
                dropping && abs(change) >= 3 -> "Downward trend — a further drop is possible if this trend continues"
                !dropping && change >= 3 -> "Upward trend — consider buying if today's price is already attractive"
                else -> "Stable trend — no strong near-term direction detected"
            }

            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), elevation = CardDefaults.cardElevation(5.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text(product.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, maxLines = 2)
                            Spacer(Modifier.height(14.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Stat("LOWEST", "₹${formatAmount(lowest)}", MaterialTheme.colorScheme.primary)
                                Stat("HIGHEST", "₹${formatAmount(highest)}", MaterialTheme.colorScheme.error)
                                Stat("AVERAGE", "₹${formatAmount(average)}", MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Actual recorded prices", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(12.dp))
                            PriceChart(prices, animatedProgress)
                            Spacer(Modifier.height(8.dp))
                            Text("Current cheapest: ₹${formatAmount(current)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (dropping) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(22.dp)) {
                        Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(if (dropping) Icons.Default.TrendingDown else Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.primary)
                            Column { Text(if (dropping) "Price is trending down" else "Price is not clearly falling", fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(4.dp)); Text(prediction, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
                item { Text("Recorded timeline", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) }
                items(points) { point ->
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

@Composable
private fun Stat(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column { Text(label, style = MaterialTheme.typography.labelSmall); Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = color) }
}

@Composable
private fun PriceChart(values: List<Double>, progress: Float) {
    val min = values.minOrNull() ?: 0.0
    val max = values.maxOrNull() ?: min + 1.0
    Canvas(Modifier.fillMaxWidth().height(190.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(18.dp)).padding(12.dp)) {
        if (values.size < 2) return@Canvas
        val range = (max - min).takeIf { it > 0 } ?: 1.0
        val usable = size.width
        val path = Path()
        values.forEachIndexed { i, value ->
            val x = i.toFloat() / (values.lastIndex.coerceAtLeast(1)) * usable
            val y = size.height - (((value - min) / range).toFloat() * size.height)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, MaterialTheme.colorScheme.primary.copy(alpha = progress), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 7f, cap = StrokeCap.Round))
        values.forEachIndexed { i, value ->
            val x = i.toFloat() / (values.lastIndex.coerceAtLeast(1)) * usable
            val y = size.height - (((value - min) / range).toFloat() * size.height)
            drawCircle(MaterialTheme.colorScheme.primary.copy(alpha = progress), 6f, Offset(x, y))
        }
    }
}

private fun formatAmount(value: Double): String = String.format(java.util.Locale.getDefault(), "%.0f", value)
