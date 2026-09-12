package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1F3A60),
    onPrimaryContainer = Color(0xFFD0E2FF),
    secondary = Color(0xFF8B949E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF21262D),
    onSecondaryContainer = Color(0xFFC9D1D9),
    tertiary = AppColors.AccentGold,
    onTertiary = Color.Black,
    background = AppColors.DarkBackground,
    onBackground = AppColors.DarkTextPrimary,
    surface = AppColors.DarkSurface,
    onSurface = AppColors.DarkTextPrimary,
    surfaceVariant = AppColors.DarkSurfaceElevated,
    onSurfaceVariant = AppColors.DarkTextSecondary,
    outline = AppColors.DarkBorder,
    outlineVariant = AppColors.DarkBorderSubtle,
    error = AppColors.DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF4FF),
    onPrimaryContainer = Color(0xFF0969DA),
    secondary = Color(0xFF59636E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEAEEF2),
    onSecondaryContainer = Color(0xFF1F2328),
    tertiary = AppColors.AccentGold,
    onTertiary = Color.White,
    background = AppColors.LightBackground,
    onBackground = AppColors.LightTextPrimary,
    surface = AppColors.LightSurface,
    onSurface = AppColors.LightTextPrimary,
    surfaceVariant = AppColors.LightSurfaceElevated,
    onSurfaceVariant = AppColors.LightTextSecondary,
    outline = AppColors.LightBorder,
    outlineVariant = AppColors.LightBorderSubtle,
    error = AppColors.DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val wave = com.example.ui.components.LocalThemeWave.current
    val targetTokens = if (darkTheme) DarkThemeTokens else LightThemeTokens
    val targetColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val currentTokens = if (wave.isWaveActive) {
        val fromTokens = if (wave.fromDark) DarkThemeTokens else LightThemeTokens
        val toTokens = if (wave.toDark) DarkThemeTokens else LightThemeTokens
        // The general screen content transitions smoothly as the liquid wave expands outward
        val contentProgress = (wave.progress / 0.70f).coerceIn(0f, 1f)
        lerpTokens(fromTokens, toTokens, contentProgress)
    } else {
        targetTokens
    }

    val currentColorScheme = (if (currentTokens.isDark) DarkColorScheme else LightColorScheme).copy(
        background = currentTokens.background,
        surface = currentTokens.surface,
        surfaceVariant = currentTokens.surfaceElevated,
        onBackground = currentTokens.textPrimary,
        onSurface = currentTokens.textPrimary,
        onSurfaceVariant = currentTokens.textSecondary,
        primary = currentTokens.primary,
        outline = currentTokens.border,
        outlineVariant = currentTokens.borderSubtle
    )

    CompositionLocalProvider(
        LocalAppThemeTokens provides currentTokens
    ) {
        MaterialTheme(colorScheme = currentColorScheme, typography = Typography, content = content)
    }
}
