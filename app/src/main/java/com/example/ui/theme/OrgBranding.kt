package com.example.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R
import com.example.model.Job
import com.example.model.JobCategory

@Immutable
data class OrgBranding(
    val orgName: String,
    val authoritySubtext: String,
    val sealInitials: String,
    @param:DrawableRes val logoResId: Int,
    val watermarkIcon: ImageVector,
    // Contrast-calibrated Primary Colors (Dark vs Light)
    val primaryColorDark: Color,
    val primaryColorLight: Color,
    val accentColorDark: Color,
    val accentColorLight: Color,
    // Layered liquid-glass background gradients
    val surfaceGradientDark: Brush,
    val surfaceGradientLight: Brush,
    // Contrast-calibrated Badge Surfaces & Text
    val badgeSurfaceDark: Color,
    val badgeSurfaceLight: Color,
    val badgeTextDark: Color,
    val badgeTextLight: Color,
    // Specular border highlights
    val borderSpecularTopDark: Color,
    val borderSpecularTopLight: Color,
    // Watermark Opacities
    val watermarkColorDark: Color,
    val watermarkColorLight: Color
) {
    // Backward compatibility helper
    val primaryColor: Color get() = primaryColorLight
    val accentColor: Color get() = accentColorLight
    val icon: ImageVector get() = watermarkIcon
    val borderSpecularTop: Color get() = borderSpecularTopDark
    val borderSpecularBottom: Color get() = Color(0x1A000000)

    fun getPrimaryColor(isDark: Boolean): Color = if (isDark) primaryColorDark else primaryColorLight
    fun getAccentColor(isDark: Boolean): Color = if (isDark) accentColorDark else accentColorLight
    fun getBadgeSurface(isDark: Boolean): Color = if (isDark) badgeSurfaceDark else badgeSurfaceLight
    fun getBadgeText(isDark: Boolean): Color = if (isDark) badgeTextDark else badgeTextLight
    fun getSurfaceGradient(isDark: Boolean): Brush = if (isDark) surfaceGradientDark else surfaceGradientLight
    fun getBorderSpecularTop(isDark: Boolean): Color = if (isDark) borderSpecularTopDark else borderSpecularTopLight
    fun getWatermarkColor(isDark: Boolean): Color = if (isDark) watermarkColorDark else watermarkColorLight
}

object OrgBrandingRegistry {

