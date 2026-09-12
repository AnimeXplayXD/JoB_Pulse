package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Canvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavTab
import com.example.ui.theme.AppThemeTokens
import com.example.ui.theme.DarkThemeTokens
import com.example.ui.theme.LightThemeTokens
import com.example.ui.theme.LocalAppThemeTokens
import com.example.ui.theme.lerpTokens

@Composable
fun GlassyDock(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    isVisible: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val baseTokens = LocalAppThemeTokens.current
    val wave = LocalThemeWave.current

    // Dock relies entirely on the ThemeRevealProvider for its theme transition.
    val tokens = baseTokens

    var isPillHeld by remember { mutableStateOf(false) }

    // Dock subtly breathes when active pill is held or dragged
    val dockScale by animateFloatAsState(
        targetValue = if (isPillHeld) 1.012f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dock_scale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it * 2 },
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(240)),
        exit = slideOutVertically(
            targetOffsetY = { it * 2 },
            animationSpec = tween(220, easing = FastOutLinearInEasing)
        ) + fadeOut(animationSpec = tween(180)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 6.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val dockShape = RoundedCornerShape(tokens.dockRadius)

            LiquidGlassBox(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = dockScale
                        scaleY = dockScale
                    }
                    .testTag("floating_glassy_dock"),
                shape = dockShape,
                elevation = if (isPillHeld) 16.dp else if (tokens.isDark) 12.dp else 8.dp,
                shadowColor = tokens.glassShadow
            ) {
                LiquidGlassDockRow(
                    currentTab = currentTab,
                    onTabSelected = onTabSelected,
                    tokens = tokens,
                    isHeld = isPillHeld,
                    onHeldChanged = { isPillHeld = it }
                )

                // Subtle liquid wave sheen passing across the dock when the wave reaches it
                if (wave.isWaveActive && wave.progress in 0.65f..0.98f) {
                    val waveFraction = ((wave.progress - 0.65f) / 0.33f).coerceIn(0f, 1f)
                    val sheenAlpha = (kotlin.math.sin(waveFraction * Math.PI.toFloat()) * 0.28f).coerceIn(0f, 1f)
                    val sheenColor = if (wave.toDark) Color(0xFF58A6FF) else Color(0xFFFFC043)

                    Canvas(modifier = Modifier.matchParentSize().clip(dockShape)) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    sheenColor.copy(alpha = sheenAlpha),
                                    Color.Transparent
                                ),
                                startX = size.width * (1f - waveFraction * 1.5f),
                                endX = size.width * (1.5f - waveFraction * 1.5f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LiquidGlassDockRow(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    tokens: AppThemeTokens,
    isHeld: Boolean,
    onHeldChanged: (Boolean) -> Unit
) {
    val density = LocalDensity.current
    val tabBoundsMap = remember { mutableStateMapOf<NavTab, Rect>() }

    // Track horizontal drag position
    var dragPositionX by remember { mutableFloatStateOf(0f) }

    // Track movement direction to create fluid liquid elongation/contraction
    var previousTabIndex by remember { mutableIntStateOf(currentTab.ordinal) }
    val isMovingRight = currentTab.ordinal >= previousTabIndex
    SideEffect {
        previousTabIndex = currentTab.ordinal
    }

    // Single continuous interpolated target bounds for zero jump between tabs
    val currentTargetRect = remember(isHeld, dragPositionX, currentTab, tabBoundsMap.toMap()) {
        if (isHeld && tabBoundsMap.size >= 4) {
            computePillRectForX(dragPositionX, tabBoundsMap, currentTab)
        } else {
            tabBoundsMap[currentTab]
        }
    }

    // When held/dragged: spring follows finger with instant physical responsiveness;
    // When released / tapped: fluid asymmetric liquid glass easing.
    val leadingDuration = 240
    val trailingDuration = 300

    val leftSpec: AnimationSpec<Float> = remember(isHeld, isMovingRight) {
        if (isHeld) {
            spring<Float>(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioNoBouncy)
        } else {
            tween<Float>(
                durationMillis = if (isMovingRight) trailingDuration else leadingDuration,
                easing = FastOutSlowInEasing
            )
        }
    }
    val rightSpec: AnimationSpec<Float> = remember(isHeld, isMovingRight) {
        if (isHeld) {
            spring<Float>(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioNoBouncy)
        } else {
            tween<Float>(
                durationMillis = if (isMovingRight) leadingDuration else trailingDuration,
                easing = FastOutSlowInEasing
            )
        }
    }

    val animatedLeft by animateFloatAsState(
        targetValue = currentTargetRect?.left ?: 0f,
        animationSpec = leftSpec,
        label = "liquid_pill_left"
    )
    val animatedRight by animateFloatAsState(
        targetValue = currentTargetRect?.right ?: 0f,
        animationSpec = rightSpec,
        label = "liquid_pill_right"
    )
    val animatedTop by animateFloatAsState(
        targetValue = currentTargetRect?.top ?: 0f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "liquid_pill_top"
    )
    val animatedBottom by animateFloatAsState(
        targetValue = currentTargetRect?.bottom ?: 0f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "liquid_pill_bottom"
    )

    // Subtle pill enlargement while being held or dragged
    val pillScale by animateFloatAsState(
        targetValue = if (isHeld) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pill_scale"
    )

    Box(
        modifier = Modifier
            .background(tokens.glassBackground)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .fillMaxWidth()
            .pointerInput(tabBoundsMap, currentTab) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val initialX = down.position.x
                    val currentPillRect = tabBoundsMap[currentTab]

                    // Check if touch down is on or near the active pill
                    val isTouchNearPill = currentPillRect != null &&
                        initialX in (currentPillRect.left - 24f)..(currentPillRect.right + 24f)

                    if (isTouchNearPill) {
                        onHeldChanged(true)
                        dragPositionX = initialX
                    }

                    var isDragStarted = false
                    val pointerId = down.id
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.find { it.id == pointerId } ?: break
                        if (!change.pressed) {
                            // Touch released
                            if (isDragStarted) {
                                change.consume()
                                val nearestTab = findNearestTab(dragPositionX, tabBoundsMap, currentTab)
                                onTabSelected(nearestTab)
                            }
                            break
                        }

                        val currentX = change.position.x
                        val distance = kotlin.math.abs(currentX - initialX)

                        if (!isDragStarted && distance > viewConfiguration.touchSlop) {
                            isDragStarted = true
                            onHeldChanged(true)
                        }

                        if (isDragStarted) {
                            change.consume()
                            dragPositionX = currentX
                        }
                    }
                    onHeldChanged(false)
                }
            }
    ) {
        // Single unified flowing liquid-glass indicator pill
        if (currentTargetRect != null && animatedRight > animatedLeft) {
            val pillWidth = with(density) { (animatedRight - animatedLeft).toDp() }
            val pillHeight = with(density) { (animatedBottom - animatedTop).toDp() }
            val pillOffsetX = with(density) { animatedLeft.toDp() }
            val pillOffsetY = with(density) { animatedTop.toDp() }

            val pillShape = RoundedCornerShape(16.dp)

            // Multi-stop liquid-glass indicator brush
            val pillBrush = remember(tokens.isDark, tokens.primary, isHeld) {
                if (tokens.isDark) {
                    Brush.verticalGradient(
                        colors = listOf(
                            tokens.primary.copy(alpha = if (isHeld) 0.35f else 0.28f),
                            tokens.primary.copy(alpha = if (isHeld) 0.18f else 0.14f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            tokens.primary.copy(alpha = if (isHeld) 0.22f else 0.16f),
                            tokens.primary.copy(alpha = if (isHeld) 0.11f else 0.08f)
                        )
                    )
                }
            }

            val pillSpecularBorder = remember(tokens.isDark, tokens.primary, isHeld) {
                Brush.verticalGradient(
                    colors = if (tokens.isDark) {
                        listOf(
                            tokens.primary.copy(alpha = if (isHeld) 0.70f else 0.55f),
                            tokens.borderSubtle
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.95f),
                            tokens.primary.copy(alpha = if (isHeld) 0.32f else 0.22f)
                        )
                    }
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = pillOffsetX, y = pillOffsetY)
                    .size(width = pillWidth, height = pillHeight)
                    .graphicsLayer {
                        scaleX = pillScale
                        scaleY = pillScale
                    }
                    .clip(pillShape)
                    .background(pillBrush)
                    .border(width = 1.dp, brush = pillSpecularBorder, shape = pillShape)
            )
        }

        // Destinations Row sitting above the flowing indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockDestinationItem(
                tab = NavTab.HOME,
                isSelected = currentTab == NavTab.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                onClick = { onTabSelected(NavTab.HOME) },
                onPositioned = { rect -> tabBoundsMap[NavTab.HOME] = rect },
                tokens = tokens
            )

            DockDestinationItem(
                tab = NavTab.FEED,
                isSelected = currentTab == NavTab.FEED,
                selectedIcon = Icons.Filled.DynamicFeed,
                unselectedIcon = Icons.Outlined.DynamicFeed,
                hasBadge = true,
                onClick = { onTabSelected(NavTab.FEED) },
                onPositioned = { rect -> tabBoundsMap[NavTab.FEED] = rect },
                tokens = tokens
            )

            DockDestinationItem(
                tab = NavTab.SEARCH,
                isSelected = currentTab == NavTab.SEARCH,
                selectedIcon = Icons.AutoMirrored.Filled.ManageSearch,
                unselectedIcon = Icons.AutoMirrored.Outlined.ManageSearch,
                onClick = { onTabSelected(NavTab.SEARCH) },
                onPositioned = { rect -> tabBoundsMap[NavTab.SEARCH] = rect },
                tokens = tokens
            )

            DockDestinationItem(
                tab = NavTab.ACCOUNT,
                isSelected = currentTab == NavTab.ACCOUNT,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                onClick = { onTabSelected(NavTab.ACCOUNT) },
                onPositioned = { rect -> tabBoundsMap[NavTab.ACCOUNT] = rect },
                tokens = tokens
            )
        }
    }
}

