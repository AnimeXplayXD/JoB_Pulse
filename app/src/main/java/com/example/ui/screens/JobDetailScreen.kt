package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.model.*
import com.example.ui.components.RecruitmentStatusBadge
import com.example.ui.components.getEffectiveStatus
import com.example.ui.theme.LocalAppThemeTokens
import com.example.ui.theme.OrgBrandingRegistry
import com.example.util.openJobLink

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    job: Job, onBack: () -> Unit, isBookmarked: Boolean, onBookmarkToggle: () -> Unit,
    sharedTransitionScope: SharedTransitionScope, animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val tokens = LocalAppThemeTokens.current
    val context = LocalContext.current
    val branding = remember(job.organization, job.title) { OrgBrandingRegistry.forJob(job) }
    val listState = rememberLazyListState()
    with(sharedTransitionScope) {
        Scaffold(
            modifier = modifier.fillMaxSize().sharedBounds(
                rememberSharedContentState("job_card_bounds_${job.id}"), animatedVisibilityScope,
                boundsTransform = { _, _ -> spring(dampingRatio = 1f, stiffness = 600f) }
            ).testTag("job_detail_screen"),
            containerColor = tokens.background,
            topBar = {
                TopAppBar(
                    title = { Text("Job details") },
                    navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to jobs") } },
                    actions = { IconButton(onBookmarkToggle) { Icon(if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder, if (isBookmarked) "Remove Bookmark" else "Bookmark Job") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = tokens.background)
                )
            },
            bottomBar = {
                Surface(color = tokens.surface) {
                    Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
                        Button(
                            { openJobLink(context, job.applyUrl) }, Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            enabled = !job.applyUrl.isNullOrBlank() && job.getEffectiveStatus() != RecruitmentStatus.APPLICATION_CLOSED
                        ) { Text("Open application website") }
                        TextButton({ openJobLink(context, job.noticeUrl) }, Modifier.fillMaxWidth(), enabled = job.noticeUrl.isNotBlank()) { Text("Read source notice") }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                state = listState, modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item(key = "hero") {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.sharedElement(rememberSharedContentState("job_seal_${job.id}"), animatedVisibilityScope)
                                    .size(52.dp).clip(RoundedCornerShape(14.dp)).background(tokens.surfaceSubtle),
                                contentAlignment = Alignment.Center
                            ) { Image(painterResource(branding.logoResId), null, Modifier.size(32.dp)) }
                            Spacer(Modifier.width(12.dp))
                            Text(job.organization, Modifier.weight(1f), color = tokens.textSecondary, style = MaterialTheme.typography.bodyLarge)
                        }
                        Text(
                            job.title,
                            Modifier.sharedBounds(
                                rememberSharedContentState("job_title_${job.id}"), animatedVisibilityScope,
                                boundsTransform = { _, _ -> spring(dampingRatio = 1f, stiffness = 600f) },
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                            ), color = tokens.textPrimary, style = MaterialTheme.typography.headlineLarge
                        )
                        RecruitmentStatusBadge(job.getEffectiveStatus(), tokens)
                        DetailField("Vacancies", if (job.seats > 0) job.seats.toString() else "Not announced")
                        DetailField("Location", job.location)
                        DetailField("Pay", job.salary)
                        DetailField("Employment type", job.employmentType)
                    }
                }
                item(key = "source") {
                    DetailSection("Source and freshness") {
                        Text(if (BuildConfig.DEBUG || job.sourceName == "Development sample") "Development sample. Do not use this listing to make application decisions." else "Confirm the latest notice with the recruiting authority before applying.", color = tokens.textSecondary)
                        DetailField("Source", job.sourceName)
                        DetailField("Classification", if (job.isOfficialSource) "Marked official by the data provider" else "Not confirmed as an official source")
                        DetailField("Published", job.publishedAt)
                        DetailField("Updated", job.lastUpdatedAt)
                        DetailField("Provider-reported check", job.lastVerifiedAt ?: "No verification recorded")
                        Text("A timestamp or source label is not independent verification. Corrections and extensions may not yet appear in cached data.", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                        TextButton({ openJobLink(context, job.officialSiteUrl) }, enabled = job.officialSiteUrl.isNotBlank()) { Text("Visit source website") }
                        TextButton({
                            val report = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "JobPulse correction: ${job.title}")
                                putExtra(Intent.EXTRA_TEXT, "Please check this listing:\n${job.title}\n${job.organization}\nSource: ${job.noticeUrl}\n\nIncorrect field:\nCorrect information and supporting source:\n")
                            }
                            try { context.startActivity(Intent.createChooser(report, "Share a correction report")) }
                            catch (_: ActivityNotFoundException) { Toast.makeText(context, "No sharing application is available.", Toast.LENGTH_SHORT).show() }
                        }) { Text("Share a correction report") }
                    }
                }
                item(key = "overview") { DetailSection("Overview") {
                    DetailField("Role", job.jobOverview.ifBlank { job.description.orEmpty() })
                    DetailField("Post level", job.level)
                    DetailField("Posting details", job.locationDetails)
                } }
                item(key = "eligibility") { DetailSection("Eligibility") {
                    DetailField("Qualification", job.minQualification)
                    DetailField("Age", job.ageLimit)
                    DetailField("Age relaxation", job.ageRelaxation)
                    Text("Check the source notice for the age reference date, experience, domicile and category-specific conditions. This app does not confirm personal eligibility.", color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall)
                } }
                item(key = "dates") { DetailSection("Application and dates") {
                    DetailField("Applications start", job.applicationStartDate)
                    DetailField("Deadline", job.applicationClosingDate)
                    DetailField("Fee and exemptions", job.applicationFee)
                    DetailField("Exam", job.examDate)
                    DetailField("Admit card", job.admitCardDate)
                    DetailField("Results", job.resultDate)
                    job.milestones.forEach { DetailField(it.eventName, it.dateString) }
                    Text("Check the latest notice for closing time and timezone, required documents and any correction window.", color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall)
                } }
                item(key = "selection") { DetailSection("Selection and exam pattern") {
                    DetailField("Selection process", job.selectionStagesSummary)
                    DetailField("Exam mode", job.examMode)
                    DetailField("Exam location", job.examLocation)
                    DetailField("Pattern", job.examPatternDetails.ifBlank { job.recruitmentExamDetails })
                    job.examStages.forEach { stage ->
                        DetailField(stage.stageName, "${stage.mode} · ${stage.duration}")
                        DetailField("Questions and marks", "${stage.questions} questions · ${stage.marks} marks")
                        DetailField("Negative marking", stage.negativeMarking)
                        stage.subjects.forEach { Text(it, color = tokens.textPrimary) }
                    }
                } }
                item(key = "preparation") { DetailSection("Preparation") {
                    if (job.syllabusTopics.isEmpty()) DetailField("Syllabus", "Not available in this listing")
                    else job.syllabusTopics.forEach { Text("• $it", color = tokens.textPrimary) }
                    Text("Planning suggestions—not official exam requirements", style = MaterialTheme.typography.titleSmall, color = tokens.textPrimary)
                    Text("Use the latest official syllabus as a checklist. Plan study and revision around the announced stages. Use previous papers only when their exam and year are identified. Keep time for practice and application documents.", color = tokens.textSecondary)
                    if (job.preparationInfo.isNotBlank()) DetailField("Additional guidance from the data provider", job.preparationInfo)
                } }
                item(key = "reservation") { DetailSection("Vacancies and reservation") {
                    DetailField("Reservation", job.seatsAndReservation.ifBlank { job.quota })
                    job.categoryQuotas.forEach { DetailField(it.categoryName, "${it.count} vacancies · ${it.percentage}") }
                } }
                item(key = "cutoffs") { DetailSection("Cutoffs and benchmarks") {
                    Text("Historical scores and estimates do not predict this recruitment’s cutoff. Check each source and year.", color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall)
                    if (job.cutoffBenchmarks.isEmpty()) DetailField("Available information", job.cutOffInfo)
                    job.cutoffBenchmarks.forEach { DetailField("${it.category} · ${it.yearOrShift}", "${it.score} · ${it.type.label}") }
                } }
                item(key = "conditions") { DetailSection("Other conditions") { DetailField("Service and medical conditions", job.otherInfo) } }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val tokens = LocalAppThemeTokens.current
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(tokens.cardRadius), color = tokens.surface) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = tokens.textPrimary)
            content()
        }
    }
}

@Composable
private fun DetailField(label: String, value: String?) {
    val tokens = LocalAppThemeTokens.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = tokens.textSecondary)
        Text(value?.takeIf { it.isNotBlank() } ?: "Not announced", style = MaterialTheme.typography.bodyMedium, color = tokens.textPrimary)
    }
}
