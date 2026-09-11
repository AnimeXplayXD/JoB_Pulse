package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavTab(val title: String) {
    HOME("Home"),
    FEED("Feed"),
    SEARCH("Search"),
    ACCOUNT("Account")
}

enum class JobCategory(val displayName: String) {
    ALL("All"),
    CENTRAL("Central Govt"),
    STATE("State Govt"),
    RAILWAY("Railway"),
    BANK("Banking"),
    DEFENSE("Defense")
}

data class Job(
    val id: Int,
    val title: String,
    val category: JobCategory,
    val organization: String,
    val level: String,
    val salary: String,
    val location: String,
    val seats: Int,
    val quota: String,
    val applyUrl: String?,
    val noticeUrl: String,
    val officialSiteUrl: String,
    val state: String? = null,
    val minQualification: String = "Graduate",
    // Extended Full-Screen Fields
    val jobOverview: String = "",
    val locationDetails: String = "",
    val recruitmentExamDetails: String = "",
    val examPatternDetails: String = "",
    val preparationInfo: String = "",
    val seatsAndReservation: String = "",
    val cutOffInfo: String = "",
    val otherInfo: String = ""
)

data class FeedItem(
    val id: Int,
    val title: String,
    val tag: String, // "Exam Date", "Admit Card", "Result", "Notice", "Syllabus"
    val organization: String,
    val timeAgo: String,
    val summary: String,
    val noticeUrl: String,
    val isUrgent: Boolean = false
)

data class UserProfile(
    val name: String = "Aarav Sharma",
    val email: String = "aarav.aspirant@govmail.in",
    val rollNumberOrId: String = "GOVT-ASP-2026-9821",
    val targetExam: String = "UPSC CSE & SSC CGL",
    val qualification: String = "Bachelor of Technology / Graduate",
    val categoryQuota: String = "General / EWS",
    val stateResidence: String = "Uttar Pradesh",
    val pushAlertsEnabled: Boolean = true,
    val examDateAlertsEnabled: Boolean = true,
    val admitCardAlertsEnabled: Boolean = true
)

val IndianStates = listOf(
    "All States", "Uttar Pradesh", "Maharashtra", "Bihar", "West Bengal",
    "Madhya Pradesh", "Tamil Nadu", "Rajasthan", "Karnataka", "Gujarat", "Andhra Pradesh"
)

val QualificationLevels = listOf(
    "All Qualifications", "10th Pass", "12th Pass", "Diploma", "Graduate", "Post Graduate"
)

