package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.model.*
import com.example.ui.theme.AppThemeTokens
import com.example.ui.theme.LocalAppThemeTokens
import com.example.ui.theme.OrgBrandingRegistry

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    job: Job,
    onBack: () -> Unit,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokens = LocalAppThemeTokens.current
    val branding = remember(job.organization, job.title) { OrgBrandingRegistry.forJob(job) }
    val scrollState = rememberScrollState()

    with(sharedTransitionScope) {
        Scaffold(
            containerColor = tokens.background,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = branding.orgName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                color = tokens.textPrimary
                            )
                            Text(
                                text = "Official Recruitment Bulletin",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.textSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Return to Feed",
                                tint = tokens.textPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onBookmarkToggle) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = if (isBookmarked) "Bookmarked" else "Bookmark",
                                tint = if (isBookmarked) branding.getPrimaryColor(tokens.isDark) else tokens.textSecondary
                            )
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "${job.title} - ${job.organization}")
                                putExtra(Intent.EXTRA_TEXT, "JobPulse Alert: ${job.title} (${job.seats} Vacancies) announced by ${job.organization}. Apply at: ${job.applyUrl ?: job.officialSiteUrl}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Job Announcement"))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = tokens.textSecondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = tokens.background,
                        titleContentColor = tokens.textPrimary
                    )
                )
            },
            bottomBar = {
                // Sticky Action Surface with direct Application & Official PDF triggers
                Surface(
                    color = tokens.surface,
                    shadowElevation = 16.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        tokens.borderSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                val url = job.noticeUrl.ifEmpty { job.officialSiteUrl }
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                tokens.border
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = tokens.textPrimary
                            ),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Official PDF", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                val url = job.applyUrl ?: job.officialSiteUrl
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = branding.getPrimaryColor(tokens.isDark),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1.3f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = if (job.applyUrl != null) "Apply Online" else "Official Portal",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .testTag("job_detail_screen")
            ) {
                // Shared Bounds Hero Card transforming from the originating JobCard
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sharedBounds(
                            rememberSharedContentState(key = "job_card_bounds_${job.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = 320, easing = FastOutSlowInEasing)
                            }
                        )
                        .background(branding.getSurfaceGradient(tokens.isDark))
                        .padding(22.dp)
                ) {
                    // Subtle Official Watermark Motif integrated into the liquid-glass background
                    Icon(
                        imageVector = branding.watermarkIcon,
                        contentDescription = null,
                        tint = branding.getWatermarkColor(tokens.isDark),
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 24.dp, y = (-12).dp)
                    )

                    Column {
                        // Organization Identity Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "job_seal_${job.id}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(branding.getBadgeSurface(tokens.isDark))
                                    .border(
                                        1.dp,
                                        branding.getBorderSpecularTop(tokens.isDark),
                                        RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = branding.logoResId),
                                    contentDescription = branding.orgName,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = branding.orgName.uppercase(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = branding.getBadgeText(tokens.isDark),
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (job.isOfficialSource) {
                                            tokens.success.copy(alpha = if (tokens.isDark) 0.20f else 0.12f)
                                        } else {
                                            tokens.accent.copy(alpha = if (tokens.isDark) 0.20f else 0.12f)
                                        }
                                    ) {
                                        Text(
                                            text = if (job.isOfficialSource) "OFFICIAL NOTIFICATION" else "AGGREGATED NOTICE",
                                            color = if (job.isOfficialSource) tokens.success else tokens.accent,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = branding.authoritySubtext,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textTertiary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Shared Title with ScaleToBounds for continuous, flicker-free expansion
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary,
                            lineHeight = 32.sp,
                            modifier = Modifier.sharedBounds(
                                rememberSharedContentState(key = "job_title_${job.id}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 320, easing = FastOutSlowInEasing)
                                },
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Key Metrics Pill Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HeroDetailChip(
                                icon = Icons.Default.Groups,
                                title = "VACANCIES",
                                value = if (job.seats > 0) "${job.seats} Posts" else "Not announced",
                                tokens = tokens,
                                modifier = Modifier.weight(1f)
                            )
                            HeroDetailChip(
                                icon = Icons.Default.Payments,
                                title = "PAY MATRIX",
                                value = job.salary.ifBlank { "Not announced" },
                                tokens = tokens,
                                modifier = Modifier.weight(1.3f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HeroDetailChip(
                                icon = Icons.Default.LocationOn,
                                title = "JURISDICTION",
                                value = job.location.ifBlank { "All India" },
                                tokens = tokens,
                                modifier = Modifier.weight(1f)
                            )
                            HeroDetailChip(
                                icon = Icons.Default.School,
                                title = "QUALIFICATION",
                                value = job.minQualification.ifBlank { "Graduate" },
                                tokens = tokens,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Main Content Body Organized Into Distinct Visual Sections
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Timeline Milestones Section
                    RecruitmentSection(title = "Important Dates & Timeline", icon = Icons.Default.CalendarMonth, tokens = tokens) {
                        if (job.milestones.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                job.milestones.forEach { milestone ->
                                    TimelineMilestoneRow(milestone = milestone, tokens = tokens, brandingColor = branding.getPrimaryColor(tokens.isDark))
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TimelineMilestoneRow(
                                    milestone = TimelineMilestone(
                                        eventName = "Application Submission Window",
                                        dateString = if (job.applicationClosingDate.isNotBlank()) "${job.applicationStartDate} to ${job.applicationClosingDate}" else "Not announced"
                                    ),
                                    tokens = tokens,
                                    brandingColor = branding.getPrimaryColor(tokens.isDark)
                                )
                                TimelineMilestoneRow(
                                    milestone = TimelineMilestone(
                                        eventName = "Examination Schedule",
                                        dateString = job.examDate.ifBlank { "Not announced" },
                                        isCrucial = true
                                    ),
                                    tokens = tokens,
                                    brandingColor = branding.getPrimaryColor(tokens.isDark)
                                )
                            }
                        }
                    }

                    // Job Overview & Role Details Section
                    RecruitmentSection(title = "Job Overview & Role Details", icon = Icons.Default.Info, tokens = tokens) {
                        Text(
                            text = job.jobOverview.ifEmpty { "No official overview provided in the notification. Refer to the official PDF for full details." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textPrimary,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoKeyValRow(label = "Post Level", value = job.level.ifBlank { "Not announced" }, tokens = tokens)
                        InfoKeyValRow(label = "Selection Authority", value = job.organization, tokens = tokens)
                        InfoKeyValRow(
                            label = "Application Window",
                            value = if (job.applicationClosingDate.isNotBlank()) "${job.applicationStartDate} to ${job.applicationClosingDate}" else "Not announced",
                            tokens = tokens
                        )
                        InfoKeyValRow(
                            label = "Application Fee",
                            value = job.applicationFee.ifBlank { "Not available" },
                            tokens = tokens
                        )
                    }

                    // Location & Posting
                    RecruitmentSection(title = "Location & Posting Jurisdiction", icon = Icons.Default.LocationCity, tokens = tokens) {
                        Text(
                            text = job.locationDetails.ifEmpty { "Posting jurisdiction: ${job.location.ifBlank { "All India" }}." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textPrimary,
                            lineHeight = 22.sp
                        )
                    }

                    // Eligibility & Age Limit
                    RecruitmentSection(title = "Eligibility & Age Criteria", icon = Icons.Default.CheckCircleOutline, tokens = tokens) {
                        InfoKeyValRow(label = "Educational Standard", value = job.minQualification.ifBlank { "Not announced" }, tokens = tokens)
                        InfoKeyValRow(label = "Prescribed Age Limit", value = job.ageLimit.ifBlank { "Not announced" }, tokens = tokens)
                        InfoKeyValRow(label = "Citizenship", value = "Citizen of India / Subject of Nepal / Bhutan", tokens = tokens)
                    }

                    // Selection Process
                    RecruitmentSection(title = "Selection Process", icon = Icons.Default.AccountTree, tokens = tokens) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tokens.surfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = job.selectionStagesSummary.ifBlank { "Not announced in preliminary notification" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = branding.getBadgeText(tokens.isDark),
                                modifier = Modifier.padding(14.dp),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Examination Pattern & Stages
                    RecruitmentSection(title = "Examination Pattern & Stages", icon = Icons.Default.Quiz, tokens = tokens) {
                        if (job.examStages.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                job.examStages.forEach { stage ->
                                    ExamStageCard(stage = stage, tokens = tokens, brandingColor = branding.getPrimaryColor(tokens.isDark))
                                }
                            }
                        } else if (job.examPatternDetails.isNotEmpty()) {
                            Text(
                                text = job.examPatternDetails,
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textPrimary,
                                lineHeight = 22.sp
                            )
                        } else {
                            MissingDataNotice(
                                title = "Examination Pattern",
                                statusText = "Not announced",
                                description = "The scheme of examination and test structure have not yet been released in the official notification."
                            )
                        }
                    }

                    // Syllabus & Preparation Strategy
                    RecruitmentSection(title = "Syllabus & Recommended Preparation", icon = Icons.Default.AutoStories, tokens = tokens) {
                        if (job.syllabusTopics.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                job.syllabusTopics.forEach { topic ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = branding.getPrimaryColor(tokens.isDark),
                                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = topic,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = tokens.textPrimary,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        } else if (job.preparationInfo.isNotEmpty()) {
                            Text(
                                text = job.preparationInfo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textPrimary,
                                lineHeight = 22.sp
                            )
                        } else {
                            MissingDataNotice(
                                title = "Detailed Syllabus",
                                statusText = "Not announced",
                                description = "Topic-wise syllabus breakdown will be uploaded once published by the recruitment board."
                            )
                        }
                    }

                    // Vacancies & Category-wise Reservation
                    RecruitmentSection(title = "Vacancies & Category-wise Reservation", icon = Icons.Default.PieChart, tokens = tokens) {
                        if (job.categoryQuotas.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                job.categoryQuotas.forEach { quota ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                tokens.surfaceElevated,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = quota.categoryName,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = tokens.textPrimary
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${quota.count} Seats",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = branding.getPrimaryColor(tokens.isDark)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${quota.percentage})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = tokens.textTertiary
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = job.seatsAndReservation.ifEmpty { job.quota.ifBlank { "Reservation details not announced." } },
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Cut-Off Benchmarks (CLEARLY DISTINGUISHED: OFFICIAL vs HISTORICAL vs ESTIMATED)
                    RecruitmentSection(title = "Cut-Off Benchmarks & Qualifying Thresholds", icon = Icons.AutoMirrored.Filled.TrendingUp, tokens = tokens) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.surfaceElevated,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "Transparency Notice: Official cutoffs are verified from official recruitment notices. Estimates are projections based on competitive mock data.",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.textSecondary,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 14.sp
                            )
                        }

                        if (job.cutoffBenchmarks.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                job.cutoffBenchmarks.forEach { cutoff ->
                                    CutoffBenchmarkRow(cutoff = cutoff, tokens = tokens)
                                }
                            }
                        } else if (job.cutOffInfo.isNotEmpty()) {
                            Text(
                                text = job.cutOffInfo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textPrimary,
                                lineHeight = 20.sp
                            )
                        } else {
                            MissingDataNotice(
                                title = "Previous Year Cutoffs",
                                statusText = "Not available",
                                description = "Historical benchmark scores are not available in the current dataset for this recruitment."
                            )
                        }
                    }

                    // Additional Information / Conditions
                    RecruitmentSection(title = "Special Service Conditions & Medicals", icon = Icons.Default.MedicalInformation, tokens = tokens) {
                        Text(
                            text = job.otherInfo.ifEmpty { "No additional service conditions or medical guidelines specified in this notice." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textPrimary,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun RecruitmentSection(
    title: String,
    icon: ImageVector,
    tokens: AppThemeTokens,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = tokens.surface,
        border = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            tokens.borderSubtle
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tokens.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary
                )
            }
            content()
        }
    }
}

@Composable
private fun HeroDetailChip(
    icon: ImageVector,
    title: String,
    value: String,
    tokens: AppThemeTokens,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = tokens.surfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            tokens.borderSubtle
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tokens.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.textTertiary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun InfoKeyValRow(label: String, value: String, tokens: AppThemeTokens) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = tokens.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = tokens.textPrimary
        )
    }
}

@Composable
private fun TimelineMilestoneRow(
    milestone: TimelineMilestone,
    tokens: AppThemeTokens,
    brandingColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                tokens.surfaceElevated,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    if (milestone.isCrucial) tokens.danger else if (milestone.isPassed) tokens.success else brandingColor,
                    CircleShape
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = milestone.eventName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (milestone.isCrucial) FontWeight.Bold else FontWeight.Normal,
            color = tokens.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = milestone.dateString,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (milestone.isCrucial) tokens.danger else brandingColor
        )
    }
}

@Composable
private fun ExamStageCard(stage: ExamStageInfo, tokens: AppThemeTokens, brandingColor: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = tokens.surfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            tokens.borderSubtle
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stage.stageName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = brandingColor
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = brandingColor.copy(alpha = if (tokens.isDark) 0.20f else 0.12f)
                ) {
                    Text(
                        text = stage.duration,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = brandingColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Mode: ${stage.mode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )
                if (stage.questions > 0) {
                    Text(
                        text = "${stage.questions} Qs • ${stage.marks} Marks",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                }
            }

            Text(
                text = "Negative Marking: ${stage.negativeMarking}",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textTertiary
            )

            if (stage.subjects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = tokens.borderSubtle)
                Spacer(modifier = Modifier.height(8.dp))
                stage.subjects.forEach { subject ->
                    Text(
                        text = "• $subject",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CutoffBenchmarkRow(cutoff: CutoffEntry, tokens: AppThemeTokens) {
    val typeBadgeColor = Color(cutoff.type.indicatorColor)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                tokens.surfaceElevated,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = cutoff.category,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = typeBadgeColor.copy(alpha = if (tokens.isDark) 0.20f else 0.12f)
                ) {
                    Text(
                        text = cutoff.type.label.uppercase(),
                        color = typeBadgeColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = cutoff.yearOrShift,
                style = MaterialTheme.typography.labelSmall,
                color = tokens.textTertiary
            )
        }

        Text(
            text = cutoff.score,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = tokens.textPrimary
        )
    }
}

@Composable
private fun MissingDataNotice(
    title: String,
    statusText: String,
    description: String
) {
    val tokens = LocalAppThemeTokens.current
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = tokens.surfaceElevated,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, tokens.borderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (statusText.contains("announced", ignoreCase = true)) tokens.accent.copy(alpha = 0.15f) else tokens.textSecondary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = statusText.uppercase(),
                    color = if (statusText.contains("announced", ignoreCase = true)) tokens.accent else tokens.textSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
