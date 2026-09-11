package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.model.*
import com.example.ui.theme.AppColors
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
    val isDark = isSystemInDarkTheme()
    val branding = remember(job.organization, job.title) { OrgBrandingRegistry.forJob(job) }
    val scrollState = rememberScrollState()

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = branding.orgName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "Official Recruitment Bulletin",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Return to Feed",
                                tint = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onBookmarkToggle) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = if (isBookmarked) "Bookmarked" else "Bookmark",
                                tint = if (isBookmarked) branding.primaryColor else if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                            )
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "${job.title} - ${job.organization}")
                                putExtra(Intent.EXTRA_TEXT, "Govt Jobs Alert: ${job.title} (${job.seats} Vacancies) announced by ${job.organization}. Apply at: ${job.applyUrl ?: job.officialSiteUrl}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Job Announcement"))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDark) AppColors.DarkBackground else AppColors.LightBackground,
                        titleContentColor = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    )
                )
            },
            bottomBar = {
                // Sticky Action Surface with direct Application & Gazette PDF triggers
                Surface(
                    color = if (isDark) AppColors.DarkSurface else AppColors.LightSurface,
                    shadowElevation = 16.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
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
                                if (isDark) AppColors.DarkBorder else AppColors.LightBorder
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
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
                            Text("Gazette PDF", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                val url = job.applyUrl ?: job.officialSiteUrl
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = branding.primaryColor,
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
                                tween(durationMillis = 380, easing = FastOutSlowInEasing)
                            }
                        )
                        .background(if (isDark) branding.surfaceGradientDark else branding.surfaceGradientLight)
                        .padding(22.dp)
                ) {
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
                                    .background(if (isDark) branding.badgeSurfaceDark else branding.badgeSurfaceLight)
                                    .border(
                                        1.dp,
                                        if (isDark) branding.borderSpecularTop.copy(alpha = 0.5f) else branding.primaryColor.copy(alpha = 0.2f),
                                        RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = branding.icon,
                                    contentDescription = branding.orgName,
                                    tint = if (isDark) branding.badgeTextDark else branding.primaryColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = branding.orgName.uppercase(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) branding.badgeTextDark else branding.primaryColor,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AppColors.SuccessGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "VERIFIED GAZETTE",
                                            color = AppColors.SuccessGreen,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = branding.authoritySubtext,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Shared Title
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                            lineHeight = 30.sp,
                            modifier = Modifier.sharedElement(
                                rememberSharedContentState(key = "job_title_${job.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
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
                                value = "${job.seats} Posts",
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                            HeroDetailChip(
                                icon = Icons.Default.Payments,
                                title = "PAY MATRIX",
                                value = job.salary,
                                isDark = isDark,
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
                                value = job.location,
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                            HeroDetailChip(
                                icon = Icons.Default.School,
                                title = "QUALIFICATION",
                                value = job.minQualification,
                                isDark = isDark,
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
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    // Timeline Milestones
                    if (job.milestones.isNotEmpty()) {
                        RecruitmentSection(title = "Important Dates & Timeline", icon = Icons.Default.CalendarMonth) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                job.milestones.forEach { milestone ->
                                    TimelineMilestoneRow(milestone = milestone, isDark = isDark, brandingColor = branding.primaryColor)
                                }
                            }
                        }
                    }

                    // Job Overview & Service Cadre
                    RecruitmentSection(title = "Job Overview & Role Details", icon = Icons.Default.Info) {
                        Text(
                            text = job.jobOverview.ifEmpty { "Comprehensive government executive cadre vacancy under ${job.organization}." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        InfoKeyValRow(label = "Post Level", value = job.level, isDark = isDark)
                        InfoKeyValRow(label = "Selection Authority", value = job.organization, isDark = isDark)
                        InfoKeyValRow(label = "Application Window", value = "${job.applicationStartDate} to ${job.applicationClosingDate}", isDark = isDark)
                        InfoKeyValRow(label = "Application Fee", value = job.applicationFee, isDark = isDark)
                    }

                    // Location & Posting
                    if (job.locationDetails.isNotEmpty()) {
                        RecruitmentSection(title = "Location & Posting Jurisdiction", icon = Icons.Default.LocationCity) {
                            Text(
                                text = job.locationDetails,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // Eligibility & Age Limit
                    RecruitmentSection(title = "Eligibility & Age Criteria", icon = Icons.Default.CheckCircleOutline) {
                        InfoKeyValRow(label = "Educational Standard", value = job.minQualification, isDark = isDark)
                        InfoKeyValRow(label = "Prescribed Age Limit", value = job.ageLimit, isDark = isDark)
                        InfoKeyValRow(label = "Citizenship", value = "Citizen of India / Subject of Nepal / Bhutan", isDark = isDark)
                    }

                    // Selection Pipeline
                    RecruitmentSection(title = "Selection Process", icon = Icons.Default.AccountTree) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = job.selectionStagesSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = branding.primaryColor,
                                modifier = Modifier.padding(14.dp),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Detailed Exam Pattern
                    if (job.examStages.isNotEmpty()) {
                        RecruitmentSection(title = "Examination Pattern & Stages", icon = Icons.Default.Quiz) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                job.examStages.forEach { stage ->
                                    ExamStageCard(stage = stage, isDark = isDark, brandingColor = branding.primaryColor)
                                }
                            }
                        }
                    } else if (job.examPatternDetails.isNotEmpty()) {
                        RecruitmentSection(title = "Examination Pattern", icon = Icons.Default.Quiz) {
                            Text(
                                text = job.examPatternDetails,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // Syllabus & Preparation Strategy
                    RecruitmentSection(title = "Syllabus & Recommended Preparation", icon = Icons.Default.AutoStories) {
                        if (job.syllabusTopics.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                job.syllabusTopics.forEach { topic ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = branding.primaryColor,
                                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = topic,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        } else if (job.preparationInfo.isNotEmpty()) {
                            Text(
                                text = job.preparationInfo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // Seats & Quota Breakdown
                    RecruitmentSection(title = "Vacancies & Category-wise Reservation", icon = Icons.Default.PieChart) {
                        if (job.categoryQuotas.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                job.categoryQuotas.forEach { quota ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
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
                                            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${quota.count} Seats",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = branding.primaryColor
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${quota.percentage})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = job.seatsAndReservation.ifEmpty { job.quota },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Cut-off Benchmarks (CLEARLY DISTINGUISHED: OFFICIAL vs HISTORICAL vs ESTIMATED)
                    RecruitmentSection(title = "Cut-Off Benchmarks & Qualifying Thresholds", icon = Icons.AutoMirrored.Filled.TrendingUp) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFF1E1E24) else Color(0xFFF1F3F5),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "Transparency Notice: Official gazetted cutoffs are verified from commissioning notices. Estimates are projections based on competitive mock data.",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 14.sp
                            )
                        }

                        if (job.cutoffBenchmarks.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                job.cutoffBenchmarks.forEach { cutoff ->
                                    CutoffBenchmarkRow(cutoff = cutoff, isDark = isDark)
                                }
                            }
                        } else if (job.cutOffInfo.isNotEmpty()) {
                            Text(
                                text = job.cutOffInfo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Additional Information / Conditions
                    if (job.otherInfo.isNotEmpty()) {
                        RecruitmentSection(title = "Special Service Conditions & Medicals", icon = Icons.Default.MedicalInformation) {
                            Text(
                                text = job.otherInfo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                                lineHeight = 22.sp
                            )
                        }
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
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (isDark) AppColors.DarkSurface else AppColors.LightSurface,
        border = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
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
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) AppColors.DarkSurfaceElevated.copy(alpha = 0.85f) else AppColors.LightSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
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
                tint = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun InfoKeyValRow(label: String, value: String, isDark: Boolean) {
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
            color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
        )
    }
}

@Composable
private fun TimelineMilestoneRow(
    milestone: TimelineMilestone,
    isDark: Boolean,
    brandingColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    if (milestone.isCrucial) AppColors.DangerRed else if (milestone.isPassed) AppColors.SuccessGreen else brandingColor,
                    CircleShape
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = milestone.eventName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (milestone.isCrucial) FontWeight.Bold else FontWeight.Normal,
            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = milestone.dateString,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (milestone.isCrucial) AppColors.DangerRed else brandingColor
        )
    }
}

@Composable
private fun ExamStageCard(stage: ExamStageInfo, isDark: Boolean, brandingColor: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle
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
                    color = brandingColor.copy(alpha = 0.15f)
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
                    color = if (isDark) AppColors.DarkTextSecondary else AppColors.LightTextSecondary
                )
                if (stage.questions > 0) {
                    Text(
                        text = "${stage.questions} Qs • ${stage.marks} Marks",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    )
                }
            }

            Text(
                text = "Negative Marking: ${stage.negativeMarking}",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
            )

            if (stage.subjects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = if (isDark) AppColors.DarkBorderSubtle else AppColors.LightBorderSubtle)
                Spacer(modifier = Modifier.height(8.dp))
                stage.subjects.forEach { subject ->
                    Text(
                        text = "• $subject",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CutoffBenchmarkRow(cutoff: CutoffEntry, isDark: Boolean) {
    val typeBadgeColor = Color(cutoff.type.indicatorColor)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isDark) AppColors.DarkSurfaceElevated else AppColors.LightSurfaceElevated,
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
                    color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = typeBadgeColor.copy(alpha = 0.15f)
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
                color = if (isDark) AppColors.DarkTextTertiary else AppColors.LightTextTertiary
            )
        }

        Text(
            text = cutoff.score,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDark) AppColors.DarkTextPrimary else AppColors.LightTextPrimary
        )
    }
}
