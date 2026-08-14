package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.extraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Welcome to PricePilot",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Compare prices instantly across Amazon, Flipkart, Myntra, Croma, and more to never overpay again.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Feature items
                OnboardingFeatureRow(
                    icon = Icons.Default.TrendingDown,
                    title = "Lowest Price Guarantee",
                    subtitle = "Instantly highlights the cheapest valid store offer."
                )
                Spacer(modifier = Modifier.height(16.dp))
                OnboardingFeatureRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Smart Price History",
                    subtitle = "Track price trends and spot genuine discounts."
                )
            }

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OnboardingFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}
