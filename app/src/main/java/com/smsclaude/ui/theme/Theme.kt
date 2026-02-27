package com.smsclaude.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ElectricTeal,
    onPrimary = DeepCharcoal,
    primaryContainer = TealGlow,
    onPrimaryContainer = ElectricTeal,
    secondary = TealDim,
    onSecondary = DeepCharcoal,
    background = DeepCharcoal,
    onBackground = OnSurface,
    surface = SurfaceCard,
    onSurface = OnSurface,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = OnSurfaceMuted,
    error = RedError,
    onError = DeepCharcoal,
    outline = BorderColor
)

@Composable
fun SmsForwarderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