val DummyJobs = listOf(
    Job(
        1, "RRB NTPC Graduate Level", JobCategory.RAILWAY, "Indian Railways", "Group C", "₹35,400 - ₹1,12,400", "All India", 11558, "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", "https://rrbcdg.gov.in/apply", "https://rrbcdg.gov.in/notice", "https://rrbcdg.gov.in/", null, "Graduate",
        jobOverview = "Recruitment for Non-Technical Popular Categories (NTPC) under Indian Railways. Includes posts like Station Master, Goods Guard, and Commercial Apprentice.",
        locationDetails = "Pan-India posting based on the chosen RRB zone (e.g., RRB Allahabad, RRB Chennai, RRB Mumbai).",
        recruitmentExamDetails = "Application closes on 25th Oct. CBT-1 expected in Jan. Selection involves CBT-1, CBT-2, Typing Test (for some posts), and Document Verification.",
        examPatternDetails = "CBT-1: 100 questions (Math, Reasoning, General Awareness) in 90 mins. 1/3 negative marking. CBT-2 has a similar structure with higher difficulty.",
        preparationInfo = "Focus heavily on current affairs, static GK, and quantitative aptitude. Practice previous year RRB NTPC papers.",
        seatsAndReservation = "Total: 11,558 seats. UR: ~4600, OBC: ~3100, SC: ~1700, ST: ~850, EWS: ~1100. PwBD reservation applies.",
        cutOffInfo = "Expected CBT-1 cutoff for UR is typically around 75-80 marks depending on the zone.",
        otherInfo = "Medical standards (A2, A3, etc.) are strictly enforced for posts like Station Master."
    ),
    Job(
        2, "SBI Probationary Officer", JobCategory.BANK, "State Bank of India", "Officer Scale I", "₹41,960 - ₹63,840", "All India", 2000, "UR: 40.5%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", "https://sbi.co.in/careers", "https://sbi.co.in/notice", "https://sbi.co.in/", null, "Graduate",
        jobOverview = "Direct recruitment for the prestigious Probationary Officer (PO) role at the State Bank of India. Offers excellent career growth and benefits.",
        locationDetails = "Initial posting anywhere in India. Candidates must be willing to serve across any branch nationwide.",
        recruitmentExamDetails = "Prelims in Nov. Mains in Dec. Selection: Prelims (Phase-I), Mains (Phase-II), Psychometric Test & Interview (Phase-III).",
        examPatternDetails = "Prelims: 100 Qs (English, Quant, Reasoning) in 60 mins. Mains: Objective + Descriptive Test. Negative marking: 1/4th.",
        preparationInfo = "Requires high speed in Quantitative Aptitude and strong reading comprehension. Practice banking mocks regularly.",
        seatsAndReservation = "Total 2000 seats. UR: 810, OBC: 540, SC: 300, ST: 150, EWS: 200.",
        cutOffInfo = "Previous year prelims cutoff: ~59.50 (UR). Mains cutoff: ~88.93 (UR).",
        otherInfo = "Selected candidates execute a bond of ₹2.00 Lakhs to serve the bank for a minimum of 3 years."
    ),
    Job(
        3, "NDA & NA Examination", JobCategory.DEFENSE, "UPSC (Defense)", "Officer", "₹56,100 - ₹1,77,500", "All India", 400, "Based on Merit", null, "https://upsc.gov.in/notice", "https://upsc.gov.in/", null, "12th Pass",
        jobOverview = "Gateway to join the Indian Army, Navy, and Air Force as a commissioned officer after completing 12th standard.",
        locationDetails = "Training at National Defence Academy (NDA), Khadakwasla, Pune. Posting anywhere in India based on the armed forces branch.",
        recruitmentExamDetails = "Written exam conducted by UPSC, followed by a rigorous 5-day SSB Interview.",
        examPatternDetails = "Paper I: Mathematics (300 Marks). Paper II: General Ability Test (600 Marks). Total 900 marks. 1/3 negative marking.",
        preparationInfo = "Mathematics level is up to 12th standard (CBSE syllabus). GAT covers Physics, Chemistry, History, Geography, and English.",
        seatsAndReservation = "Army: 208, Navy: 42, Air Force: 120, Naval Academy: 30. No category-based reservation; selection is purely merit-based.",
        cutOffInfo = "Written exam cutoff usually hovers around 350-360 out of 900. Minimum 25% marks required in each subject.",
        otherInfo = "Strict physical and medical standards apply. Unmarried male and female candidates are eligible."
    ),
    Job(
        4, "SSC CGL", JobCategory.CENTRAL, "Staff Selection Commission", "Group B & C", "₹25,500 - ₹1,51,100", "All India", 17727, "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", "https://ssc.nic.in/apply", "https://ssc.nic.in/notice", "https://ssc.nic.in/", null, "Graduate",
        jobOverview = "Combined Graduate Level (CGL) exam for Group B and C posts in various Ministries/Departments of the Govt of India.",
        locationDetails = "Ministries in Delhi (CSS) or all-India field postings (Excise, Income Tax, etc.) based on merit and preference.",
        recruitmentExamDetails = "Tier I (Qualifying) followed by Tier II (Merit deciding). Document verification done by user departments.",
        examPatternDetails = "Tier I: 100 Qs (Maths, English, Reasoning, GS). Tier II: Comprehensive paper covering Maths, Reasoning, English, GS, and Computer.",
        preparationInfo = "Advanced Maths (Geometry, Trigonometry, Algebra) and English grammar are crucial. Attempt timed mocks.",
        seatsAndReservation = "Total 17,727 tentative vacancies across multiple departments. Standard Govt of India reservations apply.",
        cutOffInfo = "Tier I UR cutoff typically ranges from 130 to 150 depending on the difficulty and number of vacancies.",
        otherInfo = "Age limit varies post-wise (usually 18-27 or 18-32). Computer proficiency is mandatory for most posts."
    ),
    Job(
        5, "UP Police Constable", JobCategory.STATE, "UP Police", "Group C", "₹21,700 - ₹69,100", "Uttar Pradesh", 60244, "UR: 40%, OBC: 27%, SC: 21%, ST: 2%, EWS: 10%", "https://uppbpb.gov.in/apply", "https://uppbpb.gov.in/notice", "https://uppbpb.gov.in/", "Uttar Pradesh", "12th Pass",
        jobOverview = "Massive recruitment drive for Constables in Uttar Pradesh Police.",
        locationDetails = "Posting in various districts of Uttar Pradesh.",
        recruitmentExamDetails = "Written Exam (OMR based) -> Physical Standard Test (PST) -> Physical Efficiency Test (PET).",
        examPatternDetails = "150 questions (General Knowledge, General Hindi, Numerical/Mental Ability, Reasoning). Total 300 marks. -0.5 negative marking.",
        preparationInfo = "Focus on UP specific General Knowledge, basic mathematics, and Hindi grammar. Regular physical running is necessary for PET.",
        seatsAndReservation = "Total: 60,244. UR: 24,102, EWS: 6,024, OBC: 16,264, SC: 12,650, ST: 1,204. 20% horizontal reservation for women.",
        cutOffInfo = "Historical cutoffs for UR males hover around 185-195 out of 300.",
        otherInfo = "Male PET: 4.8 km in 25 mins. Female PET: 2.4 km in 14 mins."
    ),
    Job(
        6, "MPSC Civil Services", JobCategory.STATE, "MPSC", "Class I & II", "₹56,100 - ₹1,77,500", "Maharashtra", 274, "State Govt Quotas apply", null, "https://mpsc.gov.in/notice", "https://mpsc.gov.in/", "Maharashtra", "Graduate",
        jobOverview = "Maharashtra Gazetted Civil Services for prestigious posts like Deputy Collector, DSP, and Tehsildar.",
        locationDetails = "Posting within the state of Maharashtra.",
        recruitmentExamDetails = "Prelims -> Mains -> Interview. Entire process takes about a year.",
        examPatternDetails = "Prelims: Paper I (GS) & Paper II (CSAT - qualifying). Mains: Descriptive pattern (GS, Essay, Marathi/English, Optional Subject).",
        preparationInfo = "Thorough knowledge of Maharashtra's history, geography, and polity is essential. Practice descriptive answer writing.",
        seatsAndReservation = "Constitutional reservations apply as per Maharashtra state policy (including SEBC/Maratha quota as applicable).",
        cutOffInfo = "Prelims cutoff depends heavily on paper difficulty, generally ~105-115 for UR (GS Paper I).",
        otherInfo = "Knowledge of Marathi language (reading, writing, speaking) is compulsory."
    ),
    Job(
        7, "BPSC Combined Competitive Exam", JobCategory.STATE, "BPSC", "Class I & II", "₹53,100 - ₹1,67,800", "Bihar", 346, "State Govt Quotas apply", "https://bpsc.bih.nic.in/apply", "https://bpsc.bih.nic.in/notice", "https://bpsc.bih.nic.in/", "Bihar", "Graduate",
        jobOverview = "Bihar Public Service Commission exam for administrative roles like SDM, DySP, Revenue Officer in Bihar Govt.",
        locationDetails = "Posting anywhere across the 38 districts of Bihar.",
        recruitmentExamDetails = "Preliminary Exam (Objective) -> Main Exam (Descriptive) -> Interview.",
        examPatternDetails = "Prelims: 150 marks GS paper (1/3rd negative marking). Mains: General Hindi, GS I, GS II, Essay, and one qualifying Optional Subject.",
        preparationInfo = "Focus on Bihar Special current affairs, history, and economy. Essay writing practice is crucial.",
        seatsAndReservation = "Reservations for BC, EBC, SC, ST, EWS. 35% horizontal reservation for women across all categories.",
        cutOffInfo = "Prelims cutoff generally stays around 90-95 marks out of 150 for the UR category.",
        otherInfo = "Final merit list is prepared on the basis of Mains (900 marks) and Interview (120 marks)."
    ),
    Job(
        8, "IBPS PO", JobCategory.BANK, "IBPS", "Officer Scale I", "₹36,000 - ₹63,840", "All India", 3049, "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", "https://ibps.in/apply", "https://ibps.in/notice", "https://ibps.in/", null, "Graduate",
        jobOverview = "Common Recruitment Process for Probationary Officers in 11 Participating Public Sector Banks (PNB, BOB, Canara, etc.).",
        locationDetails = "All-India transfer liability. Can be posted in rural, semi-urban, urban, or metro branches.",
        recruitmentExamDetails = "Prelims -> Mains -> Interview. Final allotment is based on combined Mains + Interview score.",
        examPatternDetails = "Prelims: 100 Qs in 1 hour. Mains: 155 Qs in 3 hours + English Descriptive Test (2 Qs in 30 mins).",
        preparationInfo = "Banking awareness, high-level Data Interpretation, and critical reasoning are the main hurdles in Mains.",
        seatsAndReservation = "Total 3049 seats distributed among participating banks according to category.",
        cutOffInfo = "Mains cutoff is typically around 65-75 out of 225 due to high difficulty. Sectional cutoffs apply.",
        otherInfo = "Bank preferences chosen during application play a major role in final allotment."
    ),
    Job(
        9, "Indian Navy Agniveer", JobCategory.DEFENSE, "Indian Navy", "Sailor", "₹30,000 / month", "All India", 1365, "Based on Merit", "https://joinindiannavy.gov.in/apply", "https://joinindiannavy.gov.in/notice", "https://joinindiannavy.gov.in/", null, "10th Pass",
        jobOverview = "Recruitment under the Agnipath scheme for Senior Secondary Recruit (SSR) and Matric Recruit (MR).",
        locationDetails = "Training at INS Chilka. Deployment on ships/submarines/bases across the country.",
        recruitmentExamDetails = "Computer Based Examination (INET) -> Written Exam -> PFT -> Medical Standard Test.",
        examPatternDetails = "INET comprises 100 objective questions covering Science, Mathematics, English, and General Awareness.",
        preparationInfo = "Maintain excellent physical fitness. Study 10th/12th level Science and Maths thoroughly.",
        seatsAndReservation = "No category reservation. Maximum 20% of Agniveers may be enrolled as regular cadres after 4 years.",
        cutOffInfo = "State-wise merit lists are generated, hence cutoffs vary significantly by state of domicile.",
        otherInfo = "Engagement period is 4 years. Agniveers receive a Seva Nidhi package of ~11.71 Lakhs upon exit."
    ),
    Job(
        10, "RRB Group D", JobCategory.RAILWAY, "Indian Railways", "Group D", "₹18,000 - ₹56,900", "All India", 103769, "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%", null, "https://rrbcdg.gov.in/notice", "https://rrbcdg.gov.in/", null, "10th Pass",
        jobOverview = "Level-1 posts (Track Maintainer, Assistant Pointsman, Helper, etc.) in various departments of Indian Railways.",
        locationDetails = "Posting within the jurisdiction of the applied Railway Recruitment Cell (RRC) zone.",
        recruitmentExamDetails = "Computer Based Test (CBT) -> Physical Efficiency Test (PET) -> Document Verification -> Medical.",
        examPatternDetails = "CBT: 100 questions (General Science, Maths, Reasoning, General Awareness) in 90 minutes. 1/3 negative marking.",
        preparationInfo = "Focus heavily on Class 10 level General Science (Physics, Chemistry, Bio) and basic Maths/Reasoning.",
        seatsAndReservation = "Total >1 Lakh vacancies. Significant reservation for Course Completed Act Apprentices (CCAA).",
        cutOffInfo = "Normalized score cutoffs usually range from 60 to 75 depending on the RRC zone.",
        otherInfo = "PET for males: run 1000m in 4m 15s and carry 35kg for 100m in 2m. Extremely strict medical check (especially vision)."
    )
)

