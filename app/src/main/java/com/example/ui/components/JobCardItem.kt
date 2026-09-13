package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.model.Job
import com.example.model.RecruitmentStatus
import com.example.ui.theme.AppThemeTokens
import com.example.ui.theme.LocalAppThemeTokens
import com.example.ui.theme.OrgBrandingRegistry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal val standardDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JobCardItem(
    job: Job,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier,
    // Compatibility with existing call sites; no double-tap gesture is installed.
    onDoubleTap: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    onOpenDetails: () -> Unit = onDoubleTap
) {
    var expanded by rememberSaveable(job.id) { mutableStateOf(false) }
    val tokens = LocalAppThemeTokens.current
    val branding = remember(job.organization, job.title) { OrgBrandingRegistry.forJob(job) }
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val scale = animateFloatAsState(
        if (pressed) 0.99f else 1f,
        spring(dampingRatio = 1f, stiffness = Spring.StiffnessHigh), label = "card_press"
    )
    val shape = RoundedCornerShape(tokens.cardRadius)
    val base = modifier.fillMaxWidth().testTag("job_card_${job.id}")
    val bounds = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            base.sharedBounds(
                rememberSharedContentState("job_card_bounds_${job.id}"), animatedVisibilityScope,
                boundsTransform = { _, _ -> spring(dampingRatio = 1f, stiffness = 600f) }
            )
        }
    } else base
    Surface(
        modifier = bounds.graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clip(shape)
            .clickable(
                interactionSource = interactions, indication = null, role = Role.Button,
                onClickLabel = if (expanded) "Collapse preview" else "Expand preview",
                onClick = { expanded = !expanded }
            )
            .semantics { stateDescription = if (expanded) "Expanded" else "Collapsed" },
        color = tokens.surface, shape = shape
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val seal = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier.sharedElement(rememberSharedContentState("job_seal_${job.id}"), animatedVisibilityScope)
                    }
                } else Modifier
                Box(seal.size(44.dp).clip(RoundedCornerShape(12.dp)).background(tokens.surfaceSubtle), contentAlignment = Alignment.Center) {
                    Image(painterResource(branding.logoResId), null, Modifier.size(28.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(job.organization, Modifier.weight(1f), color = tokens.textSecondary, style = MaterialTheme.typography.bodyMedium)
                IconButton(onBookmarkToggle, Modifier.size(48.dp).testTag("bookmark_${job.id}")) {
                    Icon(
                        if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        if (isBookmarked) "Remove Bookmark" else "Bookmark Job",
                        tint = if (isBookmarked) tokens.primary else tokens.textSecondary
                    )
                }
            }
            val title = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                with(sharedTransitionScope) {
                    Modifier.sharedBounds(
                        rememberSharedContentState("job_title_${job.id}"), animatedVisibilityScope,
                        boundsTransform = { _, _ -> spring(dampingRatio = 1f, stiffness = 600f) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
                }
            } else Modifier
            Text(job.title, title, color = tokens.textPrimary, style = MaterialTheme.typography.titleLarge)
            Text(listOf(job.level, job.location).filter { it.isNotBlank() }.joinToString(" · "), color = tokens.textSecondary, style = MaterialTheme.typography.bodyMedium)
            Text(job.salary.ifBlank { "Pay not announced" }, color = tokens.textPrimary, style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(if (job.seats > 0) "${job.seats} vacancies" else "Vacancies not announced", color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall)
                    RecruitmentStatusBadge(job.getEffectiveStatus(), tokens)
                }
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = tokens.textSecondary)
            }
            AnimatedVisibility(
                expanded,
                enter = expandVertically(spring(dampingRatio = 1f, stiffness = 600f)) + fadeIn(tween(140)),
                exit = shrinkVertically(spring(dampingRatio = 1f, stiffness = 600f)) + fadeOut(tween(100))
            ) {
                Column(Modifier.testTag("job_preview_${job.id}"), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HorizontalDivider(color = tokens.divider)
                    PreviewField("Deadline", job.applicationClosingDate)
                    PreviewField("Qualification", job.minQualification)
                    PreviewField("Age", job.ageLimit)
                    PreviewField("Reservation", job.quota)
                    Button(onOpenDetails, Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("open_job_${job.id}"), shape = RoundedCornerShape(tokens.buttonRadius)) {
                        Text("View full details", Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewField(label: String, value: String) {
    val tokens = LocalAppThemeTokens.current
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, color = tokens.textSecondary, style = MaterialTheme.typography.labelMedium)
        Text(value.ifBlank { "Not announced" }, color = tokens.textPrimary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun JobCardSkeleton() {
    val tokens = LocalAppThemeTokens.current
    Surface(shape = RoundedCornerShape(tokens.cardRadius), color = tokens.surface, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(tokens.surfaceSubtle))
            listOf(0.85f, 0.65f, 0.75f, 0.5f).forEach { fraction ->
                Box(Modifier.fillMaxWidth(fraction).height(20.dp).clip(RoundedCornerShape(6.dp)).background(tokens.surfaceSubtle))
            }
        }
    }
}

@Composable
fun RecruitmentStatusBadge(status: RecruitmentStatus, tokens: AppThemeTokens, modifier: Modifier = Modifier) {
    val label = when (status) {
        RecruitmentStatus.APPLICATION_OPEN -> "Applications open"
        RecruitmentStatus.CLOSING_SOON -> "Closing soon"
        RecruitmentStatus.APPLICATION_CLOSED -> "Applications closed"
        else -> status.displayName
    }
    Text(label, modifier, color = if (status == RecruitmentStatus.CLOSING_SOON) tokens.danger else tokens.textSecondary, style = MaterialTheme.typography.labelMedium)
}

/** Date-only deadlines remain open through their stated calendar day. */
internal fun Job.getEffectiveStatus(nowMillis: Long = System.currentTimeMillis()): RecruitmentStatus {
    if (status != RecruitmentStatus.APPLICATION_OPEN || applicationClosingDate.isBlank()) return status
    val closing = listOf("dd MMM yyyy", "yyyy-MM-dd").firstNotNullOfOrNull { pattern ->
        val parser = SimpleDateFormat(pattern, Locale.ENGLISH).apply { isLenient = false }
        val position = java.text.ParsePosition(0)
        parser.parse(applicationClosingDate.trim(), position)?.takeIf { position.index == applicationClosingDate.trim().length }
    } ?: return status
    val today = Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val soon = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7) }
    return when {
        closing.before(today.time) -> RecruitmentStatus.APPLICATION_CLOSED
        !closing.after(soon.time) -> RecruitmentStatus.CLOSING_SOON
        else -> RecruitmentStatus.APPLICATION_OPEN
    }
}
