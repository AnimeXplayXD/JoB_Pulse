package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavTab
import com.example.ui.theme.AppThemeTokens
import com.example.ui.theme.LocalAppThemeTokens

@Composable
fun GlassyDock(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    isVisible: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val tokens = LocalAppThemeTokens.current

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
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val dockShape = RoundedCornerShape(26.dp)

            // Multi-layer liquid glass surface with specular highlight and ambient depth
            Surface(
                modifier = Modifier
                    .shadow(
                        elevation = if (tokens.isDark) 20.dp else 10.dp,
                        shape = dockShape,
                        spotColor = tokens.glassShadow,
                        ambientColor = tokens.glassShadow.copy(alpha = 0.35f)
                    )
                    .clip(dockShape)
                    .border(width = 1.dp, brush = tokens.glassBorder, shape = dockShape)
                    .testTag("floating_glassy_dock"),
                color = Color.Transparent,
                shape = dockShape
            ) {
                // Dock content container with single coordinated liquid-glass pill
                LiquidGlassDockRow(
                    currentTab = currentTab,
                    onTabSelected = onTabSelected,
                    tokens = tokens
                )
            }
        }
    }
}

@Composable
private fun LiquidGlassDockRow(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    tokens: AppThemeTokens
) {
    val density = LocalDensity.current
    val tabBoundsMap = remember { mutableStateMapOf<NavTab, Rect>() }

    val currentTargetRect = tabBoundsMap[currentTab]

    // Track movement direction to create fluid liquid elongation/contraction
    var previousTabIndex by remember { mutableIntStateOf(currentTab.ordinal) }
    val isMovingRight = currentTab.ordinal >= previousTabIndex
    SideEffect {
        previousTabIndex = currentTab.ordinal
    }

    // Asymmetric leading/trailing edge interpolation for fluid liquid glass flow:
    // Leading edge expands swiftly toward target; trailing edge lags and contracts smoothly.
    val leadingDuration = 260
    val trailingDuration = 330

    val leftSpec = remember(isMovingRight) {
        tween<Float>(
            durationMillis = if (isMovingRight) trailingDuration else leadingDuration,
            easing = FastOutSlowInEasing
        )
    }
    val rightSpec = remember(isMovingRight) {
        tween<Float>(
            durationMillis = if (isMovingRight) leadingDuration else trailingDuration,
            easing = FastOutSlowInEasing
        )
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
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "liquid_pill_top"
    )
    val animatedBottom by animateFloatAsState(
        targetValue = currentTargetRect?.bottom ?: 0f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "liquid_pill_bottom"
    )

    Box(
        modifier = Modifier
            .background(tokens.glassBackground)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
    ) {
        // Single unified flowing liquid-glass indicator pill
        if (currentTargetRect != null && animatedRight > animatedLeft) {
            val pillWidth = with(density) { (animatedRight - animatedLeft).toDp() }
            val pillHeight = with(density) { (animatedBottom - animatedTop).toDp() }
            val pillOffsetX = with(density) { animatedLeft.toDp() }
            val pillOffsetY = with(density) { animatedTop.toDp() }

            val pillShape = RoundedCornerShape(18.dp)

            // Multi-stop liquid-glass indicator brush
            val pillBrush = remember(tokens.isDark, tokens.primary) {
                if (tokens.isDark) {
                    Brush.verticalGradient(
                        colors = listOf(
                            tokens.primary.copy(alpha = 0.28f),
                            tokens.primary.copy(alpha = 0.14f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            tokens.primary.copy(alpha = 0.16f),
                            tokens.primary.copy(alpha = 0.08f)
                        )
                    )
                }
            }

            val pillSpecularBorder = remember(tokens.isDark, tokens.primary) {
                Brush.verticalGradient(
                    colors = if (tokens.isDark) {
                        listOf(tokens.primary.copy(alpha = 0.55f), tokens.borderSubtle)
                    } else {
                        listOf(Color.White.copy(alpha = 0.9f), tokens.primary.copy(alpha = 0.22f))
                    }
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = pillOffsetX, y = pillOffsetY)
                    .size(width = pillWidth, height = pillHeight)
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
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 9.dp),
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
                    modifier = Modifier.size(22.dp)
                )

                if (hasBadge && !isSelected) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = activeColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
