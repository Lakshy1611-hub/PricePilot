package com.paypilot.app

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Green = Color(0xFF19C37D)
private val DeepGreen = Color(0xFF087F5B)
private val Ink = Color(0xFF10231D)
private val Glass = Color.White.copy(alpha = 0.72f)
private val GlassDark = Color(0xFF17332A).copy(alpha = 0.78f)

data class Transaction(val title: String, val subtitle: String, val amount: String, val time: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PayPilotApp() }
    }
}

@Composable
fun PayPilotApp() {
    var screen by remember { mutableStateOf("Home") }
    var dark by remember { mutableStateOf(false) }
    val transactions = remember { mutableStateListOf<Transaction>() }
    val balance = remember { mutableStateOf(0.0) }

    val colors = if (dark) darkColorScheme(primary = Green, secondary = Green, background = Color(0xFF07130F), surface = Color(0xFF10231D), onBackground = Color.White, onSurface = Color.White)
                 else lightColorScheme(primary = DeepGreen, secondary = Green, background = Color(0xFFF2FBF7), surface = Color.White, onBackground = Ink, onSurface = Ink)

    MaterialTheme(colorScheme = colors) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFE7FFF5), MaterialTheme.colorScheme.background, Color(0xFFDDF7EF))))) {
            Scaffold(containerColor = Color.Transparent, bottomBar = {
                GlassBottomBar(screen) { screen = it }
            }) { p ->
                AnimatedContent(targetState = screen, label = "screen") { target ->
                    when (target) {
                        "Pay" -> PayScreen(p, transactions, balance) { screen = "Home" }
                        "Receive" -> ReceiveScreen(p)
                        "Scan" -> ScanScreen(p) { screen = "Pay" }
                        "History" -> HistoryScreen(p, transactions)
                        "Profile" -> ProfileScreen(p, dark) { dark = !dark }
                        else -> HomeScreen(p, balance.value, transactions.size, onPay = { screen = "Pay" }, onReceive = { screen = "Receive" }, onScan = { screen = "Scan" })
                    }
                }
            }
        }
    }
}

