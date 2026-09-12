package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AppColors
import com.example.ui.theme.LocalAppThemeTokens

/**
 * Premium Liquid-Glass Notification Permission & Subscription Dialog.
 *
 * Provides a clear, aspirant-centric rationale for push notification permissions
 * on first app launch and on-demand via the top-bar alert action.
 */
@Composable
fun NotificationPermissionDialog(
    isPermissionGranted: Boolean,
    onEnableClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onSendSampleAlert: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalAppThemeTokens.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tokens.scrim)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            LiquidGlassBox(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag("notification_permission_dialog"),
                shape = RoundedCornerShape(26.dp),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Row with Close Button
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                                .testTag("btn_close_notification_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = tokens.textTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Luminous Hero Icon Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = if (isPermissionGranted) {
                                            listOf(
                                                tokens.success.copy(alpha = if (tokens.isDark) 0.35f else 0.22f),
                                                tokens.success.copy(alpha = 0.08f)
                                            )
                                        } else {
                                            listOf(
                                                tokens.primary.copy(alpha = if (tokens.isDark) 0.40f else 0.25f),
                                                tokens.accent.copy(alpha = 0.08f)
                                            )
                                        }
                                    )
                                )
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.verticalGradient(
                                        colors = if (isPermissionGranted) {
                                            listOf(tokens.success, tokens.success.copy(alpha = 0.3f))
                                        } else {
                                            listOf(tokens.primary, tokens.accent.copy(alpha = 0.3f))
                                        }
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPermissionGranted) Icons.Default.CheckCircle else Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = if (isPermissionGranted) tokens.success else tokens.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title & Description
                    Text(
                        text = if (isPermissionGranted) "Live Alerts Active" else "Never Miss a Sarkari Vacancy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isPermissionGranted) {
                            "You are currently subscribed to instant recruitment updates, hall ticket releases, and exam milestones."
                        } else {
                            "Enable push notifications to receive real-time updates for newly announced Central & State Government vacancies."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feature highlights container
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = tokens.surfaceSubtle.copy(alpha = if (tokens.isDark) 0.65f else 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.borderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            NotificationFeatureRow(
                                icon = Icons.Default.Speed,
                                iconTint = tokens.primary,
                                title = "Instant Notice Broadcasts",
                                subtitle = "Be the first to know when new posts are officially notified",
                                tokens = tokens
                            )

                            NotificationFeatureRow(
                                icon = Icons.Default.DateRange,
                                iconTint = tokens.brandSaffron,
                                title = "Admit Cards & Exam Dates",
                                subtitle = "Timely reminders for hall tickets, shift timings & results",
                                tokens = tokens
                            )

                            NotificationFeatureRow(
                                icon = Icons.Default.HourglassBottom,
                                iconTint = tokens.warning,
                                title = "Deadline Warnings",
                                subtitle = "48-hour advance alerts before application links close",
                                tokens = tokens
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Privacy Assurance Note
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = tokens.textTertiary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Zero spam. Control or mute alerts anytime in Profile.",
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.textTertiary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Action Buttons
                    if (!isPermissionGranted) {
                        Button(
                            onClick = onEnableClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_enable_notifications"),
                            shape = RoundedCornerShape(tokens.buttonRadius),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tokens.primary,
                                contentColor = tokens.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enable Notifications",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_maybe_later")
                        ) {
                            Text(
                                text = "Maybe Later",
                                color = tokens.textTertiary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        // Already Granted State Actions: Send Sample Alert or Close
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (onSendSampleAlert != null) {
                                OutlinedButton(
                                    onClick = onSendSampleAlert,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("btn_send_sample_alert"),
                                    shape = RoundedCornerShape(tokens.buttonRadius),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                        tint = tokens.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Test Alert",
                                        color = tokens.textPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Button(
                                onClick = onDismissRequest,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_done_notifications"),
                                shape = RoundedCornerShape(tokens.buttonRadius),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = tokens.primary,
                                    contentColor = tokens.onPrimary
                                )
                            ) {
                                Text(
                                    text = "Done",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        if (onOpenSettings != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = onOpenSettings,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = tokens.textTertiary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "System Notification Settings",
                                    color = tokens.textTertiary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationFeatureRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    tokens: com.example.ui.theme.AppThemeTokens
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = if (tokens.isDark) 0.18f else 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
