package com.example

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.example.util.NotificationHelper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BrandIdentityTest {

    @Test
    fun appName_isJobPulse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("JobPulse", appName)
        assertFalse("App name must not contain legacy underscores", appName.contains("_"))
    }

    @Test
    fun notificationChannels_reflectJobPulseBranding() {
        assertTrue(
            "Alerts channel name must contain JobPulse",
            NotificationHelper.CHANNEL_NAME_ALERTS.startsWith("JobPulse")
        )
        assertTrue(
            "Milestones channel name must contain JobPulse",
            NotificationHelper.CHANNEL_NAME_MILESTONES.startsWith("JobPulse")
        )
    }

    @Test
    fun brandVectorDrawables_loadSuccessfully() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val symbolDrawable = ContextCompat.getDrawable(context, R.drawable.ic_jobpulse_symbol)
        assertNotNull("ic_jobpulse_symbol must resolve and load", symbolDrawable)

        val notifDrawable = ContextCompat.getDrawable(context, R.drawable.ic_jobpulse_notification)
        assertNotNull("ic_jobpulse_notification must resolve and load", notifDrawable)

        val launcherBg = ContextCompat.getDrawable(context, R.drawable.ic_launcher_background)
        assertNotNull("ic_launcher_background must resolve and load", launcherBg)

        val launcherFg = ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)
        assertNotNull("ic_launcher_foreground must resolve and load", launcherFg)

        val launcherMono = ContextCompat.getDrawable(context, R.drawable.ic_launcher_monochrome)
        assertNotNull("ic_launcher_monochrome must resolve and load", launcherMono)
    }

    @Test
    fun adaptiveLauncherIcons_resolveSuccessfully() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val launcherIcon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher)
        assertNotNull("R.mipmap.ic_launcher must resolve and load", launcherIcon)

        val launcherRoundIcon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)
        assertNotNull("R.mipmap.ic_launcher_round must resolve and load", launcherRoundIcon)
    }

    @Test
    fun monochromeIcon_hasPureDynamicColorGeometryWithoutHardcodedBrandTints() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val resources = context.resources
        val parser = resources.getXml(R.drawable.ic_launcher_monochrome)

        var eventType = parser.eventType
        while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
            if (eventType == org.xmlpull.v1.XmlPullParser.START_TAG && parser.name == "path") {
                for (i in 0 until parser.attributeCount) {
                    val attrName = parser.getAttributeName(i)
                    val attrVal = parser.getAttributeValue(i)
                    if (attrName == "fillColor" || attrName == "strokeColor") {
                        // Monochrome must only use pure white (#FFFFFF or #FFFFFFFF) so system can dynamically tint
                        assertTrue(
                            "Monochrome path attribute $attrName ($attrVal) must be pure white for dynamic theming",
                            attrVal.equals("#FFFFFF", ignoreCase = true) ||
                                attrVal.equals("#FFFFFFFF", ignoreCase = true) ||
                                attrVal.contains("white", ignoreCase = true)
                        )
                    }
                }
            }
            eventType = parser.next()
        }
    }
}
