package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_ID_ALERTS = "recruitment_alerts"
    const val CHANNEL_NAME_ALERTS = "JobPulse Recruitment Alerts"
    const val CHANNEL_DESC_ALERTS = "Recruitment notices and deadline reminders"
    const val CHANNEL_ID_MILESTONES = "exam_milestones"
    const val CHANNEL_NAME_MILESTONES = "JobPulse Exam Dates & Milestones"
    const val CHANNEL_DESC_MILESTONES = "Exam dates and recruitment milestones"
    private fun prefs(context: Context) = context.getSharedPreferences("jobpulse_notification_prefs", Context.MODE_PRIVATE)

    fun setupNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            manager.createNotificationChannels(listOf(
                NotificationChannel(CHANNEL_ID_ALERTS, CHANNEL_NAME_ALERTS, NotificationManager.IMPORTANCE_HIGH).apply { description = CHANNEL_DESC_ALERTS },
                NotificationChannel(CHANNEL_ID_MILESTONES, CHANNEL_NAME_MILESTONES, NotificationManager.IMPORTANCE_DEFAULT).apply { description = CHANNEL_DESC_MILESTONES }
            ))
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        val runtimeGranted = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        return runtimeGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun canPostToChannel(context: Context, channelId: String): Boolean {
        if (!hasNotificationPermission(context)) return false
        if (Build.VERSION.SDK_INT < 26) return true
        val manager = context.getSystemService(NotificationManager::class.java) ?: return false
        val channel = manager.getNotificationChannel(channelId) ?: return false
        if (channel.importance == NotificationManager.IMPORTANCE_NONE) return false
        if (Build.VERSION.SDK_INT >= 28 && channel.group != null && manager.getNotificationChannelGroup(channel.group)?.isBlocked == true) return false
        return true
    }

    // Retained for callers/tests; this is explicitly a test, never a subscription claim.
    fun postSubscriptionConfirmedNotification(context: Context) {
        setupNotificationChannels(context)
        if (!canPostToChannel(context, CHANNEL_ID_MILESTONES)) return
        val intent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MILESTONES)
            .setSmallIcon(R.drawable.ic_jobpulse_notification)
            .setContentTitle("JobPulse notification test")
            .setContentText("Notifications are permitted. This does not activate an alert subscription.")
            .setContentIntent(intent).setAutoCancel(true).build()
        try { NotificationManagerCompat.from(context).notify(1001, notification) }
        catch (_: SecurityException) { /* Permission may be revoked between check and delivery. */ }
    }

    fun shouldShowFirstOpenPrompt(context: Context): Boolean = !hasNotificationPermission(context) && !prefs(context).getBoolean("first_open_notification_prompted", false)
    fun markFirstOpenPromptShown(context: Context) { prefs(context).edit().putBoolean("first_open_notification_prompted", true).apply() }
    fun resetFirstOpenPromptForTesting(context: Context) { prefs(context).edit().remove("first_open_notification_prompted").remove("permission_requested").apply() }
    fun wasPermissionRequested(context: Context): Boolean = prefs(context).getBoolean("permission_requested", false)
    fun markPermissionRequested(context: Context) { prefs(context).edit().putBoolean("permission_requested", true).apply() }

    fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= 26) Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
            else Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:${context.packageName}"))
        try { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        catch (_: android.content.ActivityNotFoundException) {
            android.widget.Toast.makeText(context, "Open notification settings from Android Settings.", android.widget.Toast.LENGTH_LONG).show()
        }
    }
}
