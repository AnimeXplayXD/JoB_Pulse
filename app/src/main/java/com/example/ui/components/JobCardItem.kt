package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.model.Job
import com.example.model.RecruitmentStatus
import com.example.ui.theme.AppThemeTokens
import com.example.ui.theme.LocalAppThemeTokens
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
    val tokens = LocalAppThemeTokens.current

    // Authentic Organization Branding & Palette
    val branding = remember(job.organization, job.title) { OrgBrandingRegistry.forJob(job) }
    val effectiveStatus = remember(job.status, job.applicationClosingDate) { job.getEffectiveStatus() }

    // Subtle tactile scale spring on tap/press using graphicsLayer for zero recomposition jank
    val scaleAnim = animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale_${job.id}"
    )

    val cardShape = RoundedCornerShape(22.dp)

    // Specular border gradient simulating refined liquid-glass edge
    val specularBorder = remember(tokens.isDark, branding) {
        Brush.verticalGradient(
            colors = listOf(
                branding.getBorderSpecularTop(tokens.isDark),
                tokens.borderSubtle
            )
        )
    }

    // Zero-latency gesture detector: immediate tactile response + immediate expansion on single tap,
    // while preserving seamless double-tap detection.
    val cardGestureModifier = Modifier
        .pointerInput(job.id) {
            var lastTapTime = 0L
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                val downTime = System.currentTimeMillis()
                val up = waitForUpOrCancellation()
                isPressed = false
                if (up != null && !up.isConsumed) {
                    val upTime = System.currentTimeMillis()
                    if (upTime - downTime < 400L) {
                        up.consume()
                        if (upTime - lastTapTime < 340L) {
                            lastTapTime = 0L
                            onDoubleTap()
                        } else {
                            lastTapTime = upTime
                            expanded = !expanded
                        }
                    }
                }
            }
        }

    val cardBaseModifier = modifier
        .fillMaxWidth()
        .then(cardGestureModifier)
        .testTag("job_card_${job.id}")

    // Shared bounds container transition wiring
    val sharedCardModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            cardBaseModifier.sharedBounds(
                rememberSharedContentState(key = "job_card_bounds_${job.id}"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    tween(durationMillis = 320, easing = FastOutSlowInEasing)
                }
            )
        }
    } else cardBaseModifier

    Surface(
        modifier = sharedCardModifier
            .shadow(
                elevation = if (tokens.isDark) 8.dp else 4.dp,
                shape = cardShape,
                spotColor = if (tokens.isDark) Color.Black.copy(alpha = 0.5f) else Color(0x1A000000),
                ambientColor = if (tokens.isDark) Color.Black.copy(alpha = 0.3f) else Color(0x0A000000)
            )
            .clip(cardShape)
            .border(width = 1.dp, brush = specularBorder, shape = cardShape),
        color = Color.Transparent,
        shape = cardShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                }
                .background(branding.getSurfaceGradient(tokens.isDark))
                .padding(20.dp)
        ) {
            // Subtle Official Watermark Motif integrated into the liquid-glass background
            Icon(
                imageVector = branding.watermarkIcon,
                contentDescription = null,
                tint = branding.getWatermarkColor(tokens.isDark),
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-6).dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                // Header: Official Organization Emblem, Name, Verification, Bookmark
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Organization Emblem Badge with authentic vector logo
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
                            .size(46.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(branding.getBadgeSurface(tokens.isDark))
                            .border(
                                1.dp,
                                branding.getBorderSpecularTop(tokens.isDark),
                                RoundedCornerShape(13.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = branding.logoResId),
                            contentDescription = branding.orgName,
                            modifier = Modifier.size(30.dp)
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
                                color = branding.getBadgeText(tokens.isDark),
                                letterSpacing = 0.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Official",
                                tint = if (tokens.isDark) tokens.success else branding.getPrimaryColor(tokens.isDark),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = branding.authoritySubtext,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textTertiary,
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
                                    branding.getPrimaryColor(tokens.isDark).copy(alpha = if (tokens.isDark) 0.25f else 0.12f)
                                } else {
                                    Color.Transparent
                                }
                            )
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Job",
                            tint = if (isBookmarked) branding.getPrimaryColor(tokens.isDark) else tokens.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Job Title with Shared Bounds for perfectly continuous, flicker-free expansion
                val titleModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier.sharedBounds(
                            rememberSharedContentState(key = "job_title_${job.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = 320, easing = FastOutSlowInEasing)
                            },
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        )
                    }
                } else Modifier

                Text(
                    text = job.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary,
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
                            tokens = tokens,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            icon = Icons.Default.LocationOn,
                            label = job.location,
                            tokens = tokens,
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
                            tokens = tokens,
                            modifier = Modifier.weight(1.2f)
                        )
                        VacancyHighlightPill(
                            seats = job.seats,
                            brandingColor = branding.getPrimaryColor(tokens.isDark),
                            tokens = tokens,
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
                        color = tokens.textTertiary
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = tokens.textTertiary,
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
                            color = tokens.borderSubtle,
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Application Deadline & Timeline Highlight
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tokens.surfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = branding.getPrimaryColor(tokens.isDark),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "APPLICATION DEADLINE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.textTertiary
                                    )
                                    Text(
                                        text = job.applicationClosingDate.ifBlank { "Not announced" },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = tokens.textPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                RecruitmentStatusBadge(
                                    status = effectiveStatus,
                                    tokens = tokens
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quota & Reservation Breakdown Summary
                        Text(
                            text = "Reservation & Quota Scheme",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = job.quota.ifBlank { "Not available" },
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Minimum Qualification & Age
                        Text(
                            text = "Eligibility Summary",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Qualification: ${job.minQualification} • Age: ${job.ageLimit.ifBlank { "Not announced" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textPrimary,
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
                                    containerColor = branding.getPrimaryColor(tokens.isDark),
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
                                    contentColor = tokens.textPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    tokens.border
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
                                    containerColor = tokens.surfaceElevated,
                                    contentColor = branding.getPrimaryColor(tokens.isDark)
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
    tokens: AppThemeTokens,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = tokens.surfaceSubtle,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tokens.textSecondary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textPrimary,
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
    tokens: AppThemeTokens,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = brandingColor.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            brandingColor.copy(alpha = if (tokens.isDark) 0.40f else 0.22f)
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
                color = if (tokens.isDark) Color.White else brandingColor,
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
    val tokens = LocalAppThemeTokens.current
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_float"
    )

    val shimmerColors = if (tokens.isDark) {
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

@Composable
fun RecruitmentStatusBadge(
    status: RecruitmentStatus,
    tokens: AppThemeTokens,
    modifier: Modifier = Modifier
) {
    val (label, bg, fg) = when (status) {
        RecruitmentStatus.APPLICATION_OPEN -> Triple(
            "Applications Open",
            tokens.success.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.success
        )
        RecruitmentStatus.CLOSING_SOON -> Triple(
            "Closing Soon",
            tokens.danger.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.danger
        )
        RecruitmentStatus.APPLICATION_CLOSED -> Triple(
            "Closed",
            tokens.textTertiary.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.textSecondary
        )
        RecruitmentStatus.PUBLISHED -> Triple(
            "Notification Released",
            tokens.primary.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.primary
        )
        RecruitmentStatus.EXAM_SCHEDULED -> Triple(
            "Exam Scheduled",
            tokens.accent.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.accent
        )
        RecruitmentStatus.ADMIT_CARD_RELEASED -> Triple(
            "Admit Card Live",
            tokens.accent.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.accent
        )
        RecruitmentStatus.RESULT_RELEASED -> Triple(
            "Results Declared",
            tokens.primary.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.primary
        )
        else -> Triple(
            status.displayName,
            tokens.surfaceElevated,
            tokens.textSecondary
        )
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

internal fun Job.getEffectiveStatus(): RecruitmentStatus {
    if (status != RecruitmentStatus.APPLICATION_OPEN) return status
    return try {
        val format = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH)
        val now = java.util.Calendar.getInstance().time
        val closingDate = format.parse(applicationClosingDate.trim())
        if (closingDate != null) {
            val diffMs = closingDate.time - now.time
            val diffDays = diffMs / (1000 * 60 * 60 * 24)
            if (diffDays < 0) {
                RecruitmentStatus.APPLICATION_CLOSED
            } else if (diffDays <= 7) {
                RecruitmentStatus.CLOSING_SOON
            } else {
                RecruitmentStatus.APPLICATION_OPEN
            }
        } else {
            status
        }
    } catch (_: Exception) {
        status
    }
}

