package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {
    var start by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (start) 1f else 0f, tween(850), label = "alpha")
    val scale by animateFloatAsState(if (start) 1f else .72f, tween(850), label = "scale")
    val transition = rememberInfiniteTransition(label = "brand")
    val floatY by transition.animateFloat(-10f, 10f, infiniteRepeatable(tween(1800), RepeatMode.Reverse), label = "float")
    val arrow by transition.animateFloat(-4f, 4f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "arrow")
    LaunchedEffect(Unit) { start = true; delay(1800); onNavigateToOnboarding() }

    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primaryContainer))), contentAlignment = Alignment.Center) {
        Box(Modifier.size(230.dp).offset(x = 115.dp, y = (-220).dp + floatY.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = .10f)))
        Box(Modifier.size(150.dp).offset(x = (-145).dp, y = 260.dp - floatY.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = .08f)))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(alpha).scale(scale)) {
            Box(Modifier.size(118.dp).clip(RoundedCornerShape(34.dp)).background(MaterialTheme.colorScheme.onPrimary), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PP", color = MaterialTheme.colorScheme.primary, fontSize = 42.sp, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
                    Icon(Icons.Default.ArrowDownward, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp).offset(y = arrow.dp))
                }
            }
            Spacer(Modifier.height(22.dp))
            Text("PricePilot", color = MaterialTheme.colorScheme.onPrimary, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            Text("Compare smarter. Save more.", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .84f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
