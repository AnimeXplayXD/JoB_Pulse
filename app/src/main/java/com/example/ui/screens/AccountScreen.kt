package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.ui.theme.LocalAppThemeTokens
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
    val tokens = LocalAppThemeTokens.current

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
                color = tokens.surface,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    tokens.borderSubtle
                ),
                shadowElevation = if (tokens.isDark) 4.dp else 2.dp
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
                                        tokens.primary,
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
                        color = tokens.textPrimary
                    )

                    Text(
                        text = profile.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textTertiary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.primary.copy(alpha = if (tokens.isDark) 0.18f else 0.10f)
                    ) {
                        Text(
                            text = "Target: ${profile.targetExam}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.primary,
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
                color = tokens.surfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    tokens.borderSubtle
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
                                .background(tokens.primary, CircleShape),
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
                                color = tokens.textPrimary
                            )
                            Text(
                                text = "$bookmarkedCount positions saved for quick application",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View",
                        tint = tokens.textSecondary
                    )
                }
            }
        }

        // Eligibility & Quota Profile
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = tokens.surface,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    tokens.borderSubtle
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Candidate Eligibility & Quota Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileItem(
                        icon = Icons.Default.School,
                        label = "Highest Qualification",
                        value = profile.qualification,
                        tokens = tokens
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = tokens.divider
                    )

                    ProfileItem(
                        icon = Icons.Default.Category,
                        label = "Reservation / Quota Category",
                        value = selectedCategoryQuota,
                        tokens = tokens
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = tokens.divider
                    )

                    ProfileItem(
                        icon = Icons.Default.PinDrop,
                        label = "Home State / Domicile",
                        value = profile.stateResidence,
                        tokens = tokens
                    )
                }
            }
        }

        // Live Alerts & Notifications Preferences
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = tokens.surface,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    tokens.borderSubtle
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Live Alerts & Notification Triggers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    AccountSwitchRow(
                        title = "Push Notifications for New Jobs",
                        subtitle = "Instant alert whenever a relevant quota seat is announced",
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it },
                        tokens = tokens
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = tokens.divider
                    )

                    AccountSwitchRow(
                        title = "Admit Card Download Alerts",
                        subtitle = "Notify as soon as the hall ticket link goes live",
                        checked = admitCardAlerts,
                        onCheckedChange = { admitCardAlerts = it },
                        tokens = tokens
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = tokens.divider
                    )

                    AccountSwitchRow(
                        title = "Exam Schedule & Date Alerts",
                        subtitle = "Live notice of exam dates and shift timings",
                        checked = examDateAlerts,
                        onCheckedChange = { examDateAlerts = it },
                        tokens = tokens
                    )
                }
            }
        }

        // Appearance & Information
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = tokens.surface,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    tokens.borderSubtle
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Appearance & System Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (tokens.isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = tokens.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Dark Mode",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = if (tokens.isDark) "Obsidian dark mode active" else "Warm alabaster light mode active",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                            }
                        }

                        Switch(
                            checked = tokens.isDark,
                            onCheckedChange = { onToggleTheme() }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = tokens.divider
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            com.example.ui.components.JobPulseSymbol(size = 32.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "JobPulse Platform",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = "v2.5 • Modern Indian Recruitment Intelligence",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textTertiary
                                )
                            }
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
private fun ProfileItem(icon: ImageVector, label: String, value: String, tokens: com.example.ui.theme.AppThemeTokens) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tokens.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tokens.textTertiary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = tokens.textPrimary
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
    tokens: com.example.ui.theme.AppThemeTokens
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
                color = tokens.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
