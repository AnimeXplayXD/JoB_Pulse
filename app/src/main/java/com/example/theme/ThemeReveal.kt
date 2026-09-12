package com.example.theme

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Custom Shape creating a circular reveal outline based on a progress float and an origin offset.
 *
 * @param progress Animation progress from 0f to 1f.
 * @param origin Center point of the circular reveal. Defaults to screen center if null or zero.
 */
class CircularRevealShape(
    private val progress: Float,
    private val origin: Offset? = null
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        if (progress <= 0f) {
            return Outline.Generic(Path())
        }
        if (progress >= 1f) {
            return Outline.Rectangle(Rect(Offset.Zero, size))
        }

        val center = if (origin == null || origin == Offset.Zero) {
            Offset(size.width / 2f, size.height / 2f)
        } else {
            origin
        }

        val maxRadius = hypot(
            max(center.x, size.width - center.x).toDouble(),
            max(center.y, size.height - center.y).toDouble()
        ).toFloat() * 1.05f

        val radius = maxRadius * progress

        val circlePath = Path().apply {
            addOval(Rect(center = center, radius = radius))
        }

        return Outline.Generic(circlePath)
    }
}

/**
 * Controller allowing any component deep in the Compose hierarchy to trigger
 * a circular reveal theme transition originating from its spatial coordinates.
 */
interface ThemeRevealController {
    fun reveal(center: Offset? = null)
    fun toggleTheme(center: Offset? = null) = reveal(center)
}

val LocalThemeRevealController = compositionLocalOf<ThemeRevealController> {
    object : ThemeRevealController {
        override fun reveal(center: Offset?) {}
    }
}

/**
 * Spatial Theme Wave state exposing the wavefront propagation across the display.
 */
data class ThemeWaveState(
    val isWaveActive: Boolean = false,
    val progress: Float = 1f,
    val origin: Offset = Offset.Zero,
    val fromDark: Boolean = true,
    val toDark: Boolean = false
)

val LocalThemeWave = staticCompositionLocalOf { ThemeWaveState() }

/**
 * High-performance Snapshot Theme Reveal Provider.
 *
 * Synchronously captures an instantaneous snapshot of the current view hierarchy before
 * the theme switches. The frozen snapshot is displayed in the background, while the new
 * theme is rendered in the foreground with an expanding circular clip originating from
 * the user's touch point, accompanied by a glass wavefront crest.
 */
@Composable
fun ThemeRevealProvider(
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier,
    animationDurationMillis: Int = 560,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    var snapshotBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isRevealing by remember { mutableStateOf(false) }
    var revealOrigin by remember { mutableStateOf(Offset.Zero) }
    var animFromDark by remember { mutableStateOf(isDarkTheme) }
    var animToDark by remember { mutableStateOf(!isDarkTheme) }
    val animProgress = remember { Animatable(0f) }

    val controller = remember(onToggleTheme, isDarkTheme, view) {
        object : ThemeRevealController {
            override fun reveal(center: Offset?) {
                if (isRevealing) return
                revealOrigin = center ?: Offset.Zero
                animFromDark = isDarkTheme
                animToDark = !isDarkTheme

                // Synchronously snapshot the current view hierarchy before theme state changes
                try {
                    if (view.width > 0 && view.height > 0) {
                        val bitmap = Bitmap.createBitmap(
                            view.width,
                            view.height,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = android.graphics.Canvas(bitmap)
                        view.draw(canvas)
                        snapshotBitmap = bitmap.asImageBitmap()
                    }
                } catch (t: Throwable) {
                    snapshotBitmap = null
                }

                isRevealing = true

                // Toggle theme state; child content recomposes with the new theme
                onToggleTheme()

                coroutineScope.launch {
                    animProgress.snapTo(0f)
                    animProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = animationDurationMillis,
                            easing = FastOutSlowInEasing
                        )
                    )
                    isRevealing = false
                    snapshotBitmap = null
                }
            }
        }
    }

    val waveState = remember(isRevealing, animProgress.value, revealOrigin, animFromDark, animToDark) {
        ThemeWaveState(
            isWaveActive = isRevealing,
            progress = if (isRevealing) animProgress.value else 1f,
            origin = revealOrigin,
            fromDark = animFromDark,
            toDark = animToDark
        )
    }

    CompositionLocalProvider(
        LocalThemeRevealController provides controller,
        LocalThemeWave provides waveState,
        com.example.ui.components.LocalCircularReveal provides controller,
        com.example.ui.components.LocalThemeWave provides waveState
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            // Background Layer: The frozen snapshot of the old theme.
            val snapshot = snapshotBitmap
            if (isRevealing && snapshot != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawImage(
                        image = snapshot,
                        dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                    )
                }
            }

            // Foreground Layer: The new theme content.
            // When revealing, it clips itself with an expanding circle to reveal the new theme over the old snapshot.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (isRevealing) {
                            clip = true
                            shape = CircularRevealShape(
                                progress = animProgress.value,
                                origin = revealOrigin
                            )
                        }
                    }
            ) {
                content()
            }

            // Luminous wavefront crest along the circular reveal perimeter
            if (isRevealing) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = if (revealOrigin == Offset.Zero) {
                        Offset(size.width - 48.dp.toPx(), 48.dp.toPx())
                    } else {
                        revealOrigin
                    }

                    val maxRadius = hypot(
                        max(center.x, size.width - center.x).toDouble(),
                        max(center.y, size.height - center.y).toDouble()
                    ).toFloat() * 1.05f

                    val currentRadius = maxRadius * animProgress.value
                    if (currentRadius > 0f && currentRadius < maxRadius) {
                        // Ambient glow ring
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.35f * (1f - animProgress.value)),
                                    Color(0xFF38BDF8).copy(alpha = 0.20f * (1f - animProgress.value)),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = currentRadius + 16.dp.toPx()
                            ),
                            radius = currentRadius + 8.dp.toPx(),
                            center = center
                        )

                        // Crisp glass crest outline
                        drawCircle(
                            color = Color.White.copy(alpha = 0.65f * (1f - animProgress.value)),
                            radius = currentRadius,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}
