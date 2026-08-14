package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
      primary = NaturalPrimary,
      onPrimary = NaturalOnPrimary,
      primaryContainer = NaturalPrimaryContainer,
      onPrimaryContainer = NaturalOnPrimaryContainer,
      secondary = NaturalSecondary,
      onSecondary = NaturalOnSecondary,
      secondaryContainer = NaturalSecondaryContainer,
      onSecondaryContainer = NaturalOnSecondaryContainer,
      background = BackgroundDark,
      surface = SurfaceDark,
      onSurface = OnSurfaceDark,
      surfaceVariant = SurfaceVariantDark,
      outline = OutlineDark
  )

private val LightColorScheme =
  lightColorScheme(
      primary = NaturalPrimary,
      onPrimary = NaturalOnPrimary,
      primaryContainer = NaturalPrimaryContainer,
      onPrimaryContainer = NaturalOnPrimaryContainer,
      secondary = NaturalSecondary,
      onSecondary = NaturalOnSecondary,
      secondaryContainer = NaturalSecondaryContainer,
      onSecondaryContainer = NaturalOnSecondaryContainer,
      background = BackgroundLight,
      surface = SurfaceLight,
      onSurface = OnSurfaceLight,
      surfaceVariant = SurfaceVariantLight,
      outline = OutlineLight
  )

@Composable
fun PricePilotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(
      colorScheme = colorScheme,
      typography = AppTypography,
      content = content
  )
}
