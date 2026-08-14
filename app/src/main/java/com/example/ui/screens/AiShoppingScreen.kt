package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.PricePilotAi
import com.example.ui.viewmodel.PricePilotViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiShoppingScreen(viewModel: PricePilotViewModel, onBack: () -> Unit) {
    val product by viewModel.currentComparison.collectAsStateWithLifecycle()
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val context = product?.let { p ->
        p.offers.joinToString("\n") { o -> "${o.storeName}: ₹${o.currentPrice}, MRP ₹${o.originalPrice}, ${o.discount}% off, rating ${o.rating}, ${o.availability}, seller ${o.sellerName}" }
    }.orEmpty()

    Scaffold(
        topBar = { TopAppBar(title = { Text("PricePilot AI", fontWeight = FontWeight.ExtraBold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, Modifier.size(38.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Shop smarter with AI", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Text("Ask about the best deal, value, price or product differences.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                answer?.let { text -> item { Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(18.dp)) { Text("AI recommendation", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold); Spacer(Modifier.height(7.dp)); Text(text) } } } }
                if (product != null) {
                    item { Text("Quick questions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold) }
                    items(listOf("Which is the best deal?", "Is this a good price?", "What should I check before buying?")) { q ->
                        AssistChip(onClick = { question = q }, label = { Text(q) })
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                OutlinedTextField(value = question, onValueChange = { question = it }, modifier = Modifier.weight(1f), placeholder = { Text("Ask PricePilot AI…") }, shape = RoundedCornerShape(20.dp), maxLines = 4)
                Spacer(Modifier.width(8.dp))
                FilledIconButton(onClick = {
                    if (question.isNotBlank() && !loading) {
                        val q = question.trim(); question = ""; loading = true
                        scope.launch {
                            answer = runCatching { PricePilotAi.ask(q, context) }.getOrElse { "AI is temporarily unavailable. Please try again." }
                            loading = false
                        }
                    }
                }) { if (loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Icon(Icons.Default.Send, "Ask") }
            }
        }
    }
}
