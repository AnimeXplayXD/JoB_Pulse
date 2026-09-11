package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.model.Job
import com.example.model.getThemeInfoForCategory

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
    val themeInfo = getThemeInfoForCategory(job.category)

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Job Details") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = onBookmarkToggle) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val url = job.noticeUrl.ifEmpty { job.officialSiteUrl }
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Notice")
                        }
                        
                        Button(
                            onClick = {
                                val url = job.applyUrl ?: job.officialSiteUrl
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Apply Now")
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ) {
                // Shared Hero Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sharedBounds(
                            rememberSharedContentState(key = "job_card_bounds_${job.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ -> tween(400) }
                        )
                        .background(themeInfo.gradient)
                        .padding(24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = themeInfo.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = job.organization.uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = job.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.sharedElement(
                                        rememberSharedContentState(key = "job_title_${job.id}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailChip(icon = Icons.Default.Work, text = job.level)
                            DetailChip(icon = Icons.Default.LocationOn, text = job.location)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailChip(icon = Icons.Default.Paid, text = job.salary)
                            DetailChip(icon = Icons.Default.People, text = "${job.seats} Seats")
                        }
                    }
                }

                // Details Content
                Column(modifier = Modifier.padding(20.dp)) {
                    if (job.jobOverview.isNotEmpty()) {
                        DetailSection("Overview", job.jobOverview)
                    }
                    if (job.locationDetails.isNotEmpty()) {
                        DetailSection("Location & Posting", job.locationDetails)
                    }
                    if (job.recruitmentExamDetails.isNotEmpty()) {
                        DetailSection("Recruitment Timeline", job.recruitmentExamDetails)
                    }
                    if (job.examPatternDetails.isNotEmpty()) {
                        DetailSection("Exam Pattern", job.examPatternDetails)
                    }
                    if (job.preparationInfo.isNotEmpty()) {
                        DetailSection("Preparation Strategy", job.preparationInfo)
                    }
                    if (job.seatsAndReservation.isNotEmpty()) {
                        DetailSection("Seats & Reservation", job.seatsAndReservation)
                    }
                    if (job.cutOffInfo.isNotEmpty()) {
                        DetailSection("Previous Cut-offs", job.cutOffInfo)
                    }
                    if (job.otherInfo.isNotEmpty()) {
                        DetailSection("Additional Info", job.otherInfo)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun DetailChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DetailSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
        )
    }
}
