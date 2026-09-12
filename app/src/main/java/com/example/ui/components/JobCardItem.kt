package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
import java.text.SimpleDateFormat
import java.util.Locale

// Thread-safe cached formatter for recruitment deadline calculations
internal val standardDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

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
    val context = LocalContext.current
    val tokens = LocalAppThemeTokens.current

    // Authentic Organization Branding & Palette
    val branding = remember(job.organization, job.title) { OrgBrandingRegistry.forJob(job) }
    val effectiveStatus = remember(job.status, job.applicationClosingDate) { job.getEffectiveStatus() }

    val cardShape = RoundedCornerShape(tokens.cardRadius)

    // Subtle specular border for quiet depth without excessive neon glow
    val cardBorderBrush = remember(tokens.isDark, branding) {
        Brush.verticalGradient(
            colors = listOf(
                branding.getBorderSpecularTop(tokens.isDark).copy(alpha = if (tokens.isDark) 0.35f else 0.20f),
                tokens.borderSubtle
            )
        )
    }

    // Zero-latency gesture recognizer:
    // 1. Fires single-tap expansion INSTANTLY on finger lift (zero double-tap delay).
    // 2. Preserves double-tap to open full details if a second tap occurs within 320ms.
    // 3. Respects viewConfiguration.touchSlop so scroll/fling gestures pass immediately to LazyColumn.
    val cardGestureModifier = Modifier
        .pointerInput(job.id) {
            var lastTapTime = 0L
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val touchSlop = viewConfiguration.touchSlop
                var isTap = true
                var pointerUp: androidx.compose.ui.input.pointer.PointerInputChange? = null

                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (change.isConsumed) {
                        isTap = false
                        break
                    }
                    val distance = (change.position - down.position).getDistance()
                    if (distance > touchSlop) {
                        isTap = false
                        break
                    }
                    if (!change.pressed) {
                        pointerUp = change
                        break
                    }
                }

                if (isTap && pointerUp != null) {
                    pointerUp.consume()
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTapTime in 1..320L) {
                        onDoubleTap()
                        lastTapTime = 0L
                    } else {
                        expanded = !expanded
                        lastTapTime = currentTime
                    }
                }
            }
        }
        .semantics {
            role = Role.Button
            onClick(label = if (expanded) "Collapse details" else "Expand details") {
                expanded = !expanded
                true
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
                    tween(durationMillis = tokens.durationMedium, easing = tokens.standardEasing)
                }
            )
        }
    } else cardBaseModifier

    Surface(
        modifier = sharedCardModifier
            .shadow(
                elevation = if (tokens.isDark) 3.dp else 2.dp,
                shape = cardShape,
                spotColor = tokens.glassShadow.copy(alpha = 0.25f),
                ambientColor = tokens.glassShadow.copy(alpha = 0.10f)
            )
            .clip(cardShape)
            .border(width = 1.dp, brush = cardBorderBrush, shape = cardShape),
        color = tokens.surface,
        shape = cardShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Official Organization Emblem, Name, Status Badge, Bookmark
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
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(branding.getBadgeSurface(tokens.isDark))
                        .border(
                            1.dp,
                            branding.getBorderSpecularTop(tokens.isDark).copy(alpha = 0.40f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = branding.logoResId),
                        contentDescription = branding.orgName,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Organization Title & Provenance Subtext
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = branding.orgName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = branding.getBadgeText(tokens.isDark),
                        letterSpacing = 0.4.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = branding.authoritySubtext,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Compact Status Badge
                RecruitmentStatusBadge(
                    status = effectiveStatus,
                    tokens = tokens
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Bookmark Button with safe touch target
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isBookmarked) {
                                tokens.primary.copy(alpha = if (tokens.isDark) 0.22f else 0.12f)
                            } else {
                                Color.Transparent
                            }
                        )
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Job",
                        tint = if (isBookmarked) tokens.primary else tokens.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Job Title with Shared Bounds for smooth detail transition
            val titleModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                with(sharedTransitionScope) {
                    Modifier.sharedBounds(
                        rememberSharedContentState(key = "job_title_${job.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = tokens.durationMedium, easing = tokens.standardEasing)
                        },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
                }
            } else Modifier

            Text(
                text = job.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp,
                modifier = titleModifier
            )

            Spacer(modifier = Modifier.height(12.dp))

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

            // Interactive Expand Cue
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

            // Inline Single-Tap Expansion Area (constrained strictly to this child layout)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(180)) + expandVertically(tween(220)),
                exit = fadeOut(tween(140)) + shrinkVertically(tween(180))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    HorizontalDivider(
                        color = tokens.divider,
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Application Deadline & Timeline Highlight
                    Surface(
                        shape = RoundedCornerShape(10.dp),
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
                                tint = tokens.primary,
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

                    // Minimum Qualification & Age Summary
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

                    Spacer(modifier = Modifier.height(16.dp))

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
                            shape = RoundedCornerShape(tokens.buttonRadius),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tokens.primary,
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
                            shape = RoundedCornerShape(tokens.buttonRadius),
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

                        // Full Details Experience Trigger (Accessible Single Tap Alternative to Double Tap)
                        FilledTonalIconButton(
                            onClick = onDoubleTap,
                            shape = RoundedCornerShape(tokens.buttonRadius),
                            modifier = Modifier.size(42.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = tokens.surfaceElevated,
                                contentColor = tokens.primary
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

@Composable
private fun MetricPill(
    icon: ImageVector,
    label: String,
    tokens: AppThemeTokens,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(tokens.chipRadius),
        color = tokens.surfaceElevated,
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
                modifier = Modifier.size(14.dp)
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
    val highlightColor = if (tokens.isDark) tokens.brandSaffron else brandingColor
    Surface(
        shape = RoundedCornerShape(tokens.chipRadius),
        color = highlightColor.copy(alpha = if (tokens.isDark) 0.16f else 0.10f),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            highlightColor.copy(alpha = if (tokens.isDark) 0.35f else 0.20f)
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
                tint = highlightColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$seats Seats",
                style = MaterialTheme.typography.bodySmall,
                color = if (tokens.isDark) Color.White else highlightColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun JobCardSkeleton() {
    val tokens = LocalAppThemeTokens.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(tokens.cardRadius),
        color = tokens.surfaceElevated,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, tokens.borderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(tokens.surfaceSubtle)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(tokens.surfaceSubtle)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(tokens.surfaceSubtle)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(tokens.surfaceSubtle)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tokens.surfaceSubtle)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tokens.surfaceSubtle)
                )
            }
        }
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
            "OPEN",
            tokens.success.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.success
        )
        RecruitmentStatus.CLOSING_SOON -> Triple(
            "CLOSING SOON",
            tokens.danger.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.danger
        )
        RecruitmentStatus.APPLICATION_CLOSED -> Triple(
            "CLOSED",
            tokens.textTertiary.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.textSecondary
        )
        RecruitmentStatus.PUBLISHED -> Triple(
            "INCOMING",
            tokens.primary.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.primary
        )
        RecruitmentStatus.EXAM_SCHEDULED -> Triple(
            "EXAM PHASE",
            tokens.accent.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.accent
        )
        RecruitmentStatus.ADMIT_CARD_RELEASED -> Triple(
            "ADMIT CARD",
            tokens.accent.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.accent
        )
        RecruitmentStatus.RESULT_RELEASED -> Triple(
            "RESULT OUT",
            tokens.primary.copy(alpha = if (tokens.isDark) 0.20f else 0.12f),
            tokens.primary
        )
        else -> Triple(
            status.displayName.uppercase(),
            tokens.surfaceElevated,
            tokens.textSecondary
        )
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, fg.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

internal fun Job.getEffectiveStatus(): RecruitmentStatus {
    if (status != RecruitmentStatus.APPLICATION_OPEN) return status
    if (applicationClosingDate.isBlank()) return status
    return try {
        val now = System.currentTimeMillis()
        val closingDate = synchronized(standardDateFormat) {
            standardDateFormat.parse(applicationClosingDate.trim())
        }
        if (closingDate != null) {
            val diffMs = closingDate.time - now
            val diffDays = diffMs / (1000L * 60 * 60 * 24)
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
