package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
    // Set dynamicColor default to false so app maintains its curated palette across all devices
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
