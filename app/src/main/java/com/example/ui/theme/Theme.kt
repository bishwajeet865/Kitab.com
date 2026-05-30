package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonCyan,
    secondary = SparkViolet,
    tertiary = NeonViolet,
    background = ObsidianBg,
    surface = ObsidianCard,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = GhostWhite,
    onSurface = GhostWhite,
    surfaceVariant = DarkGray,
    onSurfaceVariant = TerminalText
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}


