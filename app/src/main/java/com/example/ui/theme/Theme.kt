package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = TurquoisePrimary,
    onPrimary = TurquoiseOnPrimary,
    primaryContainer = TurquoisePrimaryContainer,
    onPrimaryContainer = TurquoiseOnPrimaryContainer,
    secondary = TurquoiseSecondary,
    onSecondary = TurquoiseOnSecondary,
    secondaryContainer = TurquoiseSecondaryContainer,
    onSecondaryContainer = TurquoiseOnSecondaryContainer,
    tertiary = TurquoiseTertiary,
    onTertiary = TurquoiseOnTertiary,
    tertiaryContainer = TurquoiseTertiaryContainer,
    onTertiaryContainer = TurquoiseOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to dark theme with turquoise accents as requested
  content: @Composable () -> Unit,
) {
  // Always use the customized turquoise dark color scheme for this app
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

