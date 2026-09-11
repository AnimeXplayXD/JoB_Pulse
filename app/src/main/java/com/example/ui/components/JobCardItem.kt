package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.example.model.getThemeInfoForCategory

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
    val themeInfo = getThemeInfoForCategory(job.category)

    val cardModifier = modifier
        .fillMaxWidth()
        .animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { expanded = !expanded },
                onDoubleTap = { onDoubleTap() }
            )
        }
        .testTag("job_card_${job.id}")

    val sharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            cardModifier.sharedBounds(
                rememberSharedContentState(key = "job_card_bounds_${job.id}"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ -> tween(400) }
            )
        }
    } else cardModifier

    Card(
        modifier = sharedModifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeInfo.gradient)
                // Glassmorphism subtle highlight border
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Category Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = themeInfo.icon,
                            contentDescription = job.category.displayName,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Title & Sector
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = job.organization.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
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
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = if (expanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = titleModifier
                        )
                    }

                    // Bookmark Action
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(icon = Icons.Default.Work, text = job.level)
                    InfoItem(icon = Icons.Default.LocationOn, text = job.location)
                }

                Spacer(modifier = Modifier.height(10.dp))
                InfoItem(icon = Icons.Default.Paid, text = job.salary)

                Spacer(modifier = Modifier.height(10.dp))
                InfoItem(icon = Icons.Default.People, text = "${job.seats} Total Seats Available")

                // Expanded Details
                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.25f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Quota & Reservation Breakdown",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = job.quota,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Eligibility Criteria",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Minimum qualification: ${job.minQualification}. All eligible Indian citizens can apply.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Buttons: Apply / Official Website + Notice
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (job.applyUrl != null) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, job.applyUrl.toUri())
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = themeInfo.primaryColor
                                    )
                                ) {
                                    Text("Apply Now", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, job.officialSiteUrl.toUri())
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = themeInfo.primaryColor
                                    )
                                ) {
                                    Text("Official Site", fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, job.noticeUrl.toUri())
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                            ) {
                                Text("Notice")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.95f)
        )
    }
}

@Composable
fun JobCardSkeleton() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_float"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )
    }
}
