package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Preserve the legacy token contract while applying one consistent presentation palette.
val RefinedDarkTokens = DarkThemeTokens.copy(
    background = Color(0xFF141416), surface = Color(0xFF202023),
    surfaceElevated = Color(0xFF2C2C30), surfaceSubtle = Color(0xFF27272B),
    textPrimary = Color(0xFFF5F5F7), textSecondary = Color(0xFFB9B9C0),
    textTertiary = Color(0xFFA3A3AD), border = Color(0x24FFFFFF),
    borderSubtle = Color(0x12FFFFFF), divider = Color(0x14FFFFFF),
    danger = Color(0xFFFF8A85)
)
val RefinedLightTokens = LightThemeTokens.copy(
    background = Color(0xFFF5F4F1), surface = Color.White,
    surfaceElevated = Color(0xFFF0EFEC), surfaceSubtle = Color(0xFFF5F4F2),
    textPrimary = Color(0xFF202024), textSecondary = Color(0xFF62626B),
    textTertiary = Color(0xFF6F6F78)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // The reveal composites old/new themes spatially; do not crossfade the palette.
    val tokens = if (darkTheme) RefinedDarkTokens else RefinedLightTokens
    val colors = (if (darkTheme) darkColorScheme() else lightColorScheme()).copy(
        primary = tokens.primary, onPrimary = tokens.onPrimary,
        primaryContainer = tokens.primaryContainer, onPrimaryContainer = tokens.onPrimaryContainer,
        secondary = tokens.textSecondary, onSecondary = tokens.textInverse,
        background = tokens.background, onBackground = tokens.textPrimary,
        surface = tokens.surface, onSurface = tokens.textPrimary,
        surfaceVariant = tokens.surfaceElevated, onSurfaceVariant = tokens.textSecondary,
        outline = tokens.border, outlineVariant = tokens.borderSubtle, error = tokens.danger
    )
    CompositionLocalProvider(LocalAppThemeTokens provides tokens) {
        MaterialTheme(colorScheme = colors, typography = Typography, content = content)
    }
}
