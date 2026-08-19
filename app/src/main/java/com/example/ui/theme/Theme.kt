package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FrostedGlassColorScheme =
  darkColorScheme(
    primary = GlassPrimaryDark,
    onPrimary = GlassBackground,
    primaryContainer = GlassPrimaryContainer,
    onPrimaryContainer = GlassOnPrimaryContainer,
    secondary = GlassSecondary,
    onSecondary = GlassBackground,
    secondaryContainer = GlassSecondaryContainer,
    onSecondaryContainer = GlassOnSecondaryContainer,
    tertiary = GlassTertiary,
    onTertiary = GlassBackground,
    tertiaryContainer = GlassTertiaryContainer,
    onTertiaryContainer = GlassOnTertiaryContainer,
    background = GlassBackground,
    surface = GlassSurface,
    surfaceVariant = GlassSurfaceElevated,
    outline = GlassBorder,
    outlineVariant = GlassBorderSubtle,
    onBackground = GlassTextPrimary,
    onSurface = GlassTextPrimary,
    onSurfaceVariant = GlassTextSecondary
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = FrostedGlassColorScheme,
    typography = Typography,
    content = content
  )
}