@Composable
private fun DockDestinationItem(
    tab: NavTab,
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    hasBadge: Boolean = false,
    onClick: () -> Unit,
    onPositioned: (Rect) -> Unit,
    tokens: AppThemeTokens
) {
    val activeColor = tokens.primary
    val inactiveColor = tokens.textSecondary
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                onPositioned(coordinates.boundsInParent())
            }
            .clip(RoundedCornerShape(16.dp))
            .semantics {
                role = Role.Tab
                selected = isSelected
            }
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = if (isSelected) selectedIcon else unselectedIcon,
                    contentDescription = tab.title,
                    tint = if (isSelected) activeColor else inactiveColor,
                    modifier = Modifier.size(20.dp)
                )

                if (hasBadge && !isSelected) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.TopEnd)
                            .background(tokens.livePulse, CircleShape)
                    )
                }
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(160)) + expandHorizontally(tween(200)),
                exit = fadeOut(tween(120)) + shrinkHorizontally(tween(160))
            ) {
                Row {
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = activeColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Computes a smooth, continuous interpolated [Rect] for any pointer [x] coordinate along the dock,
 * eliminating jumps between tabs.
 */
private fun computePillRectForX(
    x: Float,
    tabBoundsMap: Map<NavTab, Rect>,
    currentTab: NavTab
): Rect {
    val tabs = listOf(NavTab.HOME, NavTab.FEED, NavTab.SEARCH, NavTab.ACCOUNT)
    val rects = tabs.map { tabBoundsMap[it] }
    if (rects.any { it == null }) {
        return tabBoundsMap[currentTab] ?: Rect.Zero
    }
    val nonNullRects = rects.filterNotNull()
    val centers = nonNullRects.map { (it.left + it.right) / 2f }

    if (x <= centers.first()) {
        return nonNullRects.first()
    }
    if (x >= centers.last()) {
        return nonNullRects.last()
    }

    for (i in 0 until centers.size - 1) {
        val c1 = centers[i]
        val c2 = centers[i + 1]
        if (x in c1..c2) {
            val fraction = ((x - c1) / (c2 - c1)).coerceIn(0f, 1f)
            val r1 = nonNullRects[i]
            val r2 = nonNullRects[i + 1]
            return Rect(
                left = r1.left + (r2.left - r1.left) * fraction,
                top = r1.top + (r2.top - r1.top) * fraction,
                right = r1.right + (r2.right - r1.right) * fraction,
                bottom = r1.bottom + (r2.bottom - r1.bottom) * fraction
            )
        }
    }
    return tabBoundsMap[currentTab] ?: Rect.Zero
}

/**
 * Finds the nearest tab destination based on the pointer release coordinate [x].
 */
private fun findNearestTab(
    x: Float,
    tabBoundsMap: Map<NavTab, Rect>,
    currentTab: NavTab
): NavTab {
    val tabs = listOf(NavTab.HOME, NavTab.FEED, NavTab.SEARCH, NavTab.ACCOUNT)
    return tabs.minByOrNull { tab ->
        val rect = tabBoundsMap[tab]
        if (rect != null) {
            val centerX = (rect.left + rect.right) / 2f
            kotlin.math.abs(centerX - x)
        } else {
            Float.MAX_VALUE
        }
    } ?: currentTab
}
