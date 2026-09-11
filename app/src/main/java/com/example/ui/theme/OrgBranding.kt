package com.example.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.model.Job

@Immutable
data class OrgBranding(
    val orgName: String,
    val authoritySubtext: String,
    val sealInitials: String,
    val primaryColor: Color,
    val accentColor: Color,
    val icon: ImageVector,
    // Layered glass background gradients
    val surfaceGradientDark: Brush,
    val surfaceGradientLight: Brush,
    // Tinted surface colors for chips & badges
    val badgeSurfaceDark: Color,
    val badgeSurfaceLight: Color,
    val badgeTextDark: Color,
    val badgeTextLight: Color,
    // Specular border highlights
    val borderSpecularTop: Color,
    val borderSpecularBottom: Color
)

object OrgBrandingRegistry {

    private val SBI = OrgBranding(
        orgName = "State Bank of India",
        authoritySubtext = "India's Largest Public Sector Bank",
        sealInitials = "SBI",
        primaryColor = Color(0xFF0072BC),
        accentColor = Color(0xFF00A859),
        icon = Icons.Default.AccountBalance,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF0D1B2A), Color(0xFF0A192F))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF0F7FD), Color(0xFFE8F2FA))
        ),
        badgeSurfaceDark = Color(0xFF0072BC).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFF0072BC).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFF64B5F6),
        badgeTextLight = Color(0xFF005A9E),
        borderSpecularTop = Color(0xFF64B5F6).copy(alpha = 0.40f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val RAILWAYS = OrgBranding(
        orgName = "Indian Railways",
        authoritySubtext = "Ministry of Railways • Govt. of India",
        sealInitials = "RRB",
        primaryColor = Color(0xFF1B365D),
        accentColor = Color(0xFFC59B27),
        icon = Icons.Default.DirectionsTransit,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF141E28), Color(0xFF0E1720))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF4F7FB), Color(0xFFEBF0F7))
        ),
        badgeSurfaceDark = Color(0xFFC59B27).copy(alpha = 0.20f),
        badgeSurfaceLight = Color(0xFFC59B27).copy(alpha = 0.15f),
        badgeTextDark = Color(0xFFFFD54F),
        badgeTextLight = Color(0xFF8A6D1C),
        borderSpecularTop = Color(0xFFFFD54F).copy(alpha = 0.35f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val SSC = OrgBranding(
        orgName = "Staff Selection Commission",
        authoritySubtext = "DoPT • Government of India",
        sealInitials = "SSC",
        primaryColor = Color(0xFF1F3A60),
        accentColor = Color(0xFF2980B9),
        icon = Icons.Default.AssignmentInd,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF111A24), Color(0xFF0B121A))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF2F6FA), Color(0xFFE7EEF5))
        ),
        badgeSurfaceDark = Color(0xFF2980B9).copy(alpha = 0.22f),
        badgeSurfaceLight = Color(0xFF1F3A60).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFF81D4FA),
        badgeTextLight = Color(0xFF154360),
        borderSpecularTop = Color(0xFF81D4FA).copy(alpha = 0.35f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val UPSC = OrgBranding(
        orgName = "Union Public Service Commission",
        authoritySubtext = "Constitutional Authority of India",
        sealInitials = "UPSC",
        primaryColor = Color(0xFF16253D),
        accentColor = Color(0xFFD4AF37),
        icon = Icons.Default.Gavel,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF121924), Color(0xFF0A1017))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFFAF8F2), Color(0xFFF2EEE2))
        ),
        badgeSurfaceDark = Color(0xFFD4AF37).copy(alpha = 0.22f),
        badgeSurfaceLight = Color(0xFFD4AF37).copy(alpha = 0.15f),
        badgeTextDark = Color(0xFFFFE082),
        badgeTextLight = Color(0xFF8D6E14),
        borderSpecularTop = Color(0xFFFFE082).copy(alpha = 0.40f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val POLICE = OrgBranding(
        orgName = "State Police Recruitment",
        authoritySubtext = "Home Police Department • Law & Order",
        sealInitials = "POLICE",
        primaryColor = Color(0xFF1A237E),
        accentColor = Color(0xFFB71C1C),
        icon = Icons.Default.LocalPolice,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF151722), Color(0xFF0D0F17))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF7F4F6), Color(0xFFEFE8EC))
        ),
        badgeSurfaceDark = Color(0xFFB71C1C).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFFB71C1C).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFFEF9A9A),
        badgeTextLight = Color(0xFF8E0000),
        borderSpecularTop = Color(0xFFEF9A9A).copy(alpha = 0.35f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val DEFENSE = OrgBranding(
        orgName = "Indian Armed Forces",
        authoritySubtext = "Ministry of Defence • Govt. of India",
        sealInitials = "MOD",
        primaryColor = Color(0xFF1B2A38),
        accentColor = Color(0xFFC5A059),
        icon = Icons.Default.Shield,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF101920), Color(0xFF090E13))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF4F6F5), Color(0xFFE8ECE9))
        ),
        badgeSurfaceDark = Color(0xFFC5A059).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFFC5A059).copy(alpha = 0.15f),
        badgeTextDark = Color(0xFFFFE082),
        badgeTextLight = Color(0xFF7D601E),
        borderSpecularTop = Color(0xFFFFE082).copy(alpha = 0.35f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val MPSC = OrgBranding(
        orgName = "Maharashtra Public Service Commission",
        authoritySubtext = "Govt. of Maharashtra State Services",
        sealInitials = "MPSC",
        primaryColor = Color(0xFFD35400),
        accentColor = Color(0xFF2C3E50),
        icon = Icons.Default.LocationCity,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF1F1814), Color(0xFF130E0B))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFFAF5F0), Color(0xFFF3EAE1))
        ),
        badgeSurfaceDark = Color(0xFFD35400).copy(alpha = 0.22f),
        badgeSurfaceLight = Color(0xFFD35400).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFFFFB74D),
        badgeTextLight = Color(0xFFA04000),
        borderSpecularTop = Color(0xFFFFB74D).copy(alpha = 0.35f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val BPSC = OrgBranding(
        orgName = "Bihar Public Service Commission",
        authoritySubtext = "Govt. of Bihar Administrative Cadre",
        sealInitials = "BPSC",
        primaryColor = Color(0xFFA04000),
        accentColor = Color(0xFF1F3A60),
        icon = Icons.Default.LocationCity,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF1D1714), Color(0xFF110D0B))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFFAF5F2), Color(0xFFF2E9E3))
        ),
        badgeSurfaceDark = Color(0xFFA04000).copy(alpha = 0.22f),
        badgeSurfaceLight = Color(0xFFA04000).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFFFFCC80),
        badgeTextLight = Color(0xFF7E3200),
        borderSpecularTop = Color(0xFFFFCC80).copy(alpha = 0.35f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val IBPS = OrgBranding(
        orgName = "Institute of Banking Personnel Selection",
        authoritySubtext = "Common Banking Recruitment Board",
        sealInitials = "IBPS",
        primaryColor = Color(0xFF00695C),
        accentColor = Color(0xFF004D40),
        icon = Icons.Default.Savings,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF0E1A17), Color(0xFF07110F))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF0F7F6), Color(0xFFE2EFEA))
        ),
        badgeSurfaceDark = Color(0xFF00695C).copy(alpha = 0.25f),
        badgeSurfaceLight = Color(0xFF00695C).copy(alpha = 0.12f),
        badgeTextDark = Color(0xFF80CBC4),
        badgeTextLight = Color(0xFF004D40),
        borderSpecularTop = Color(0xFF80CBC4).copy(alpha = 0.35f),
        borderSpecularBottom = Color(0x1A000000)
    )

    private val CENTRAL_DEFAULT = OrgBranding(
        orgName = "Central Government of India",
        authoritySubtext = "Gazette Notification • Official Recruitment",
        sealInitials = "GOI",
        primaryColor = Color(0xFF2C3E50),
        accentColor = Color(0xFF16A085),
        icon = Icons.Default.Work,
        surfaceGradientDark = Brush.linearGradient(
            colors = listOf(Color(0xFF151A21), Color(0xFF0D1117))
        ),
        surfaceGradientLight = Brush.linearGradient(
            colors = listOf(Color(0xFFF4F6F8), Color(0xFFE9EDF1))
        ),
        badgeSurfaceDark = Color(0xFF34495E).copy(alpha = 0.30f),
        badgeSurfaceLight = Color(0xFF2C3E50).copy(alpha = 0.10f),
        badgeTextDark = Color(0xFFB0BEC5),
        badgeTextLight = Color(0xFF2C3E50),
        borderSpecularTop = Color(0xFFB0BEC5).copy(alpha = 0.30f),
        borderSpecularBottom = Color(0x1A000000)
    )

    fun forJob(job: Job): OrgBranding {
        val org = job.organization.lowercase()
        val title = job.title.lowercase()
        return when {
            org.contains("sbi") || org.contains("state bank") -> SBI
            org.contains("railway") || org.contains("rrb") || title.contains("rrb") -> RAILWAYS
            org.contains("ssc") || org.contains("staff selection") -> SSC
            org.contains("upsc") || org.contains("union public") -> UPSC
            org.contains("police") || title.contains("police") -> POLICE
            org.contains("navy") || org.contains("army") || org.contains("defense") || org.contains("defence") || title.contains("nda") -> DEFENSE
            org.contains("mpsc") || title.contains("mpsc") -> MPSC
            org.contains("bpsc") || title.contains("bpsc") -> BPSC
            org.contains("ibps") || title.contains("ibps") -> IBPS
            else -> CENTRAL_DEFAULT
        }
    }
}
