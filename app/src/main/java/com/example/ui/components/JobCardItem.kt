package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.model.Job
import com.example.ui.theme.AppColors
import com.example.ui.theme.OrgBrandingRegistry

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JobCardItem(
    job: Job,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onDoubleTap: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    // Authentic Organization Branding & Palette
    val branding = remember(job.organization, job.title) { OrgBrandingRegistry.forJob(job) }

    // Subtle tactile scale spring on tap/press
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.982f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale_${job.id}"
    )

    // Coordinated corner radius
    val cornerRadius = 22.dp
    val cardShape = RoundedCornerShape(cornerRadius)

    // Specular border gradient simulating refined glass edge
    val specularBorder = remember(isDark, branding) {
        Brush.verticalGradient(
            colors = if (isDark) {
                listOf(branding.borderSpecularTop, AppColors.DarkBorderSubtle)
            } else {
                listOf(Color.White.copy(alpha = 0.9f), AppColors.LightBorderSubtle)
            }
        )
    }

    val cardBaseModifier = modifier
        .fillMaxWidth()
        .scale(scaleAnim)
        .testTag("job_card_${job.id}")
        .animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        .pointerInput(expanded) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    try {
                        awaitRelease()
                    } finally {
                        isPressed = false
                    }
                },
                onTap = { expanded = !expanded },
                onDoubleTap = { onDoubleTap() }
            )
        }

    // Shared bounds container transition wiring
    val sharedCardModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            cardBaseModifier.sharedBounds(
                rememberSharedContentState(key = "job_card_bounds_${job.id}"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    tween(durationMillis = 380, easing = FastOutSlowInEasing)
                }
            )
        }
    } else cardBaseModifier

    Surface(
        modifier = sharedCardModifier
            .shadow(
                elevation = if (isDark) 8.dp else 4.dp,
                shape = cardShape,
                spotColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0x1A000000),
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.3f) else Color(0x0A000000)
            )
            .clip(cardShape)
            .border(width = 1.dp, brush = specularBorder, shape = cardShape),
        color = Color.Transparent,
        shape = cardShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) branding.surfaceGradientDark else branding.surfaceGradientLight)
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header: Organization Seal, Name, Verification, Bookmark
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Organization Emblem Badge
                    val sealModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                        with(sharedTransitionScope) {
                            Modifier.sharedElement(
                                rememberSharedContentState(key = "job_seal_${job.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    } else Modifier

                    Box(
                        modifier = sealModifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) branding.badgeSurfaceDark else branding.badgeSurfaceLight)
                            .border(
                                1.dp,
                                if (isDark) branding.borderSpecularTop.copy(alpha = 0.5f) else branding.primaryColor.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = branding.icon,
                            contentDescription = branding.orgName,
                            tint = if (isDark) branding.badgeTextDark else branding.primaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Organization Title & Subtext
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = branding.orgName.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) branding.badgeTextDark else branding.primaryColor,
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Official",
                                tint = if (isDark) AppColors.SuccessGreen else branding.primaryColor,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = branding.authoritySubtext,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Bookmark Tactile Button
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isBookmarked) {
                                    branding.primaryColor.copy(alpha = if (isDark) 0.25f else 0.12f)
                                } else {
                                    Color.Transparent
                                }
                            )
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Job",
                            tint = if (isBookmarked) branding.primaryColor else if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Job Title with Shared Element
                val titleModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier.sharedElement(
                            rememberSharedContentState(key = "job_title_${job.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                } else Modifier

                Text(
                    text = job.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 24.sp,
                    modifier = titleModifier
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Structured Metrics Grid (Level, Location, Salary, Vacancy Count)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            icon = Icons.Default.WorkOutline,
                            label = job.level,
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            icon = Icons.Default.LocationOn,
                            label = job.location,
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            icon = Icons.Default.Payments,
                            label = job.salary,
                            isDark = isDark,
                            modifier = Modifier.weight(1.2f)
                        )
                        VacancyHighlightPill(
                            seats = job.seats,
                            brandingColor = branding.primaryColor,
                            isDark = isDark,
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }

                // Interactive Expand Cue (Subtle chevron hint)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "Tap to collapse" else "Single tap for details • Double tap full view",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Inline Smooth Single-Tap Expansion
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(220)) + expandVertically(tween(280)),
                    exit = fadeOut(tween(180)) + shrinkVertically(tween(220))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        HorizontalDivider(
                            color = if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle,
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Application Deadline & Timeline Highlight
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = branding.primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "APPLICATION DEADLINE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
                                    )
                                    Text(
                                        text = job.applicationClosingDate,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AppColors.DangerRed.copy(alpha = if (isDark) 0.20f else 0.12f)
                                ) {
                                    Text(
                                        text = "Closing Soon",
                                        color = AppColors.DangerRed,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quota & Reservation Breakdown Summary
                        Text(
                            text = "Reservation & Quota Scheme",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = job.quota,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Minimum Qualification & Age
                        Text(
                            text = "Eligibility Summary",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Qualification: ${job.minQualification} • Age: ${job.ageLimit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Actions Row: Apply / Portal + PDF Notice + Full Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Primary Apply / Official Button
                            Button(
                                onClick = {
                                    val targetUrl = job.applyUrl ?: job.officialSiteUrl
                                    val intent = Intent(Intent.ACTION_VIEW, targetUrl.toUri())
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = branding.primaryColor,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.weight(1.1f),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text(
                                    text = if (job.applyUrl != null) "Apply Now" else "Official Portal",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // Secondary PDF Notice Button
                            OutlinedButton(
                                onClick = {
                                    val targetUrl = job.noticeUrl.ifEmpty { job.officialSiteUrl }
                                    val intent = Intent(Intent.ACTION_VIEW, targetUrl.toUri())
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDark) AppColors.DarkBorder else AppColors.LightBorder
                                ),
                                modifier = Modifier.weight(0.9f),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "PDF Notice",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Full Details Experience Trigger
                            FilledTonalIconButton(
                                onClick = onDoubleTap,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(42.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                                    contentColor = branding.primaryColor
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Open Full Screen Recruitment Details",
                                    modifier = Modifier.size(18.dp)
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
private fun MetricPill(
    icon: ImageVector,
    label: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isDark) AppColors.DarkSurfaceSubtle else AppColors.LightSurfaceSubtle,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun VacancyHighlightPill(
    seats: Int,
    brandingColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = brandingColor.copy(alpha = if (isDark) 0.18f else 0.10f),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            brandingColor.copy(alpha = if (isDark) 0.35f else 0.20f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = brandingColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$seats Seats",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) Color.White else brandingColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun JobCardSkeleton() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val isDark = isSystemInDarkTheme()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_float"
    )

    val shimmerColors = if (isDark) {
        listOf(
            Color(0xFF1E232B),
            Color(0xFF282F3A),
            Color(0xFF1E232B)
        )
    } else {
        listOf(
            Color(0xFFE8ECEF),
            Color(0xFFF4F7F9),
            Color(0xFFE8ECEF)
        )
    }

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim, y = translateAnim)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )
    }
}
