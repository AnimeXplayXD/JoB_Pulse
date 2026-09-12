package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAppThemeTokens

/**
 * JobPulse Brand Color Palette
 */
object JobPulseBrandColors {
    val IndianIndigo = Color(0xFF0B1B3D)
    val IndianIndigoLight = Color(0xFF142852)
    val WarmSaffron = Color(0xFFFF9933)
    val SaffronGlow = Color(0xFFFFB366)
    val TechCyan = Color(0xFF00A3FF)
}

/**
 * Proprietary JobPulse J-P Monogram Pulse Symbol.
 * Resolution-independent vector Canvas implementation for small & large scales.
 */
@Composable
fun JobPulseSymbol(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    primaryColor: Color? = null,
    accentColor: Color = JobPulseBrandColors.WarmSaffron,
    monochrome: Boolean = false
) {
    val tokens = LocalAppThemeTokens.current
    val resolvedPrimary = primaryColor ?: tokens.textPrimary
    val beaconColor = if (monochrome) resolvedPrimary else accentColor

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val scale = w / 100f
        val strokeLetters = Stroke(width = 5.5f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val strokePulse = Stroke(width = 4.2f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // J Path: Fluid monoline with smooth curved hook
        val jPath = Path().apply {
            moveTo(40f * scale, 30f * scale)
            lineTo(40f * scale, 64f * scale)
            cubicTo(
                40f * scale, 74f * scale,
                26f * scale, 74f * scale,
                26f * scale, 64f * scale
            )
            lineTo(26f * scale, 58f * scale)
        }
        drawPath(jPath, resolvedPrimary, style = strokeLetters)

        // P Path: Upright stem with balanced rounded bowl
        val pPath = Path().apply {
            moveTo(54f * scale, 30f * scale)
            lineTo(54f * scale, 76f * scale)
            moveTo(54f * scale, 30f * scale)
            lineTo(66f * scale, 30f * scale)
            cubicTo(
                76f * scale, 30f * scale,
                76f * scale, 52f * scale,
                66f * scale, 52f * scale
            )
            lineTo(54f * scale, 52f * scale)
        }
        drawPath(pPath, resolvedPrimary, style = strokeLetters)

        // Pulse Waveform: Dynamic discovery heartbeat signal bridging J & P
        val pulsePath = Path().apply {
            moveTo(24f * scale, 52f * scale)
            lineTo(34f * scale, 52f * scale)
            lineTo(40f * scale, 61f * scale)
            lineTo(50f * scale, 37f * scale)
            lineTo(58f * scale, 65f * scale)
            lineTo(66f * scale, 52f * scale)
            lineTo(74f * scale, 52f * scale)
        }
        drawPath(pulsePath, resolvedPrimary, style = strokePulse)

        // Radiant Saffron Beacon Node: Signal terminus / opportunity discovery point
        drawCircle(
            color = beaconColor,
            radius = 3.5f * scale,
            center = Offset(81.5f * scale, 52f * scale)
        )
    }
}

/**
 * Typographic JobPulse Wordmark with balanced geometric weight.
 */
@Composable
fun JobPulseWordmark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 20.sp,
    primaryColor: Color? = null,
    showAccentDot: Boolean = true
) {
    val tokens = LocalAppThemeTokens.current
    val resolvedColor = primaryColor ?: tokens.textPrimary

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Job",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp
            ),
            color = resolvedColor
        )
        Text(
            text = "Pulse",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.2).sp
            ),
            color = resolvedColor
        )
        if (showAccentDot) {
            Box(
                modifier = Modifier
                    .padding(start = 2.dp, bottom = 8.dp)
                    .size(fontSize.value.dp * 0.22f)
                    .background(JobPulseBrandColors.WarmSaffron, RoundedCornerShape(50))
            )
        }
    }
}

/**
 * Primary JobPulse Logo combining Symbol + Wordmark.
 */
@Composable
fun JobPulseLogo(
    modifier: Modifier = Modifier,
    symbolSize: Dp = 26.dp,
    textSize: TextUnit = 20.sp,
    primaryColor: Color? = null,
    showWordmark: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JobPulseSymbol(
            size = symbolSize,
            primaryColor = primaryColor
        )
        if (showWordmark) {
            JobPulseWordmark(
                fontSize = textSize,
                primaryColor = primaryColor
            )
        }
    }
}

/**
 * Compact JobPulse Pill Badge for headers, statuses, and verification surfaces.
 */
@Composable
fun JobPulseBadge(
    modifier: Modifier = Modifier,
    text: String = "LIVE",
    isLive: Boolean = true
) {
    val tokens = LocalAppThemeTokens.current
    val badgeColor = if (isLive) tokens.livePulse else tokens.textTertiary

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = badgeColor.copy(alpha = if (tokens.isDark) 0.18f else 0.10f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(badgeColor, RoundedCornerShape(50))
            )
            Text(
                text = text,
                color = badgeColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