@Composable
fun GlassBottomBar(screen: String, navigate: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp).clip(RoundedCornerShape(28.dp)).background(Glass).border(1.dp, Color.White.copy(alpha = .7f), RoundedCornerShape(28.dp)).padding(7.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf("Home" to "⌂", "Pay" to "₹", "History" to "↕", "Profile" to "●").forEach { (name, icon) ->
            val selected = screen == name
            TextButton(onClick = { navigate(name) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(icon, fontSize = 21.sp, color = if (selected) DeepGreen else Color.DarkGray)
                    Text(name, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = .8f), RoundedCornerShape(28.dp)), colors = CardDefaults.cardColors(containerColor = Glass), shape = RoundedCornerShape(28.dp), elevation = CardDefaults.cardElevation(0.dp), content = content)
}

@Composable
fun HomeScreen(p: PaddingValues, balance: Double, count: Int, onPay: () -> Unit, onReceive: () -> Unit, onScan: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(p).padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(top = 22.dp, bottom = 20.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("Good day 👋", fontSize = 14.sp); Text("PayPilot", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold) }
                Box(Modifier.size(48.dp).clip(CircleShape).background(Green.copy(alpha = .18f)), contentAlignment = Alignment.Center) { Text("PP", fontWeight = FontWeight.Bold, color = DeepGreen) }
            }
        }
        item {
            GlassCard {
                Column(Modifier.padding(24.dp)) {
                    Text("Available balance", fontSize = 14.sp)
                    Spacer(Modifier.height(5.dp))
                    Text("₹ ${String.format(Locale.US, "%.2f", balance)}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(8.dp).clip(CircleShape).background(Green)); Spacer(Modifier.width(7.dp)); Text("SANDBOX • TEST MODE", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
        item { Text("Quick actions", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAction("Send", "₹", onPay)
                QuickAction("Receive", "↓", onReceive)
                QuickAction("Scan", "▣", onScan)
            }
        }
        item {
            GlassCard { Column(Modifier.padding(20.dp)) { Text("Your PayPilot QR", fontWeight = FontWeight.Bold); Text("Receive test payments with your personal QR.", fontSize = 13.sp); Spacer(Modifier.height(12.dp)); Button(onClick = onReceive, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp)) { Text("Show my QR") } } }
        }
        item { Text("Activity", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        item { Text(if (count == 0) "No sandbox transactions yet." else "$count sandbox transaction${if (count == 1) "" else "s"}", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
fun QuickAction(label: String, icon: String, onClick: () -> Unit) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = .76f), contentColor = DeepGreen)) { Text(icon, fontSize = 21.sp) }
        Spacer(Modifier.height(5.dp)); Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PayScreen(p: PaddingValues, transactions: MutableList<Transaction>, balance: MutableState<Double>, onDone: () -> Unit) {
    var recipient by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("UPI / Mobile") }
    var message by remember { mutableStateOf("") }
    val methods = listOf("UPI / Mobile", "Bank Account", "QR Payload")
    Column(Modifier.fillMaxSize().padding(p).padding(18.dp)) {
        Text("Send money", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Text("Sandbox transfer simulator", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Glass).padding(4.dp)) { methods.forEach { m -> TextButton(onClick = { method = m }, modifier = Modifier.weight(1f)) { Text(m, fontSize = 11.sp, fontWeight = if (method == m) FontWeight.Bold else FontWeight.Normal) } } }
        Spacer(Modifier.height(14.dp))
        GlassCard {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = recipient, onValueChange = { recipient = it }, modifier = Modifier.fillMaxWidth(), label = { Text(if (method == "Bank Account") "Account number + IFSC" else if (method == "QR Payload") "UPI QR payload / UPI ID" else "UPI ID or mobile number") }, shape = RoundedCornerShape(17.dp), singleLine = true)
                OutlinedTextField(value = amount, onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } }, modifier = Modifier.fillMaxWidth(), label = { Text("Amount (₹)") }, shape = RoundedCornerShape(17.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                Button(onClick = {
                    val value = amount.toDoubleOrNull()
                    if (recipient.isBlank()) message = "Enter a recipient first."
                    else if (value == null || value <= 0) message = "Enter a valid amount."
                    else {
                        balance.value += value
                        transactions.add(0, Transaction("Sandbox payment", recipient, "+ ₹${String.format(Locale.US, "%.2f", value)}", now()))
                        message = "Test payment completed successfully."
                        recipient = ""; amount = ""
                    }
                }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp)) { Text("Pay securely • TEST") }
                if (message.isNotBlank()) Text(message, color = if (message.contains("success")) DeepGreen else MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(12.dp)); Text("No real bank/UPI transfer is made in this build.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp)); OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) { Text("Back to home") }
    }
}

@Composable
fun ReceiveScreen(p: PaddingValues) {
    val payload = "upi://pay?pa=paypilot.demo@upi&pn=PayPilot%20Demo&cu=INR"
    val qr = remember(payload) { createQr(payload, 720) }
    Column(Modifier.fillMaxSize().padding(p).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Receive", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Text("Your PayPilot QR", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(18.dp))
        GlassCard { Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            qr?.let { androidx.compose.foundation.Image(it.asImageBitmap(), contentDescription = "PayPilot QR", modifier = Modifier.size(270.dp).clip(RoundedCornerShape(22.dp))) }
            Spacer(Modifier.height(15.dp)); Text("paypilot.demo@upi", fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("PayPilot Demo", fontSize = 13.sp); Spacer(Modifier.height(12.dp)); Text("TEST QR • NO REAL MONEY", fontWeight = FontWeight.Bold, color = DeepGreen, fontSize = 11.sp)
        } }
        Spacer(Modifier.height(15.dp)); Text("Share this QR inside the app to simulate receiving a payment.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ScanScreen(p: PaddingValues, onContinue: () -> Unit) {
    var payload by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(p).padding(18.dp)) {
        Text("Scan QR", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Text("QR receiver • sandbox", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(18.dp))
        GlassCard { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = .65f)), contentAlignment = Alignment.Center) { Text("▣\n\nQR CAMERA\nTEST MODE", textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold, color = DeepGreen) }
            OutlinedTextField(value = payload, onValueChange = { payload = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Paste UPI QR payload (optional)") }, shape = RoundedCornerShape(17.dp))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) { Text("Use QR recipient") }
        } }
        Spacer(Modifier.height(12.dp)); Text("Camera scanning will be connected to an authorized QR/UPI provider for a production build.", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun HistoryScreen(p: PaddingValues, transactions: List<Transaction>) {
    LazyColumn(Modifier.fillMaxSize().padding(p).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Transactions", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold); Text("Sandbox activity", color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(8.dp)) }
        if (transactions.isEmpty()) item { GlassCard { Text("No transactions yet", Modifier.padding(20.dp)) } }
        items(transactions) { tx -> GlassCard { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(42.dp).clip(CircleShape).background(Green.copy(alpha = .18f)), contentAlignment = Alignment.Center) { Text("₹", fontWeight = FontWeight.Bold, color = DeepGreen) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(tx.title, fontWeight = FontWeight.Bold); Text(tx.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(tx.time, fontSize = 11.sp) }; Text(tx.amount, fontWeight = FontWeight.Bold, color = DeepGreen) } } }
    }
}

@Composable
fun ProfileScreen(p: PaddingValues, dark: Boolean, toggleDark: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(p).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Profile", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        GlassCard { Column(Modifier.padding(20.dp)) { Text("PayPilot Demo", fontSize = 21.sp, fontWeight = FontWeight.Bold); Text("paypilot.demo@upi", color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(12.dp)); Text("Account: Sandbox") } }
        GlassCard { Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Dark glass", fontWeight = FontWeight.Bold); Text("Switch appearance", fontSize = 12.sp) }; Switch(checked = dark, onCheckedChange = { toggleDark() }) } }
        GlassCard { Column(Modifier.padding(20.dp)) { Text("Security", fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text("MPIN / biometric security is UI-ready. Production payment authorization requires an approved provider integration.", fontSize = 13.sp) } }
    }
}

private fun now(): String = SimpleDateFormat("dd MMM • HH:mm", Locale.US).format(Date())

private fun createQr(text: String, size: Int): Bitmap? = try {
    val matrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size) for (y in 0 until size) bitmap.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
    bitmap
} catch (_: Exception) { null }
