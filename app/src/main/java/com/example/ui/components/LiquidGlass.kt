package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalAppThemeTokens

/**
 * Reusable Liquid Glass Material Foundation.
 * Provides frosted optical translucency, specular rim highlights, and ambient depth.
 *
 * Hardware Note:
 * On Android (including API 31+), RenderEffect operates strictly on the attached RenderNode
 * rather than arbitrary composited background siblings (Compose has no native backdrop-filter).
 * Without per-frame bitmap capture (prohibited for 60/120fps performance), backdrop simulation
 * is achieved via calibrated frosted translucency (tokens.glassTint), multi-stop specular borders,
 * top-rim specular reflection, and ambient elevation shadows, keeping foreground content 100% sharp.
 */
@Composable
fun LiquidGlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    tint: Color? = null,
    borderBrush: Brush? = null,
    elevation: Dp = 8.dp,
    shadowColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val tokens = LocalAppThemeTokens.current
    val resolvedTint = tint ?: tokens.glassTint
    val resolvedShadow = shadowColor ?: tokens.glassShadow

    val defaultBorderBrush = remember(tokens.isDark) {
        Brush.verticalGradient(
            colors = if (tokens.isDark) {
                listOf(
                    Color.White.copy(alpha = 0.28f),
                    tokens.borderSubtle
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.90f),
                    Color(0x18000000)
                )
            }
        )
    }
    val resolvedBorderBrush = borderBrush ?: defaultBorderBrush

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = resolvedShadow,
                ambientColor = resolvedShadow.copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(resolvedTint)
            .border(width = 1.dp, brush = resolvedBorderBrush, shape = shape)
    ) {
        // Specular Top Rim Gradient overlay for tactile glass light reflection
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            tokens.glassHighlightTop.copy(alpha = if (tokens.isDark) 0.18f else 0.35f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 80f
                    )
                )
        )

        content()
    }
}
