package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.model.DummyFeedItems
import com.example.model.FeedItem
import com.example.ui.components.LiquidGlassBox
import com.example.ui.theme.AppColors
import com.example.ui.theme.LocalAppThemeTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    var selectedTag by remember { mutableStateOf("All") }
    val tags = listOf("All", "Admit Card", "Exam Date", "Notice", "Result", "Answer Key")
    val context = LocalContext.current
    val tokens = LocalAppThemeTokens.current

    val filteredItems = remember(selectedTag) {
        if (selectedTag == "All") DummyFeedItems
        else DummyFeedItems.filter { it.tag.equals(selectedTag, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("feed_screen")
    ) {
        // Live Updates Banner with restrained Liquid Glass styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            LiquidGlassBox(
                shape = RoundedCornerShape(tokens.cardRadius),
                elevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(tokens.primary.copy(alpha = if (tokens.isDark) 0.22f else 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Live",
                            tint = tokens.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "OFFICIAL RECRUITMENT DISPATCHES",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = tokens.primary,
                                letterSpacing = 0.4.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(tokens.livePulse, CircleShape)
                            )
                        }
                        Text(
                            text = "Authoritative updates from UPSC, SSC, RRB & State Commission portals",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }
                }
            }
        }

        // Tag Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tags, key = { it }) { tag ->
                val isSelected = selectedTag == tag
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTag = tag },
                    label = {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    shape = RoundedCornerShape(tokens.chipRadius),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = tokens.surfaceElevated,
                        labelColor = tokens.textSecondary,
                        selectedContainerColor = tokens.primary,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = tokens.borderSubtle,
                        selectedBorderColor = tokens.primary,
                        borderWidth = 1.dp
                    )
                )
            }
        }

        // Feed Items List
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = filteredItems,
                key = { it.id },
                contentType = { "feed_card" }
            ) { item ->
                FeedCard(item = item, onOpenNotice = {
                    val intent = Intent(Intent.ACTION_VIEW, item.noticeUrl.toUri())
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun FeedCard(
    item: FeedItem,
    onOpenNotice: () -> Unit
) {
    val tokens = LocalAppThemeTokens.current
    val tagColor = when (item.tag) {
        "Exam Date" -> Color(0xFF1976D2)
        "Admit Card" -> Color(0xFF2E7D32)
        "Result" -> Color(0xFFD97706)
        "Answer Key" -> Color(0xFF7B1FA2)
        else -> tokens.primary
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(tokens.cardRadius),
        color = tokens.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            tokens.borderSubtle
        ),
        shadowElevation = if (tokens.isDark) 3.dp else 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Tag Badge & Time Ago
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = tagColor.copy(alpha = if (tokens.isDark) 0.18f else 0.10f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        tagColor.copy(alpha = if (tokens.isDark) 0.35f else 0.22f)
                    )
                ) {
                    Text(
                        text = item.tag.uppercase(),
                        color = tagColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        letterSpacing = 0.4.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = tokens.textTertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.textTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Announcement Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Issuing Organization
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = tokens.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.organization,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Summary
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Official Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onOpenNotice,
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        tokens.border
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = tokens.textPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Official Notice",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
