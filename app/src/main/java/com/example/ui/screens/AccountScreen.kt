package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.theme.AppColors

@Composable
fun AccountScreen(
    listState: LazyListState,
    bookmarkedCount: Int,
    onViewBookmarks: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    var profile by remember { mutableStateOf(UserProfile()) }
    var selectedCategoryQuota by remember { mutableStateOf(profile.categoryQuota) }
    var pushEnabled by remember { mutableStateOf(profile.pushAlertsEnabled) }
    var admitCardAlerts by remember { mutableStateOf(profile.admitCardAlertsEnabled) }
    var examDateAlerts by remember { mutableStateOf(profile.examDateAlertsEnabled) }
    val isDark = isDarkTheme

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("account_screen")
    ) {
        // Aspirant Profile Hero Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = if (isDark) AppColors.DarkSurface else AppColors.LightSurface,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                ),
                shadowElevation = if (isDark) 4.dp else 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Initials with Specular Halo
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        Color(0xFF0072BC)
                                    )
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AS",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    )

                    Text(
                        text = profile.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.18f else 0.10f)
                    ) {
                        Text(
                            text = "Target: ${profile.targetExam}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Saved Bookmarks Quick Row
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onViewBookmarks() },
                shape = RoundedCornerShape(18.dp),
                color = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Bookmarked Government Jobs",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                            )
                            Text(
                                text = "$bookmarkedCount positions saved for quick application",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View",
                        tint = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                    )
                }
            }
        }

        // Eligibility & Quota Profile
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) AppColors.DarkSurface else AppColors.LightSurface,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Candidate Eligibility & Quota Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileItem(
                        icon = Icons.Default.School,
                        label = "Highest Qualification",
                        value = profile.qualification,
                        isDark = isDark
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                    )

                    ProfileItem(
                        icon = Icons.Default.Category,
                        label = "Reservation / Quota Category",
                        value = selectedCategoryQuota,
                        isDark = isDark
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                    )

                    ProfileItem(
                        icon = Icons.Default.PinDrop,
                        label = "Home State / Domicile",
                        value = profile.stateResidence,
                        isDark = isDark
                    )
                }
            }
        }

        // Live Alerts & Notifications Preferences
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) AppColors.DarkSurface else AppColors.LightSurface,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Live Alerts & Notification Triggers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    AccountSwitchRow(
                        title = "Push Notifications for New Jobs",
                        subtitle = "Instant alert whenever a relevant quota seat is announced",
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it },
                        isDark = isDark
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                    )

                    AccountSwitchRow(
                        title = "Admit Card Download Alerts",
                        subtitle = "Notify as soon as the hall ticket link goes live",
                        checked = admitCardAlerts,
                        onCheckedChange = { admitCardAlerts = it },
                        isDark = isDark
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                    )

                    AccountSwitchRow(
                        title = "Exam Schedule & Date Alerts",
                        subtitle = "Live notice of exam dates and shift timings",
                        checked = examDateAlerts,
                        onCheckedChange = { examDateAlerts = it },
                        isDark = isDark
                    )
                }
            }
        }

        // Appearance & Information
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) AppColors.DarkSurface else AppColors.LightSurface,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Appearance & System Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Dark Mode",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                                )
                                Text(
                                    text = if (isDark) "Obsidian dark mode active" else "Warm alabaster light mode active",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isDark,
                            onCheckedChange = { onToggleTheme() }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "JoB_Pulse Gazette Engine",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                            )
                            Text(
                                text = "v2.0 • Real-time All-India Quota & Recruitment Tracker",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.SuccessGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "VERIFIED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.SuccessGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileItem(icon: ImageVector, label: String, value: String, isDark: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
            )
        }
    }
}

@Composable
private fun AccountSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
