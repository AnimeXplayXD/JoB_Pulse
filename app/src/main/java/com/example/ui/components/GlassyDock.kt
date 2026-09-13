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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
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
    val density = LocalDensity.current
    val hysteresis = with(density) { 8.dp.toPx() }

    // Measure every label once, not just the selected one. Changing tabs must not
    // resize the capsule or change drag coordinates underneath the user's finger.
    val labelStyle = MaterialTheme.typography.labelSmall
    val textMeasurer = rememberTextMeasurer()
    val labelSizes = remember(textMeasurer, labelStyle, tabs, density) {
        tabs.map { textMeasurer.measure(it.title, style = labelStyle, maxLines = 1, softWrap = false).size }
    }
    val labelWidth = with(density) { labelSizes.maxOf { it.width }.toDp() }
    val labelHeight = with(density) { labelSizes.maxOf { it.height }.toDp() }
    val preferredSlotWidth = maxOf(72.dp, labelWidth + 16.dp)
    val preferredWidth = preferredSlotWidth * tabs.size + 12.dp
    val slotHeight = maxOf(56.dp, 24.dp + labelHeight + 12.dp)
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
        BoxWithConstraints(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Reduce exterior margins before shrinking the four 48dp touch targets.
            val sideMargin = minOf(16.dp, ((maxWidth - 204.dp) / 2).coerceAtLeast(0.dp))
            LiquidGlassBox(
                modifier = Modifier.width(minOf(preferredWidth, maxWidth - sideMargin * 2)).testTag("floating_glassy_dock"),
                shape = RoundedCornerShape(26.dp), elevation = 8.dp
            ) {
                Box(
                    Modifier.padding(6.dp).fillMaxWidth().height(slotHeight)
                        .onSizeChanged { width = it.width }
                        .pointerInput(width, rtl, hysteresis) {
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
                            cornerRadius = CornerRadius(20.dp.toPx())
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
                                Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(20.dp))
                                    .testTag("dock_${tab.name}")
                                    .semantics { contentDescription = tab.title }
                                    .selectable(selected, role = Role.Tab, onClick = { latestSelect(tab) }),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(icon, null, Modifier.size(24.dp), tint = if (selected) tokens.primary else tokens.textSecondary)
                                Spacer(Modifier.height(2.dp))
                                // Reserved label space keeps icons still during selection changes.
                                Box(Modifier.fillMaxWidth().height(labelHeight), contentAlignment = Alignment.Center) {
                                    androidx.compose.animation.AnimatedVisibility(selected, enter = fadeIn(tween(100)), exit = fadeOut(tween(80))) {
                                        Text(
                                            tab.title, Modifier.padding(horizontal = 4.dp).clearAndSetSemantics {},
                                            style = labelStyle, color = tokens.primary, maxLines = 1,
                                            softWrap = false, overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
