package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class AppThemeTokens(
    val isDark: Boolean,
    // Background & Surfaces
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceSubtle: Color,
    val surfaceHighlight: Color,
    
    // Liquid-Glass Materials
    val glassBackground: Brush,
    val glassSurfaceElevated: Brush,
    val glassBorder: Brush,
    val glassHighlightTop: Color,
    val glassHighlightBottom: Color,
    val glassShadow: Color,
    
    // Typography / Text Contrast Safe
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textInverse: Color,
    
    // Borders & Dividers
    val border: Color,
    val borderSubtle: Color,
    val divider: Color,
    
    // Functional & Brand Accents
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val accent: Color,
    val success: Color,
    val danger: Color,
    val livePulse: Color,
    val iconTint: Color,
    val scrim: Color
)

val DarkThemeTokens = AppThemeTokens(
    isDark = true,
    background = Color(0xFF0D1117),
    surface = Color(0xFF161B22),
    surfaceElevated = Color(0xFF21262D),
    surfaceSubtle = Color(0xFF1B2028),
    surfaceHighlight = Color(0x33FFFFFF),
    
    glassBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xE61C2331),
            Color(0xF0121721)
        )
    ),
    glassSurfaceElevated = Brush.verticalGradient(
        colors = listOf(
            Color(0x38FFFFFF),
            Color(0x12FFFFFF)
        )
    ),
    glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color(0x50FFFFFF),
            Color(0x14FFFFFF)
        )
    ),
    glassHighlightTop = Color(0x40FFFFFF),
    glassHighlightBottom = Color(0x0AFFFFFF),
    glassShadow = Color(0x80000000),
    
    textPrimary = Color(0xFFF0F6FC),
    textSecondary = Color(0xFF8B949E),
    textTertiary = Color(0xFF6E7681),
    textInverse = Color(0xFF0D1117),
    
    border = Color(0x33FFFFFF),
    borderSubtle = Color(0x1AFFFFFF),
    divider = Color(0x1F8B949E),
    
    primary = Color(0xFF2F81F7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1F3A60),
    onPrimaryContainer = Color(0xFFD0E2FF),
    accent = Color(0xFFD29922),
    success = Color(0xFF2DA44E),
    danger = Color(0xFFCF222E),
    livePulse = Color(0xFFE53935),
    iconTint = Color(0xFFF0F6FC),
    scrim = Color(0xB3000000)
)

val LightThemeTokens = AppThemeTokens(
    isDark = false,
    background = Color(0xFFF6F8FA),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFF0F2F5),
    surfaceSubtle = Color(0xFFEAEEF2),
    surfaceHighlight = Color(0x0A000000),
    
    glassBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xF5FFFFFF),
            Color(0xE8F0F3F7)
        )
    ),
    glassSurfaceElevated = Brush.verticalGradient(
        colors = listOf(
            Color(0xCCFFFFFF),
            Color(0x99F4F6F9)
        )
    ),
    glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0x33000000)
        )
    ),
    glassHighlightTop = Color(0x80FFFFFF),
    glassHighlightBottom = Color(0x0D000000),
    glassShadow = Color(0x24000000),
    
    textPrimary = Color(0xFF1F2328),
    textSecondary = Color(0xFF59636E),
    textTertiary = Color(0xFF7D8590),
    textInverse = Color(0xFFFFFFFF),
    
    border = Color(0x26000000),
    borderSubtle = Color(0x12000000),
    divider = Color(0x14000000),
    
    primary = Color(0xFF0969DA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF4FF),
    onPrimaryContainer = Color(0xFF054DA7),
    accent = Color(0xFFB07D10),
    success = Color(0xFF1A7F37),
    danger = Color(0xFFCF222E),
    livePulse = Color(0xFFE52217),
    iconTint = Color(0xFF1F2328),
    scrim = Color(0x4D000000)
)

val LocalAppThemeTokens = staticCompositionLocalOf<AppThemeTokens> {
    DarkThemeTokens
}
