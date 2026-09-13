package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.AndroidUserPreferences
import com.example.theme.LocalThemeRevealController
import com.example.ui.theme.LocalAppThemeTokens
import com.example.util.NotificationHelper

@Composable
fun AccountScreen(
    listState: LazyListState,
    bookmarkedCount: Int,
    onViewBookmarks: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferences = remember(context) { AndroidUserPreferences(context) }
    val tokens = LocalAppThemeTokens.current
    val reveal = LocalThemeRevealController.current
    var themeCenter by remember { mutableStateOf<Offset?>(null) }
    LazyColumn(
        state = listState, modifier = modifier.fillMaxSize().testTag("account_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = com.example.ui.components.LocalDockContentPadding.current),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PreferenceSection("On this device") {
                Text("Your saved jobs and preferences stay on this device. No account is connected.", color = tokens.textSecondary)
                TextButton(onViewBookmarks) { Text("Saved jobs ($bookmarkedCount)") }
            }
        }
        item {
            PreferenceSection("Appearance") {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Dark appearance", Modifier.weight(1f), color = tokens.textPrimary)
                    Switch(isDarkTheme, { reveal.reveal(themeCenter) }, Modifier.onGloballyPositioned {
                        themeCenter = it.positionInRoot() + Offset(it.size.width / 2f, it.size.height / 2f)
                    })
                }
            }
        }
        item {
            PreferenceSection("Notification interests") {
                Text("These choices are saved locally. Automatic delivery is not connected yet; permission alone does not activate alerts.", color = tokens.textSecondary, style = MaterialTheme.typography.bodySmall)
                listOf("jobs" to "New opportunities", "admit_cards" to "Admit cards", "exam_dates" to "Exam dates").forEach { (key, title) ->
                    var checked by remember(key) { mutableStateOf(preferences.alertPreference(key)) }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(title, Modifier.weight(1f), color = tokens.textPrimary)
                        Switch(checked, { checked = it; preferences.saveAlertPreference(key, it) })
                    }
                }
                TextButton({ NotificationHelper.openNotificationSettings(context) }) { Text("Android notification settings") }
            }
        }
        item {
            PreferenceSection("About recruitment information") {
                Text("JobPulse is an independent service. Always confirm eligibility, deadlines and fees in the recruiting authority’s latest notice before applying.", color = tokens.textSecondary)
            }
        }
    }
}

@Composable
private fun PreferenceSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val tokens = LocalAppThemeTokens.current
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(tokens.cardRadius), color = tokens.surface) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = tokens.textPrimary)
            content()
        }
    }
}