    private val SBI = OrgBranding(
        orgName = "State Bank of India",
        authoritySubtext = "India's Largest Public Sector Bank • Est. 1806",
        sealInitials = "SBI",
        logoResId = R.drawable.ic_org_sbi,
        watermarkIcon = Icons.Default.AccountBalance,
        primaryColorDark = Color(0xFF64B5F6),
        primaryColorLight = Color(0xFF005A9E),
        accentColorDark = Color(0xFF81C784),
        accentColorLight = Color(0xFF1B5E20),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF0D1C2E), Color(0xFF08121E))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF2F8FD), Color(0xFFE5F1FB))
        ),
        badgeSurfaceDark = Color(0xFF0072BC).copy(alpha = 0.28f),
        badgeSurfaceLight = Color(0xFF0072BC).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFF90CAF9),
        badgeTextLight = Color(0xFF004377),
        borderSpecularTopDark = Color(0xFF64B5F6).copy(alpha = 0.45f),
        borderSpecularTopLight = Color(0xFF0072BC).copy(alpha = 0.25f),
        watermarkColorDark = Color(0xFF0072BC).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFF0072BC).copy(alpha = 0.05f)
    )

    private val RAILWAYS = OrgBranding(
        orgName = "Indian Railways",
        authoritySubtext = "Ministry of Railways • Government of India",
        sealInitials = "RRB",
        logoResId = R.drawable.ic_org_railways,
        watermarkIcon = Icons.Default.DirectionsTransit,
        primaryColorDark = Color(0xFFFFD54F),
        primaryColorLight = Color(0xFF1B365D),
        accentColorDark = Color(0xFFFFE082),
        accentColorLight = Color(0xFF8D6E14),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF161F2A), Color(0xFF0D141C))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF4F7FC), Color(0xFFE8EEF7))
        ),
        badgeSurfaceDark = Color(0xFFC59B27).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFF1B365D).copy(alpha = 0.10f),
        badgeTextDark = Color(0xFFFFE082),
        badgeTextLight = Color(0xFF12243F),
        borderSpecularTopDark = Color(0xFFFFD54F).copy(alpha = 0.45f),
        borderSpecularTopLight = Color(0xFF1B365D).copy(alpha = 0.22f),
        watermarkColorDark = Color(0xFFC59B27).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFF1B365D).copy(alpha = 0.05f)
    )

    private val SSC = OrgBranding(
        orgName = "Staff Selection Commission",
        authoritySubtext = "DoPT • Government of India",
        sealInitials = "SSC",
        logoResId = R.drawable.ic_org_ssc,
        watermarkIcon = Icons.Default.AssignmentInd,
        primaryColorDark = Color(0xFF81D4FA),
        primaryColorLight = Color(0xFF154360),
        accentColorDark = Color(0xFF4FC3F7),
        accentColorLight = Color(0xFF0E6251),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF121D28), Color(0xFF0A1118))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF3F7FA), Color(0xFFE6EFF5))
        ),
        badgeSurfaceDark = Color(0xFF0288D1).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFF154360).copy(alpha = 0.10f),
        badgeTextDark = Color(0xFFB3E5FC),
        badgeTextLight = Color(0xFF0E3349),
        borderSpecularTopDark = Color(0xFF81D4FA).copy(alpha = 0.40f),
        borderSpecularTopLight = Color(0xFF154360).copy(alpha = 0.20f),
        watermarkColorDark = Color(0xFF0288D1).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFF154360).copy(alpha = 0.05f)
    )

    private val UPSC = OrgBranding(
        orgName = "Union Public Service Commission",
        authoritySubtext = "Constitutional Authority of India • Art. 315",
        sealInitials = "UPSC",
        logoResId = R.drawable.ic_org_upsc,
        watermarkIcon = Icons.Default.Gavel,
        primaryColorDark = Color(0xFFFFD54F),
        primaryColorLight = Color(0xFF16253D),
        accentColorDark = Color(0xFFFFE082),
        accentColorLight = Color(0xFF8D6E14),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF161A22), Color(0xFF0B0E14))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFFAF8F2), Color(0xFFF0ECE0))
        ),
        badgeSurfaceDark = Color(0xFFD4AF37).copy(alpha = 0.24f),
        badgeSurfaceLight = Color(0xFFD4AF37).copy(alpha = 0.14f),
        badgeTextDark = Color(0xFFFFECB3),
        badgeTextLight = Color(0xFF634D0B),
        borderSpecularTopDark = Color(0xFFFFD54F).copy(alpha = 0.45f),
        borderSpecularTopLight = Color(0xFFD4AF37).copy(alpha = 0.30f),
        watermarkColorDark = Color(0xFFD4AF37).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFFD4AF37).copy(alpha = 0.06f)
    )

    private val POLICE = OrgBranding(
        orgName = "State Police Recruitment Board",
        authoritySubtext = "Department of Home Affairs • Law & Order",
        sealInitials = "POLICE",
        logoResId = R.drawable.ic_org_police,
        watermarkIcon = Icons.Default.LocalPolice,
        primaryColorDark = Color(0xFFEF9A9A),
        primaryColorLight = Color(0xFFB71C1C),
        accentColorDark = Color(0xFFFF8A80),
        accentColorLight = Color(0xFF7F0000),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF1A1316), Color(0xFF0F0B0D))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFFDF5F5), Color(0xFFF8E9E9))
        ),
        badgeSurfaceDark = Color(0xFFB71C1C).copy(alpha = 0.28f),
        badgeSurfaceLight = Color(0xFFB71C1C).copy(alpha = 0.10f),
        badgeTextDark = Color(0xFFFFCDD2),
        badgeTextLight = Color(0xFF7F0000),
        borderSpecularTopDark = Color(0xFFEF9A9A).copy(alpha = 0.40f),
        borderSpecularTopLight = Color(0xFFB71C1C).copy(alpha = 0.20f),
        watermarkColorDark = Color(0xFFB71C1C).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFFB71C1C).copy(alpha = 0.05f)
    )

    private val DEFENSE = OrgBranding(
        orgName = "Indian Armed Forces",
        authoritySubtext = "Ministry of Defence • Tri-Services Cadre",
        sealInitials = "MOD",
        logoResId = R.drawable.ic_org_defense,
        watermarkIcon = Icons.Default.Shield,
        primaryColorDark = Color(0xFFFFE082),
        primaryColorLight = Color(0xFF1B2A38),
        accentColorDark = Color(0xFF81D4FA),
        accentColorLight = Color(0xFF0D47A1),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF131A21), Color(0xFF090E13))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF4F6F7), Color(0xFFE6EAEB))
        ),
        badgeSurfaceDark = Color(0xFFC5A059).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFF1B2A38).copy(alpha = 0.10f),
        badgeTextDark = Color(0xFFFFECB3),
        badgeTextLight = Color(0xFF121B24),
        borderSpecularTopDark = Color(0xFFFFE082).copy(alpha = 0.40f),
        borderSpecularTopLight = Color(0xFF1B2A38).copy(alpha = 0.20f),
        watermarkColorDark = Color(0xFFC5A059).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFF1B2A38).copy(alpha = 0.05f)
    )

    private val MPSC = OrgBranding(
        orgName = "Maharashtra Public Service Commission",
        authoritySubtext = "Govt. of Maharashtra State Services",
        sealInitials = "MPSC",
        logoResId = R.drawable.ic_org_mpsc,
        watermarkIcon = Icons.Default.LocationCity,
        primaryColorDark = Color(0xFFFFB74D),
        primaryColorLight = Color(0xFFD35400),
        accentColorDark = Color(0xFFFFCC80),
        accentColorLight = Color(0xFFA04000),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF1E1712), Color(0xFF110D0A))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFFAF5EF), Color(0xFFF3EAE0))
        ),
        badgeSurfaceDark = Color(0xFFD35400).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFFD35400).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFFFFE0B2),
        badgeTextLight = Color(0xFF7E2E00),
        borderSpecularTopDark = Color(0xFFFFB74D).copy(alpha = 0.40f),
        borderSpecularTopLight = Color(0xFFD35400).copy(alpha = 0.25f),
        watermarkColorDark = Color(0xFFD35400).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFFD35400).copy(alpha = 0.05f)
    )

    private val BPSC = OrgBranding(
        orgName = "Bihar Public Service Commission",
        authoritySubtext = "Govt. of Bihar Administrative Cadre",
        sealInitials = "BPSC",
        logoResId = R.drawable.ic_org_bpsc,
        watermarkIcon = Icons.Default.LocationCity,
        primaryColorDark = Color(0xFFFFCC80),
        primaryColorLight = Color(0xFFA04000),
        accentColorDark = Color(0xFFFFB74D),
        accentColorLight = Color(0xFF7E3200),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF1C1511), Color(0xFF0F0B09))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFFAF5F1), Color(0xFFF3E8DF))
        ),
        badgeSurfaceDark = Color(0xFFA04000).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFFA04000).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFFFFE0B2),
        badgeTextLight = Color(0xFF602500),
        borderSpecularTopDark = Color(0xFFFFCC80).copy(alpha = 0.40f),
        borderSpecularTopLight = Color(0xFFA04000).copy(alpha = 0.25f),
        watermarkColorDark = Color(0xFFA04000).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFFA04000).copy(alpha = 0.05f)
    )

    private val IBPS = OrgBranding(
        orgName = "Institute of Banking Personnel Selection",
        authoritySubtext = "Common Banking Recruitment Examination",
        sealInitials = "IBPS",
        logoResId = R.drawable.ic_org_ibps,
        watermarkIcon = Icons.Default.Savings,
        primaryColorDark = Color(0xFF80CBC4),
        primaryColorLight = Color(0xFF00695C),
        accentColorDark = Color(0xFFA7FFEB),
        accentColorLight = Color(0xFF004D40),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF0E1A17), Color(0xFF06100E))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF1F8F6), Color(0xFFE2EFEA))
        ),
        badgeSurfaceDark = Color(0xFF00695C).copy(alpha = 0.28f),
        badgeSurfaceLight = Color(0xFF00695C).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFFB2DFDB),
        badgeTextLight = Color(0xFF003D34),
        borderSpecularTopDark = Color(0xFF80CBC4).copy(alpha = 0.40f),
        borderSpecularTopLight = Color(0xFF00695C).copy(alpha = 0.22f),
        watermarkColorDark = Color(0xFF00695C).copy(alpha = 0.08f),
        watermarkColorLight = Color(0xFF00695C).copy(alpha = 0.05f)
    )

    private val CENTRAL_DEFAULT = OrgBranding(
        orgName = "Central Government of India",
        authoritySubtext = "Official Gazette Recruitment Notice",
        sealInitials = "GOI",
        logoResId = R.drawable.ic_org_central,
        watermarkIcon = Icons.Default.Work,
        primaryColorDark = Color(0xFF90CAF9),
        primaryColorLight = Color(0xFF2C3E50),
        accentColorDark = Color(0xFF80DEEA),
        accentColorLight = Color(0xFF16A085),
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF141921), Color(0xFF0C1016))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF4F6F9), Color(0xFFE7EBF0))
        ),
        badgeSurfaceDark = Color(0xFF2C3E50).copy(alpha = 0.32f),
        badgeSurfaceLight = Color(0xFF2C3E50).copy(alpha = 0.10f),
        badgeTextDark = Color(0xFFCFD8DC),
        badgeTextLight = Color(0xFF1A252F),
        borderSpecularTopDark = Color(0xFF90CAF9).copy(alpha = 0.35f),
        borderSpecularTopLight = Color(0xFF2C3E50).copy(alpha = 0.20f),
        watermarkColorDark = Color(0xFF90CAF9).copy(alpha = 0.06f),
        watermarkColorLight = Color(0xFF2C3E50).copy(alpha = 0.04f)
    )

    fun forJob(job: Job): OrgBranding {
        val org = job.organization.lowercase()
        val title = job.title.lowercase()
        return when {
            org.contains("sbi") || org.contains("state bank") -> SBI
            org.contains("railway") || org.contains("rrb") || title.contains("rrb") || job.category == JobCategory.RAILWAY -> RAILWAYS
            org.contains("ssc") || org.contains("staff selection") -> SSC
            job.category == JobCategory.DEFENSE || org.contains("navy") || org.contains("army") || org.contains("defense") || org.contains("defence") || title.contains("nda") || title.contains("cds") -> DEFENSE
            org.contains("upsc") || org.contains("union public") -> UPSC
            org.contains("police") || title.contains("police") -> POLICE
            org.contains("mpsc") || title.contains("mpsc") -> MPSC
            org.contains("bpsc") || title.contains("bpsc") -> BPSC
            org.contains("ibps") || title.contains("ibps") || job.category == JobCategory.BANK -> IBPS
            else -> CENTRAL_DEFAULT
        }
    }
}
