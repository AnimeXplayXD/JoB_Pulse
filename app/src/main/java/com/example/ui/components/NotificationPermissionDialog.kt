package com.example.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Optional settings explanation for legacy callers; never shown before the system prompt. */
@Composable
fun NotificationPermissionDialog(
    isPermissionGranted: Boolean,
    onEnableClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onSendSampleAlert: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier, onDismissRequest = onDismissRequest,
        title = { Text("Notification settings") },
        text = { Text(if (isPermissionGranted) "Android permits notifications. Automatic alert delivery is not connected yet." else "Notifications are off. You can enable permission in Android settings.") },
        confirmButton = { TextButton(onOpenSettings ?: onEnableClick) { Text("Settings") } },
        dismissButton = { TextButton(onDismissRequest) { Text("Close") } }
    )
}
