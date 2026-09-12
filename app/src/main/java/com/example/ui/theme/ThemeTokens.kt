package com.example.ui.theme

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    val glassTint: Color = if (isDark) AppColors.DarkGlassTint else AppColors.LightGlassTint,
    val glassOpacity: Float = if (isDark) 0.82f else 0.92f,
    val glassSpecularBorder: Color = if (isDark) AppColors.DarkGlassBorder else AppColors.LightGlassBorder,
    
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
    val brandSaffron: Color = AppColors.BrandSaffron,
    val success: Color,
    val warning: Color = AppColors.WarningAmber,
    val danger: Color,
    val livePulse: Color,
    val iconTint: Color,
    val scrim: Color,

    // Consistent Shape & Corner Radii
    val cardRadius: Dp = 20.dp,
    val dockRadius: Dp = 24.dp,
    val buttonRadius: Dp = 12.dp,
    val chipRadius: Dp = 10.dp,
    val pillRadius: Dp = 16.dp,

    // Spacing Rhythm Tokens
    val spacingXs: Dp = 4.dp,
    val spacingSm: Dp = 8.dp,
    val spacingMd: Dp = 16.dp,
    val spacingLg: Dp = 24.dp,
    val spacingXl: Dp = 32.dp,

    // Motion Durations & Easing
    val durationShort: Int = 180,
    val durationMedium: Int = 300,
    val durationLong: Int = 420,
    val standardEasing: Easing = FastOutSlowInEasing
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
    glassTint = AppColors.DarkGlassTint,
    glassOpacity = 0.82f,
    glassSpecularBorder = AppColors.DarkGlassBorder,
    
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
    brandSaffron = Color(0xFFFF9933),
    success = Color(0xFF2DA44E),
    warning = Color(0xFFD29922),
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
    glassTint = AppColors.LightGlassTint,
    glassOpacity = 0.92f,
    glassSpecularBorder = AppColors.LightGlassBorder,
    
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
    brandSaffron = Color(0xFFFF9933),
    success = Color(0xFF1A7F37),
    warning = Color(0xFFD29922),
    danger = Color(0xFFCF222E),
    livePulse = Color(0xFFE52217),
    iconTint = Color(0xFF1F2328),
    scrim = Color(0x4D000000)
)

val LocalAppThemeTokens = staticCompositionLocalOf<AppThemeTokens> {
    DarkThemeTokens
}

/**
 * Linearly interpolates between two AppThemeTokens based on fraction (0f..1f).
 */
fun lerpTokens(start: AppThemeTokens, stop: AppThemeTokens, fraction: Float): AppThemeTokens {
    val f = fraction.coerceIn(0f, 1f)
    if (f <= 0f) return start
    if (f >= 1f) return stop
    val isDark = if (f < 0.5f) start.isDark else stop.isDark
    return AppThemeTokens(
        isDark = isDark,
        background = lerp(start.background, stop.background, f),
        surface = lerp(start.surface, stop.surface, f),
        surfaceElevated = lerp(start.surfaceElevated, stop.surfaceElevated, f),
        surfaceSubtle = lerp(start.surfaceSubtle, stop.surfaceSubtle, f),
        surfaceHighlight = lerp(start.surfaceHighlight, stop.surfaceHighlight, f),
        glassBackground = if (f < 0.5f) start.glassBackground else stop.glassBackground,
        glassSurfaceElevated = if (f < 0.5f) start.glassSurfaceElevated else stop.glassSurfaceElevated,
        glassBorder = if (f < 0.5f) start.glassBorder else stop.glassBorder,
        glassHighlightTop = lerp(start.glassHighlightTop, stop.glassHighlightTop, f),
        glassHighlightBottom = lerp(start.glassHighlightBottom, stop.glassHighlightBottom, f),
        glassShadow = lerp(start.glassShadow, stop.glassShadow, f),
        glassTint = lerp(start.glassTint, stop.glassTint, f),
        glassOpacity = start.glassOpacity + (stop.glassOpacity - start.glassOpacity) * f,
        glassSpecularBorder = lerp(start.glassSpecularBorder, stop.glassSpecularBorder, f),
        textPrimary = lerp(start.textPrimary, stop.textPrimary, f),
        textSecondary = lerp(start.textSecondary, stop.textSecondary, f),
        textTertiary = lerp(start.textTertiary, stop.textTertiary, f),
        textInverse = lerp(start.textInverse, stop.textInverse, f),
        border = lerp(start.border, stop.border, f),
        borderSubtle = lerp(start.borderSubtle, stop.borderSubtle, f),
        divider = lerp(start.divider, stop.divider, f),
        primary = lerp(start.primary, stop.primary, f),
        onPrimary = lerp(start.onPrimary, stop.onPrimary, f),
        primaryContainer = lerp(start.primaryContainer, stop.primaryContainer, f),
        onPrimaryContainer = lerp(start.onPrimaryContainer, stop.onPrimaryContainer, f),
        accent = lerp(start.accent, stop.accent, f),
        brandSaffron = lerp(start.brandSaffron, stop.brandSaffron, f),
        success = lerp(start.success, stop.success, f),
        warning = lerp(start.warning, stop.warning, f),
        danger = lerp(start.danger, stop.danger, f),
        livePulse = lerp(start.livePulse, stop.livePulse, f),
        iconTint = lerp(start.iconTint, stop.iconTint, f),
        scrim = lerp(start.scrim, stop.scrim, f)
    )
}
