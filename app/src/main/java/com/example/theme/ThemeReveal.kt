package com.example.theme

import android.animation.ValueAnimator
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt

class CircularRevealShape(private val progress: Float, private val origin: Offset? = null) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        if (progress <= 0f) return Outline.Generic(Path())
        if (progress >= 1f) return Outline.Rectangle(Rect(Offset.Zero, size))
        val center = origin ?: Offset(size.width / 2f, size.height / 2f)
        val radius = hypot(max(center.x, size.width - center.x).toDouble(), max(center.y, size.height - center.y).toDouble()).toFloat() * 1.05f * progress
        return Outline.Generic(Path().apply { addOval(Rect(center, radius)) })
    }
}

interface ThemeRevealController {
    fun reveal(center: Offset? = null)
    fun toggleTheme(center: Offset? = null) = reveal(center)
}
val LocalThemeRevealController = compositionLocalOf<ThemeRevealController> {
    object : ThemeRevealController { override fun reveal(center: Offset?) {} }
}
data class ThemeWaveState(
    val isWaveActive: Boolean = false, val progress: Float = 1f,
    val origin: Offset = Offset.Zero, val fromDark: Boolean = true, val toDark: Boolean = false
)
val LocalThemeWave = compositionLocalOf { ThemeWaveState() }

@Composable
fun ThemeRevealProvider(
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier,
    animationDurationMillis: Int = 420,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val layer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    val toggle by rememberUpdatedState(onToggleTheme)
    val dark by rememberUpdatedState(isDarkTheme)
    val progress = remember { Animatable(1f) }
    var snapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    var busy by remember { mutableStateOf(false) }
    var origin by remember { mutableStateOf<Offset?>(null) }
    var fromDark by remember { mutableStateOf(isDarkTheme) }
    var toDark by remember { mutableStateOf(!isDarkTheme) }
    val controller = remember(layer, scope, context) {
        object : ThemeRevealController {
            override fun reveal(center: Offset?) {
                if (busy) return
                val animationsEnabled = if (Build.VERSION.SDK_INT >= 26) ValueAnimator.areAnimatorsEnabled()
                    else Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f
                if (!animationsEnabled) { toggle(); return }
                busy = true
                origin = center
                fromDark = dark
                toDark = !dark
                scope.launch {
                    try {
                        // One GPU-layer snapshot per theme change, never view.draw() or per-frame capture.
                        val image = try { layer.toImageBitmap() }
                        catch (cancelled: CancellationException) { throw cancelled }
                        catch (_: Exception) { null }
                        if (image == null) { toggle(); return@launch }
                        progress.snapTo(0f)
                        snapshot = image
                        toggle()
                        progress.animateTo(1f, tween(animationDurationMillis, easing = FastOutSlowInEasing))
                    } finally {
                        snapshot = null
                        busy = false
                    }
                }
            }
        }
    }
    // Do not distribute per-frame progress through the entire composition tree.
    val wave = ThemeWaveState(snapshot != null, if (snapshot != null) 0f else 1f, origin ?: Offset.Zero, fromDark, toDark)
    CompositionLocalProvider(
        LocalThemeRevealController provides controller, LocalThemeWave provides wave,
        com.example.ui.components.LocalCircularReveal provides controller,
        com.example.ui.components.LocalThemeWave provides wave
    ) {
        Box(modifier.fillMaxSize()) {
            snapshot?.let { image ->
                Canvas(Modifier.fillMaxSize()) {
                    drawImage(image, dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()))
                }
            }
            Box(
                Modifier.fillMaxSize()
                    .graphicsLayer {
                        clip = snapshot != null
                        shape = CircularRevealShape(progress.value, origin)
                    }
                    .drawWithContent {
                        layer.record { this@drawWithContent.drawContent() }
                        drawLayer(layer)
                    }
            ) { content() }
        }
    }
}