val DummyFeedItems = listOf(
    FeedItem(
        id = 101,
        title = "RRB NTPC CBT-1 Exam Schedule & Shift Timings Released",
        tag = "Exam Date",
        organization = "Railway Recruitment Board (RRB)",
        timeAgo = "18 mins ago",
        summary = "Computer Based Test stage 1 will commence across designated test centers pan-India. City intimation slips will be active 10 days prior.",
        noticeUrl = "https://rrbcdg.gov.in/notice",
        isUrgent = true
    ),
    FeedItem(
        id = 102,
        title = "SSC CGL Tier-II Official Hall Ticket / Admit Card Download Link Live",
        tag = "Admit Card",
        organization = "Staff Selection Commission",
        timeAgo = "1 hour ago",
        summary = "Eligible candidates who qualified Tier-I can now log in using registration number and date of birth to download their e-admit card.",
        noticeUrl = "https://ssc.nic.in/notice",
        isUrgent = true
    ),
    FeedItem(
        id = 103,
        title = "UPSC Civil Services Preliminary Examination 2026 Notification Out",
        tag = "Notice",
        organization = "Union Public Service Commission",
        timeAgo = "3 hours ago",
        summary = "UPSC has published the CSE 2026 detailed gazette notice announcing tentative vacancies, eligibility requirements, and deadline for OTR registration.",
        noticeUrl = "https://upsc.gov.in/notice"
    ),
    FeedItem(
        id = 104,
        title = "UP Police Constable Re-Exam Official Answer Key & Objection Window",
        tag = "Answer Key",
        organization = "UPPRPB Lucknow",
        timeAgo = "5 hours ago",
        summary = "Provisional answer key published for all shifts. Objections can be submitted online with documentary proof within 5 days.",
        noticeUrl = "https://uppbpb.gov.in/notice"
    ),
    FeedItem(
        id = 105,
        title = "SBI PO Mains Merit List & Interview Schedule Declared",
        tag = "Result",
        organization = "State Bank of India",
        timeAgo = "Yesterday",
        summary = "SBI Central Recruitment & Promotion Department has released roll numbers of candidates shortlisted for Phase-III psychometric test and interview.",
        noticeUrl = "https://sbi.co.in/notice"
    ),
    FeedItem(
        id = 106,
        title = "Indian Army Technical Graduate Course (TGC-141) Cut-off Marks Published",
        tag = "Notice",
        organization = "Join Indian Army (HQ MoD)",
        timeAgo = "Yesterday",
        summary = "Branch-wise cutoff percentages for engineering streams published for SSB interview shortlisting at Selection Centers.",
        noticeUrl = "https://joinindiannavy.gov.in/notice"
    )
)

