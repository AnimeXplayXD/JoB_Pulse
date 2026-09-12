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
    const val CHANNEL_DESC_ALERTS = "Notifications for newly announced vacancies and application deadlines"

    const val CHANNEL_ID_MILESTONES = "exam_milestones"
    const val CHANNEL_NAME_MILESTONES = "JobPulse Exam Dates & Milestones"
    const val CHANNEL_DESC_MILESTONES = "Timely updates on admit cards, examination schedules, and results"

    private const val NOTIFICATION_ID_CONFIRMATION = 1001

    /**
     * Initializes notification channels required on Android 8.0+ (API 26+).
     */
    fun setupNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // High priority channel for critical alerts
            val alertsChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                CHANNEL_NAME_ALERTS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_ALERTS
                enableLights(true)
                enableVibration(true)
            }

            // Default priority channel for exam date / result milestones
            val milestonesChannel = NotificationChannel(
                CHANNEL_ID_MILESTONES,
                CHANNEL_NAME_MILESTONES,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC_MILESTONES
            }

            notificationManager.createNotificationChannel(alertsChannel)
            notificationManager.createNotificationChannel(milestonesChannel)
        }
    }

    /**
     * Checks if notification permission is granted on the device.
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Posts a notification confirming that job alert subscriptions are active.
     */
    fun postSubscriptionConfirmedNotification(context: Context) {
        if (!hasNotificationPermission(context)) return
        setupNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_jobpulse_notification)
            .setContentTitle("JobPulse • Live Alerts Activated")
            .setContentText("You will receive timely alerts for verified Central & State vacancies.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("JobPulse alert subscription active. You will receive notifications when new recruitment notices match your preferences.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CONFIRMATION, notification)
        } catch (e: SecurityException) {
            // Permission revoked concurrently
        }
    }

    private const val PREFS_NAME = "jobpulse_notification_prefs"
    private const val KEY_FIRST_OPEN_PROMPTED = "first_open_notification_prompted"

    /**
     * Determines whether the first-open notification permission popup should be displayed.
     * Returns true ONLY if:
     * 1. Notification permission has NOT been granted earlier.
     * 2. The user has not already been prompted on a first open.
     */
    fun shouldShowFirstOpenPrompt(context: Context): Boolean {
        if (hasNotificationPermission(context)) {
            return false
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getBoolean(KEY_FIRST_OPEN_PROMPTED, false)
    }

    /**
     * Records that the first-open notification prompt has been presented and handled.
     */
    fun markFirstOpenPromptShown(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_OPEN_PROMPTED, true).apply()
    }

    /**
     * Resets the first-open prompt state for testing purposes.
     */
    fun resetFirstOpenPromptForTesting(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_FIRST_OPEN_PROMPTED).apply()
    }

    /**
     * Directs the user to the system notification settings for this application.
     */
    fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", context.packageName, null)
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback gracefully
        }
    }
}
