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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavTab
import com.example.ui.theme.AppColors

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
        ) + fadeIn(animationSpec = tween(280)),
        exit = slideOutVertically(
            targetOffsetY = { it * 2 },
            animationSpec = tween(220, easing = FastOutLinearInEasing)
        ) + fadeOut(animationSpec = tween(180)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val dockShape = RoundedCornerShape(28.dp)

            // Multi-stop glass gradient with specular reflection
            val glassBackground = remember(isDarkTheme) {
                if (isDarkTheme) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xE61C2331),
                            Color(0xF0121721)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF2FFFFFF),
                            Color(0xE6F0F3F7)
                        )
                    )
                }
            }

            val glassBorder = remember(isDarkTheme) {
                Brush.verticalGradient(
                    colors = if (isDarkTheme) {
                        listOf(Color(0x40FFFFFF), Color(0x10FFFFFF))
                    } else {
                        listOf(Color(0xE6FFFFFF), Color(0x33000000))
                    }
                )
            }

            Surface(
                modifier = Modifier
                    .shadow(
                        elevation = if (isDarkTheme) 24.dp else 12.dp,
                        shape = dockShape,
                        spotColor = if (isDarkTheme) Color.Black.copy(alpha = 0.70f) else Color(0x2E000000),
                        ambientColor = if (isDarkTheme) Color.Black.copy(alpha = 0.50f) else Color(0x14000000)
                    )
                    .clip(dockShape)
                    .border(width = 1.dp, brush = glassBorder, shape = dockShape)
                    .testTag("floating_glassy_dock"),
                color = Color.Transparent,
                shape = dockShape
            ) {
                Row(
                    modifier = Modifier
                        .background(glassBackground)
                        .padding(horizontal = 10.dp, vertical = 7.dp),
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
                        selectedIcon = Icons.AutoMirrored.Filled.ManageSearch,
                        unselectedIcon = Icons.AutoMirrored.Outlined.ManageSearch,
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
    val inactiveColor = if (isDarkTheme) AppColors.DarkTextSecondary else AppColors.LightTextSecondary

    val activeBackground = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
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
                            .background(AppColors.LiveRed, CircleShape)
                    )
                }
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(150)) + expandHorizontally(tween(180)),
                exit = fadeOut(tween(120)) + shrinkHorizontally(tween(150))
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