data class CategoryThemeInfo(
    val primaryColor: Color,
    val gradient: Brush,
    val icon: ImageVector
)

fun getThemeInfoForCategory(category: JobCategory): CategoryThemeInfo {
    return when (category) {
        JobCategory.RAILWAY -> CategoryThemeInfo(
            primaryColor = Color(0xFF1565C0),
            gradient = Brush.linearGradient(listOf(Color(0xFF1976D2).copy(alpha = 0.85f), Color(0xFF0D47A1).copy(alpha = 0.95f))),
            icon = Icons.Default.DirectionsTransit
        )
        JobCategory.BANK -> CategoryThemeInfo(
            primaryColor = Color(0xFF2E7D32),
            gradient = Brush.linearGradient(listOf(Color(0xFF388E3C).copy(alpha = 0.85f), Color(0xFF1B5E20).copy(alpha = 0.95f))),
            icon = Icons.Default.AccountBalance
        )
        JobCategory.DEFENSE -> CategoryThemeInfo(
            primaryColor = Color(0xFF455A64),
            gradient = Brush.linearGradient(listOf(Color(0xFF546E7A).copy(alpha = 0.85f), Color(0xFF263238).copy(alpha = 0.95f))),
            icon = Icons.Default.Security
        )
        JobCategory.STATE -> CategoryThemeInfo(
            primaryColor = Color(0xFFE65100),
            gradient = Brush.linearGradient(listOf(Color(0xFFF57C00).copy(alpha = 0.85f), Color(0xFFE65100).copy(alpha = 0.95f))),
            icon = Icons.Default.Map
        )
        JobCategory.CENTRAL -> CategoryThemeInfo(
            primaryColor = Color(0xFFC62828),
            gradient = Brush.linearGradient(listOf(Color(0xFFD32F2F).copy(alpha = 0.85f), Color(0xFFB71C1C).copy(alpha = 0.95f))),
            icon = Icons.Default.Flag
        )
        JobCategory.ALL -> CategoryThemeInfo(
            primaryColor = Color(0xFF673AB7),
            gradient = Brush.linearGradient(listOf(Color(0xFF7E57C2).copy(alpha = 0.85f), Color(0xFF4527A0).copy(alpha = 0.95f))),
            icon = Icons.Default.Work
        )
    }
}
