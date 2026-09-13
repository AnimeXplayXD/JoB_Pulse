package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.model.NavTab
import com.example.ui.theme.LocalAppThemeTokens

internal fun dockIndexForX(x: Float, width: Float, count: Int, current: Int, hysteresis: Float): Int {
    if (width <= 0f || count <= 0) return current
    val slot = width / count
    val candidate = (x / slot).toInt().coerceIn(0, count - 1)
    if (candidate == current) return current
    val boundary = if (candidate > current) (current + 1) * slot else current * slot
    return if (candidate > current && x < boundary + hysteresis || candidate < current && x > boundary - hysteresis) current else candidate
}

@Composable
fun GlassyDock(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    isVisible: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val tokens = LocalAppThemeTokens.current
    val tabs = NavTab.entries
    val latestSelect by rememberUpdatedState(onTabSelected)
    val latestTab by rememberUpdatedState(currentTab)
    var width by remember { mutableIntStateOf(0) }
    var dragging by remember { mutableStateOf(false) }
    var dragX by remember { mutableFloatStateOf(0f) }
    var preview by remember { mutableStateOf(currentTab) }
    val active = if (dragging) preview else currentTab
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val hysteresis = with(LocalDensity.current) { 8.dp.toPx() }
    val center = animateFloatAsState(
        targetValue = if (dragging && width > 0) (dragX / width).coerceIn(0.5f / tabs.size, 1f - 0.5f / tabs.size) else (active.ordinal + 0.5f) / tabs.size,
        animationSpec = if (dragging) snap() else spring(dampingRatio = 1f, stiffness = 600f),
        label = "dock_indicator"
    )
    AnimatedVisibility(
        visible = isVisible, modifier = modifier,
        enter = slideInVertically { it } + fadeIn(tween(160)),
        exit = slideOutVertically { it } + fadeOut(tween(120))
    ) {
        LiquidGlassBox(
            modifier = Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 8.dp)
                .fillMaxWidth().testTag("floating_glassy_dock"),
            shape = RoundedCornerShape(28.dp), elevation = 8.dp
        ) {
            Box(
                Modifier.padding(6.dp).fillMaxWidth().height(64.dp)
                    .onSizeChanged { width = it.width }
                    .pointerInput(width, rtl) {
                        fun logicalX(x: Float) = if (rtl) width - x else x
                        try {
                            detectHorizontalDragGestures(
                                onDragStart = { offset ->
                                    preview = latestTab
                                    dragX = logicalX(offset.x)
                                    dragging = true
                                },
                                onHorizontalDrag = { change, _ ->
                                    change.consume()
                                    dragX = logicalX(change.position.x).coerceIn(0f, width.toFloat())
                                    val index = dockIndexForX(dragX, width.toFloat(), tabs.size, preview.ordinal, hysteresis)
                                    if (tabs[index] != preview) {
                                        preview = tabs[index]
                                        latestSelect(preview)
                                    }
                                },
                                onDragEnd = { dragging = false },
                                onDragCancel = { dragging = false }
                            )
                        } finally { dragging = false }
                    }
            ) {
                Canvas(Modifier.matchParentSize()) {
                    val slot = size.width / tabs.size
                    val fraction = if (rtl) 1f - center.value else center.value
                    drawRoundRect(
                        color = tokens.primary.copy(alpha = if (tokens.isDark) 0.18f else 0.09f),
                        topLeft = Offset(size.width * fraction - slot / 2f + 2.dp.toPx(), 0f),
                        size = Size((slot - 4.dp.toPx()).coerceAtLeast(0f), size.height),
                        cornerRadius = CornerRadius(22.dp.toPx())
                    )
                }
                Row(Modifier.fillMaxSize().selectableGroup()) {
                    tabs.forEach { tab ->
                        val selected = active == tab
                        val icon = when (tab) {
                            NavTab.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
                            NavTab.FEED -> if (selected) Icons.Filled.DynamicFeed else Icons.Outlined.DynamicFeed
                            NavTab.SEARCH -> if (selected) Icons.Filled.Search else Icons.Outlined.Search
                            NavTab.ACCOUNT -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
                        }
                        Column(
                            Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(22.dp))
                                .testTag("dock_${tab.name}")
                                .selectable(selected, role = Role.Tab, onClick = { latestSelect(tab) }),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(icon, tab.title, Modifier.size(23.dp), tint = if (selected) tokens.primary else tokens.textSecondary)
                            AnimatedVisibility(selected, enter = fadeIn(tween(100)), exit = fadeOut(tween(80))) {
                                Text(tab.title, style = MaterialTheme.typography.labelSmall, color = tokens.primary, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
