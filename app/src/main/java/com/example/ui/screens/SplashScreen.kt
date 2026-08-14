package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(900),
        label = "splash_alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.72f,
        animationSpec = tween(900),
        label = "splash_scale"
    )
    val transition = rememberInfiniteTransition(label = "splash_motion")
    val floatY by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "float_y"
    )
    val orbScale by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "orb_scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1800)
        onNavigateToOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(230.dp)
                .offset(x = 110.dp, y = (-220).dp + floatY.dp)
                .scale(orbScale)
                .alpha(0.12f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary)
        )
        Box(
            Modifier
                .size(150.dp)
                .offset(x = (-150).dp, y = 260.dp - floatY.dp)
                .scale(orbScale)
                .alpha(0.10f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim).scale(scaleAnim)
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(RoundedCornerShape(34.dp))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.96f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Sell,
                    contentDescription = "PricePilot Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "PricePilot",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Compare smarter. Save more.",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.84f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
