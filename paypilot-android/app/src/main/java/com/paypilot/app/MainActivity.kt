package com.paypilot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { PayPilotApp() } }
}

@Composable fun PayPilotApp() {
    var screen by remember { mutableStateOf("Home") }
    MaterialTheme { Scaffold(bottomBar = { NavigationBar { listOf("Home","Pay","History","Profile").forEach { label -> NavigationBarItem(selected=screen==label,onClick={screen=label},icon={Text(if(label=="Home") "⌂" else if(label=="Pay") "₹" else if(label=="History") "↕" else "●")},label={Text(label)}) } } }) { p ->
        when(screen) { "Pay" -> PayScreen(p); "History" -> HistoryScreen(p); "Profile" -> ProfileScreen(p); else -> HomeScreen(p) }
    }}
}

@Composable fun HomeScreen(p: PaddingValues) { Column(Modifier.fillMaxSize().padding(p).padding(20.dp)) {
    Text("PayPilot", style=MaterialTheme.typography.headlineMedium, fontWeight=FontWeight.Bold)
    Text("Secure payments, simply.", color=MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(24.dp))
    Card(Modifier.fillMaxWidth(), shape=RoundedCornerShape(24.dp)) { Column(Modifier.padding(22.dp)) { Text("Available balance", color=MaterialTheme.colorScheme.onSurfaceVariant); Text("₹ 0.00", style=MaterialTheme.typography.displaySmall, fontWeight=FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text("TEST / SANDBOX MODE", fontWeight=FontWeight.Bold) } }
    Spacer(Modifier.height(20.dp)); Text("Quick actions", style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold); Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) { listOf("Send","Receive","Scan QR").forEach { Action(it) } }
}}
@Composable fun Action(t:String){ OutlinedButton(onClick={},modifier=Modifier.weight(1f),shape=RoundedCornerShape(16.dp)){Text(t)} }
@Composable fun PayScreen(p:PaddingValues){ Column(Modifier.fillMaxSize().padding(p).padding(20.dp)){ Text("Send Money",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold); Spacer(Modifier.height(20.dp)); OutlinedTextField("",{},label={Text("UPI ID / recipient")},modifier=Modifier.fillMaxWidth()); Spacer(Modifier.height(12.dp)); OutlinedTextField("",{},label={Text("Amount")},modifier=Modifier.fillMaxWidth()); Spacer(Modifier.height(20.dp)); Button({},Modifier.fillMaxWidth(),shape=RoundedCornerShape(16.dp)){Text("Continue")}; Spacer(Modifier.height(14.dp)); Text("Sandbox only — no real bank transfer is performed.",color=MaterialTheme.colorScheme.error) }}
@Composable fun HistoryScreen(p:PaddingValues){ val tx=listOf("No transactions yet"); LazyColumn(Modifier.fillMaxSize().padding(p).padding(20.dp)){item{Text("Transactions",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold);Spacer(Modifier.height(18.dp))};items(tx){Text(it,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
@Composable fun ProfileScreen(p:PaddingValues){Column(Modifier.fillMaxSize().padding(p).padding(20.dp)){Text("Profile",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold);Spacer(Modifier.height(20.dp));Text("Account setup and KYC will appear here.");Spacer(Modifier.height(12.dp));Text("Live UPI is disabled until an authorized payment provider is configured.",color=MaterialTheme.colorScheme.error)}}
