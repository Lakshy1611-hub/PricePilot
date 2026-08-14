package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class PriceAlertUi(val product: String, val current: Double, val target: Double, val active: Boolean = true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(onBack: () -> Unit) {
    var productName by remember { mutableStateOf("") }
    var targetPrice by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    val alerts = remember { mutableStateListOf<PriceAlertUi>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Price Alerts", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { showAdd = true }, icon = { Icon(Icons.Default.AddAlert, null) }, text = { Text("New Alert", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, null, Modifier.size(38.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Never miss a price drop", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(4.dp))
                            Text("Set your target and keep your favourite products on your radar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            if (alerts.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                        Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.TrendingDown, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(10.dp))
                            Text("No alerts yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Text("Create an alert when you want a product to reach a specific price.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(alerts) { alert ->
                    var enabled by remember(alert.product) { mutableStateOf(alert.active) }
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(3.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(alert.product, fontWeight = FontWeight.ExtraBold, maxLines = 2)
                                    Spacer(Modifier.height(5.dp))
                                    Text("Current ₹${alert.current.toInt()}  •  Target ₹${alert.target.toInt()}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(checked = enabled, onCheckedChange = { enabled = it })
                            }
                            Spacer(Modifier.height(10.dp))
                            LinearProgressIndicator(progress = { (alert.target / alert.current).coerceIn(0.05, 1.0).toFloat() }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(if (enabled) "Alert active" else "Alert paused", fontWeight = FontWeight.Bold, color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                IconButton(onClick = { alerts.remove(alert) }) { Icon(Icons.Default.DeleteOutline, "Delete alert") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Create price alert", fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Product name") }, singleLine = true)
                    OutlinedTextField(value = targetPrice, onValueChange = { value -> targetPrice = value.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Target price (₹)") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    val target = targetPrice.toDoubleOrNull()
                    if (productName.isNotBlank() && target != null && target > 0) {
                        alerts.add(PriceAlertUi(productName.trim(), target * 1.15, target))
                        productName = ""
                        targetPrice = ""
                        showAdd = false
                    }
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }
        )
    }
}
