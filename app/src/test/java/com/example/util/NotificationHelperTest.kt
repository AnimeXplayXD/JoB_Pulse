package com.example.util

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NotificationHelperTest {

    @Test
    fun setupNotificationChannels_createsRequiredChannels() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotificationHelper.setupNotificationChannels(context)

        val alertsChannel = notificationManager.getNotificationChannel(NotificationHelper.CHANNEL_ID_ALERTS)
        assertNotNull("Recruitment alerts channel must be created", alertsChannel)
        assertEquals(NotificationHelper.CHANNEL_NAME_ALERTS, alertsChannel.name)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, alertsChannel.importance)

        val milestonesChannel = notificationManager.getNotificationChannel(NotificationHelper.CHANNEL_ID_MILESTONES)
        assertNotNull("Exam milestones channel must be created", milestonesChannel)
        assertEquals(NotificationHelper.CHANNEL_NAME_MILESTONES, milestonesChannel.name)
        assertEquals(NotificationManager.IMPORTANCE_DEFAULT, milestonesChannel.importance)
    }

    @Test
    fun hasNotificationPermission_returnsBooleanWithoutCrashing() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // In Robolectric context, checking permission should evaluate without throwing
        val hasPermission = NotificationHelper.hasNotificationPermission(context)
        // Should evaluate to true or false depending on test manifest/shadow
        assertNotNull(hasPermission)
    }

    @Test
    fun shouldShowFirstOpenPrompt_andMarkFirstOpenPromptShown() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        NotificationHelper.resetFirstOpenPromptForTesting(context)

        val initialShouldShow = NotificationHelper.shouldShowFirstOpenPrompt(context)
        val hasPerm = NotificationHelper.hasNotificationPermission(context)

        if (!hasPerm) {
            assertTrue("Should prompt on first open when permission is not granted", initialShouldShow)
            NotificationHelper.markFirstOpenPromptShown(context)
            assertFalse("Should not prompt after markFirstOpenPromptShown", NotificationHelper.shouldShowFirstOpenPrompt(context))
        } else {
            assertFalse("Should not prompt if permission is already granted", initialShouldShow)
        }
    }

    @Test
    fun openNotificationSettings_doesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Should execute smoothly without throwing exceptions
        NotificationHelper.openNotificationSettings(context)
    }

    @Test
    fun postSubscriptionConfirmedNotification_doesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        NotificationHelper.postSubscriptionConfirmedNotification(context)
    }
}
