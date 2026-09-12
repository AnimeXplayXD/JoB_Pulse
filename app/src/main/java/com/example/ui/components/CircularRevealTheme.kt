package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Controller interface to trigger a spatial circular reveal transition for theme toggling.
 */
interface CircularRevealController {
    fun toggleTheme(center: Offset? = null)
}

val LocalCircularReveal = staticCompositionLocalOf<CircularRevealController?> { null }

/**
 * Circular reveal theme container that captures a snapshot of the current UI before the theme
 * changes, and animates an expanding circular reveal from the origin coordinate (e.g. theme toggle icon)
 * to smoothly reveal the new theme.
 */
@Composable
fun CircularRevealTheme(
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    var previousBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var revealOrigin by remember { mutableStateOf(Offset.Zero) }
    val animProgress = remember { Animatable(1f) }

    val controller = remember(onToggleTheme) {
        object : CircularRevealController {
            override fun toggleTheme(center: Offset?) {
                // Prevent duplicate trigger while animation is active
                if (animProgress.value < 1f && previousBitmap != null) return

                try {
                    if (view.width > 0 && view.height > 0) {
                        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(bitmap)
                        view.draw(canvas)
                        previousBitmap = bitmap.asImageBitmap()
                        revealOrigin = center ?: Offset(view.width.toFloat() - 48f, 48f)
                    }
                } catch (_: Throwable) {
                    previousBitmap = null
                }

                // Trigger actual theme state change
                onToggleTheme()

                // Animate circular reveal
                coroutineScope.launch {
                    animProgress.snapTo(0f)
                    animProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
                    )
                    previousBitmap = null
                }
            }
        }
    }

    CompositionLocalProvider(LocalCircularReveal provides controller) {
        Box(modifier = modifier.fillMaxSize()) {
            content()

            val snapshot = previousBitmap
            if (snapshot != null && animProgress.value < 1f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxRadius = hypot(
                        max(revealOrigin.x, size.width - revealOrigin.x).toDouble(),
                        max(revealOrigin.y, size.height - revealOrigin.y).toDouble()
                    ).toFloat()
                    val currentRadius = maxRadius * animProgress.value

                    val clipPath = Path().apply {
                        addOval(Rect(center = revealOrigin, radius = currentRadius))
                    }

                    // Draw previous theme outside the expanding circle (Difference clip)
                    clipPath(clipPath, clipOp = ClipOp.Difference) {
                        drawImage(
                            image = snapshot,
                            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                        )
                    }

                    // Subtle specular ripple edge at the expanding circular boundary
                    if (currentRadius > 0f && currentRadius < maxRadius) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.35f * (1f - animProgress.value)),
                                    Color.Transparent
                                ),
                                center = revealOrigin,
                                radius = currentRadius + 8.dp.toPx()
                            ),
                            radius = currentRadius,
                            center = revealOrigin,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}
