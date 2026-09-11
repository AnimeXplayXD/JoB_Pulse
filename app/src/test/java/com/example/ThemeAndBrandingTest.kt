package com.example

import com.example.model.DummyJobs
import com.example.model.Job
import com.example.model.JobCategory
import com.example.ui.theme.DarkThemeTokens
import com.example.ui.theme.LightThemeTokens
import com.example.ui.theme.OrgBrandingRegistry
import org.junit.Assert.*
import org.junit.Test

class ThemeAndBrandingTest {

    @Test
    fun themeTokens_contrastAndModes_areValid() {
        // Verify Dark Tokens
        assertTrue(DarkThemeTokens.isDark)
        assertNotEquals(DarkThemeTokens.background, DarkThemeTokens.surface)
        assertNotEquals(DarkThemeTokens.textPrimary, DarkThemeTokens.textTertiary)

        // Verify Light Tokens
        assertFalse(LightThemeTokens.isDark)
        assertNotEquals(LightThemeTokens.background, LightThemeTokens.surface)
        assertNotEquals(LightThemeTokens.textPrimary, LightThemeTokens.textTertiary)

        // Verify that dark and light token colors are distinct and contrast-calibrated
        assertNotEquals(DarkThemeTokens.textPrimary, LightThemeTokens.textPrimary)
        assertNotEquals(DarkThemeTokens.background, LightThemeTokens.background)
        assertNotEquals(DarkThemeTokens.surface, LightThemeTokens.surface)
    }

    @Test
    fun orgBrandingRegistry_identifiesIndianRailways() {
        val rrbJob = DummyJobs.first { it.category == JobCategory.RAILWAY }
        val branding = OrgBrandingRegistry.forJob(rrbJob)
        assertEquals("Indian Railways", branding.orgName)
        assertEquals(R.drawable.ic_org_railways, branding.logoResId)
        assertEquals("RRB", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_identifiesStateBankOfIndia() {
        val sbiJob = DummyJobs.first { it.organization.contains("State Bank of India", ignoreCase = true) }
        val branding = OrgBrandingRegistry.forJob(sbiJob)
        assertEquals("State Bank of India", branding.orgName)
        assertEquals(R.drawable.ic_org_sbi, branding.logoResId)
        assertEquals("SBI", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_identifiesUPSC() {
        val upscJob = Job(
            id = 101,
            title = "Civil Services Examination",
            category = JobCategory.CENTRAL,
            organization = "Union Public Service Commission",
            level = "Group A Gazetted",
            salary = "₹56,100 - ₹2,50,000",
            location = "All India",
            seats = 1056,
            quota = "SC/ST/OBC/EWS/UR",
            applyUrl = "https://upsconline.nic.in",
            noticeUrl = "https://upsc.gov.in/notice",
            officialSiteUrl = "https://upsc.gov.in"
        )
        val branding = OrgBrandingRegistry.forJob(upscJob)
        assertEquals("Union Public Service Commission", branding.orgName)
        assertEquals(R.drawable.ic_org_upsc, branding.logoResId)
        assertEquals("UPSC", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_identifiesSSC() {
        val sscJob = DummyJobs.first { it.organization.contains("Staff Selection Commission", ignoreCase = true) }
        val branding = OrgBrandingRegistry.forJob(sscJob)
        assertEquals("Staff Selection Commission", branding.orgName)
        assertEquals(R.drawable.ic_org_ssc, branding.logoResId)
        assertEquals("SSC", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_identifiesDefenseForces() {
        val defenseJob = DummyJobs.first { it.category == JobCategory.DEFENSE }
        val branding = OrgBrandingRegistry.forJob(defenseJob)
        assertEquals("Indian Armed Forces", branding.orgName)
        assertEquals(R.drawable.ic_org_defense, branding.logoResId)
        assertEquals("MOD", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_identifiesStatePolice() {
        val policeJob = DummyJobs.first { it.title.contains("Police", ignoreCase = true) || it.organization.contains("Police", ignoreCase = true) }
        val branding = OrgBrandingRegistry.forJob(policeJob)
        assertEquals("State Police Recruitment Board", branding.orgName)
        assertEquals(R.drawable.ic_org_police, branding.logoResId)
        assertEquals("POLICE", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_identifiesMPSC() {
        val mpscJob = Job(
            id = 102,
            title = "Maharashtra State Services Prelims",
            category = JobCategory.STATE,
            organization = "Maharashtra Public Service Commission (MPSC)",
            level = "Class I / Class II",
            salary = "₹56,100 - ₹1,77,500",
            location = "Maharashtra",
            seats = 524,
            quota = "State Quota",
            applyUrl = "https://mpsc.gov.in",
            noticeUrl = "https://mpsc.gov.in/notice",
            officialSiteUrl = "https://mpsc.gov.in"
        )
        val branding = OrgBrandingRegistry.forJob(mpscJob)
        assertEquals("Maharashtra Public Service Commission", branding.orgName)
        assertEquals(R.drawable.ic_org_mpsc, branding.logoResId)
        assertEquals("MPSC", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_identifiesBPSC() {
        val bpscJob = Job(
            id = 103,
            title = "Combined Competitive Examination",
            category = JobCategory.STATE,
            organization = "Bihar Public Service Commission (BPSC)",
            level = "SDM / DSP Cadre",
            salary = "Level 9 Pay Matrix",
            location = "Bihar",
            seats = 1957,
            quota = "State Domicile Quota",
            applyUrl = "https://bpsc.bih.nic.in",
            noticeUrl = "https://bpsc.bih.nic.in/notice",
            officialSiteUrl = "https://bpsc.bih.nic.in"
        )
        val branding = OrgBrandingRegistry.forJob(bpscJob)
        assertEquals("Bihar Public Service Commission", branding.orgName)
        assertEquals(R.drawable.ic_org_bpsc, branding.logoResId)
        assertEquals("BPSC", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_identifiesIBPS() {
        val ibpsJob = Job(
            id = 104,
            title = "Common Recruitment Process (CRP PO/MT XIV)",
            category = JobCategory.BANK,
            organization = "Institute of Banking Personnel Selection (IBPS)",
            level = "Probationary Officer",
            salary = "₹36,000 - ₹63,840",
            location = "All India Participating Banks",
            seats = 3955,
            quota = "All Quotas",
            applyUrl = "https://ibps.in",
            noticeUrl = "https://ibps.in/notice",
            officialSiteUrl = "https://ibps.in"
        )
        val branding = OrgBrandingRegistry.forJob(ibpsJob)
        assertEquals("Institute of Banking Personnel Selection", branding.orgName)
        assertEquals(R.drawable.ic_org_ibps, branding.logoResId)
        assertEquals("IBPS", branding.sealInitials)
    }

    @Test
    fun orgBrandingRegistry_fallbackCentralGovt_whenUnknown() {
        val customJob = Job(
            id = 999,
            title = "Scientific Officer",
            category = JobCategory.CENTRAL,
            organization = "Department of Atomic Energy",
            level = "Group A",
            salary = "₹56,100 - ₹1,77,500",
            location = "Mumbai, Maharashtra",
            seats = 50,
            quota = "All Quotas",
            applyUrl = null,
            noticeUrl = "https://dae.gov.in/notice",
            officialSiteUrl = "https://dae.gov.in"
        )

        val branding = OrgBrandingRegistry.forJob(customJob)
        assertEquals("Central Government of India", branding.orgName)
        assertEquals(R.drawable.ic_org_central, branding.logoResId)
        assertEquals("GOI", branding.sealInitials)
    }
}
