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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavTab

@Composable
fun GlassyDock(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    isVisible: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it * 2 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it * 2 },
            animationSpec = tween(250, easing = FastOutLinearInEasing)
        ) + fadeOut(animationSpec = tween(200)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Glassy Container
            val dockShape = RoundedCornerShape(32.dp)
            val glassBackground = if (isDarkTheme) {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E222D).copy(alpha = 0.82f),
                        Color(0xFF14171F).copy(alpha = 0.90f)
                    )
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.88f),
                        Color(0xFFF0F3F9).copy(alpha = 0.85f)
                    )
                )
            }

            val glassBorderColor = if (isDarkTheme) {
                Color.White.copy(alpha = 0.18f)
            } else {
                Color.White.copy(alpha = 0.85f)
            }

            Surface(
                modifier = Modifier
                    .shadow(
                        elevation = 20.dp,
                        shape = dockShape,
                        spotColor = if (isDarkTheme) Color.Black.copy(alpha = 0.65f) else Color(0x33000000),
                        ambientColor = if (isDarkTheme) Color.Black.copy(alpha = 0.45f) else Color(0x1A000000)
                    )
                    .clip(dockShape)
                    .border(width = 1.2.dp, color = glassBorderColor, shape = dockShape)
                    .testTag("floating_glassy_dock"),
                color = Color.Transparent,
                shape = dockShape
            ) {
                Row(
                    modifier = Modifier
                        .background(glassBackground)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DockItem(
                        tab = NavTab.HOME,
                        isSelected = currentTab == NavTab.HOME,
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        onClick = { onTabSelected(NavTab.HOME) },
                        isDarkTheme = isDarkTheme
                    )

                    DockItem(
                        tab = NavTab.FEED,
                        isSelected = currentTab == NavTab.FEED,
                        selectedIcon = Icons.Filled.DynamicFeed,
                        unselectedIcon = Icons.Outlined.DynamicFeed,
                        hasBadge = true,
                        onClick = { onTabSelected(NavTab.FEED) },
                        isDarkTheme = isDarkTheme
                    )

                    DockItem(
                        tab = NavTab.SEARCH,
                        isSelected = currentTab == NavTab.SEARCH,
                        selectedIcon = Icons.Filled.ManageSearch,
                        unselectedIcon = Icons.Outlined.ManageSearch,
                        onClick = { onTabSelected(NavTab.SEARCH) },
                        isDarkTheme = isDarkTheme
                    )

                    DockItem(
                        tab = NavTab.ACCOUNT,
                        isSelected = currentTab == NavTab.ACCOUNT,
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        onClick = { onTabSelected(NavTab.ACCOUNT) },
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun DockItem(
    tab: NavTab,
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    hasBadge: Boolean = false,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = if (isDarkTheme) Color.White.copy(alpha = 0.60f) else Color.Black.copy(alpha = 0.55f)

    val activeBackground = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(if (isSelected) activeBackground else Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
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
                            .background(Color(0xFFFF3B30), CircleShape)
                    )
                }
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
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
