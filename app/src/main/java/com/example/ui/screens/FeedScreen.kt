package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.model.DummyFeedItems
import com.example.model.FeedItem
import com.example.ui.theme.LocalAppThemeTokens
import com.example.util.openJobLink

@Composable
fun FeedScreen(listState: LazyListState, modifier: Modifier = Modifier) {
    var selectedTag by rememberSaveable { mutableStateOf("All") }
    val tags = remember { listOf("All", "Admit Card", "Exam Date", "Notice", "Result", "Answer Key") }
    val context = LocalContext.current
    val tokens = LocalAppThemeTokens.current
    val feed = remember { if (BuildConfig.DEBUG) DummyFeedItems else emptyList() }
    val filtered = remember(feed, selectedTag) { if (selectedTag == "All") feed else feed.filter { it.tag.equals(selectedTag, true) } }
    Column(modifier.fillMaxSize().testTag("feed_screen")) {
        Text(
            if (BuildConfig.DEBUG) "Sample announcements · not a live feed" else "Recruitment updates",
            Modifier.padding(20.dp), color = tokens.textSecondary, style = MaterialTheme.typography.bodyMedium
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tags, key = { it }) { tag ->
                FilterChip(selectedTag == tag, { selectedTag = tag }, { Text(tag) }, border = null, shape = RoundedCornerShape(50))
            }
        }
        LazyColumn(
            state = listState, modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = com.example.ui.components.LocalDockContentPadding.current),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filtered.isEmpty()) item { Text("No announcements available yet.", Modifier.padding(20.dp), color = tokens.textSecondary) }
            items(filtered, key = { it.id }, contentType = { "feed_card" }) { item ->
                FeedCard(item) { openJobLink(context, item.noticeUrl) }
            }
        }
    }
}

@Composable
fun FeedCard(item: FeedItem, onOpenNotice: () -> Unit) {
    val tokens = LocalAppThemeTokens.current
    Surface(shape = RoundedCornerShape(tokens.cardRadius), color = tokens.surface, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("${item.tag} · ${item.timeAgo}", color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall)
            Text(item.title, color = tokens.textPrimary, style = MaterialTheme.typography.titleLarge)
            Text(item.organization, color = tokens.textSecondary, style = MaterialTheme.typography.bodyMedium)
            Text(item.summary, color = tokens.textPrimary, style = MaterialTheme.typography.bodyMedium)
            TextButton(onOpenNotice, enabled = item.noticeUrl.isNotBlank()) { Text("Read announcement") }
        }
    }
}
