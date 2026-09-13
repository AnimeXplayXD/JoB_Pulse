package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import com.example.ui.theme.LocalAppThemeTokens

@Stable
class GlassBackdrop(val layer: GraphicsLayer) {
    var origin by mutableStateOf(Offset.Zero)
}

val LocalGlassBackdrop = compositionLocalOf<GlassBackdrop?> { null }

/** Record the scene behind glass, excluding glass itself to avoid feedback. */
fun Modifier.recordGlassBackdrop(backdrop: GlassBackdrop): Modifier = this
    .onGloballyPositioned { backdrop.origin = it.positionInRoot() }
    .drawWithContent {
        backdrop.layer.record { this@drawWithContent.drawContent() }
        drawLayer(backdrop.layer)
    }

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
    val backdrop = LocalGlassBackdrop.current
    val blurLayer = rememberGraphicsLayer()
    val radius = with(LocalDensity.current) { 18.dp.toPx() }
    val supported = Build.VERSION.SDK_INT >= 31 && LocalView.current.isHardwareAccelerated && backdrop != null
    var origin by remember { mutableStateOf(Offset.Zero) }
    val effect = remember(supported, radius) {
        if (supported) BlurEffect(radiusX = radius, radiusY = radius, edgeTreatment = TileMode.Clamp) else null
    }
    SideEffect { blurLayer.renderEffect = effect }
    val tintColor = tint ?: tokens.surface.copy(alpha = if (supported) 0.72f else 0.96f)
    val border = if (borderBrush == null) Modifier.border(0.5.dp, tokens.borderSubtle, shape)
        else Modifier.border(0.5.dp, borderBrush, shape)
    Box(
        modifier.shadow(elevation, shape,
            spotColor = (shadowColor ?: tokens.glassShadow).copy(alpha = 0.12f),
            ambientColor = (shadowColor ?: tokens.glassShadow).copy(alpha = 0.06f))
            .clip(shape)
            .onGloballyPositioned { origin = it.positionInRoot() }
            .drawWithContent {
                if (supported && backdrop != null) {
                    val relative = origin - backdrop.origin
                    // Sample beyond the visible capsule before applying the blur kernel.
                    // Cropping first creates repeated/transparent strips along glass edges.
                    val padding = ceil(radius * 3f).toInt()
                    blurLayer.record(size = IntSize(ceil(size.width).toInt() + padding * 2, ceil(size.height).toInt() + padding * 2)) {
                        translate(padding - relative.x, padding - relative.y) { drawLayer(backdrop.layer) }
                    }
                    translate(-padding.toFloat(), -padding.toFloat()) { drawLayer(blurLayer) }
                }
                drawRect(tintColor)
                drawContent()
            }
            .then(border),
        content = content
    )
}
