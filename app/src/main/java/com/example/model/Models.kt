package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
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

@Immutable
data class TimelineMilestone(
    val eventName: String,
    val dateString: String,
    val isPassed: Boolean = false,
    val isCrucial: Boolean = false
)

@Immutable
data class VacancyCategoryQuota(
    val categoryName: String,
    val count: Int,
    val percentage: String
)

@Immutable
data class ExamStageInfo(
    val stageName: String,
    val mode: String,
    val duration: String,
    val questions: Int,
    val marks: Int,
    val negativeMarking: String,
    val subjects: List<String>
)

enum class CutoffType(val label: String, val indicatorColor: Long) {
    OFFICIAL("Official Gazetted", 0xFF2DA44E),
    HISTORICAL("Historical Prev Year", 0xFF0969DA),
    ESTIMATED("Community Estimate", 0xFFD29922)
}

@Immutable
data class CutoffEntry(
    val category: String,
    val score: String,
    val type: CutoffType,
    val yearOrShift: String,
    val qualifyingMarks: String = "40%"
)

@Immutable
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
    val otherInfo: String = "",
    // Rich Structured Recruitment Data
    val applicationStartDate: String = "15 Sep 2026",
    val applicationClosingDate: String = "15 Oct 2026",
    val examDate: String = "Nov / Dec 2026",
    val ageLimit: String = "20 to 30 Years (Relaxation for SC/ST/OBC)",
    val applicationFee: String = "₹100 (UR/OBC) • Exempted for SC/ST/Women/PwBD",
    val selectionStagesSummary: String = "Tier I (CBT) ➔ Tier II (CBT) ➔ Document Verification & Medicals",
    val milestones: List<TimelineMilestone> = emptyList(),
    val categoryQuotas: List<VacancyCategoryQuota> = emptyList(),
    val examStages: List<ExamStageInfo> = emptyList(),
    val cutoffBenchmarks: List<CutoffEntry> = emptyList(),
    val syllabusTopics: List<String> = emptyList()
)

@Immutable
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

@Stable
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
        id = 1,
        title = "RRB NTPC Graduate Level",
        category = JobCategory.RAILWAY,
        organization = "Indian Railways",
        level = "Group C (Pay Level 4-6)",
        salary = "₹35,400 - ₹1,12,400",
        location = "All India (Zonal Rail Boards)",
        seats = 11558,
        quota = "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%",
        applyUrl = "https://rrbcdg.gov.in/apply",
        noticeUrl = "https://rrbcdg.gov.in/notice",
        officialSiteUrl = "https://rrbcdg.gov.in/",
        state = null,
        minQualification = "Graduate",
        jobOverview = "Centralized recruitment for Non-Technical Popular Categories (NTPC) under Indian Railways. Prestigious executive cadre posts include Station Master, Goods Guard, Senior Clerk cum Typist, and Commercial Apprentice across 21 Railway Recruitment Boards.",
        locationDetails = "Pan-India posting across 21 RRB Zones (e.g. Northern Railway, Western Railway, Southern Railway). Transfer policy governed by Indian Railway Establishment Manual (IREM).",
        recruitmentExamDetails = "Online application window open till 25th Oct 2026. CBT-1 expected Dec 2026 / Jan 2027. Multi-tier selection process: Computer Based Test Stage 1 (Screening), Computer Based Test Stage 2 (Merit), Computer Based Aptitude Test (CBAT for Station Master), Document Verification, and Class A2/A3 Medical Examination.",
        examPatternDetails = "CBT-1: 100 Multiple Choice Questions (General Awareness: 40 Qs, Mathematics: 30 Qs, General Intelligence & Reasoning: 30 Qs) in 90 Minutes. Strict 1/3 negative marking for each incorrect response.",
        preparationInfo = "Prioritize Current Affairs (National & International, Science & Tech, Sports), Indian Railways History & Trivia, Arithmetic Aptitude (Percentages, Profit & Loss, SI/CI, Geometry), and Analytical Reasoning.",
        seatsAndReservation = "Total 11,558 Posts. UR: 4,623, OBC: 3,120, SC: 1,733, ST: 867, EWS: 1,215. Horizontal reservations applicable: PwBD (4%), Ex-Servicemen (10%), and Course Completed Act Apprentices (CCAA).",
        cutOffInfo = "Official CBT-1 normalized cut-off scores vary by chosen RRB Board. Zone cutoffs range between 71.50 and 78.90 for Unreserved candidates.",
        otherInfo = "Physical vision test standards (A2 without glasses for Station Master) are strictly non-negotiable.",
        applicationStartDate = "16 Sep 2026",
        applicationClosingDate = "25 Oct 2026",
        examDate = "15 Jan 2027",
        ageLimit = "18 to 33 Years (Relaxable up to 5 yrs for SC/ST, 3 yrs for OBC-NCL)",
        applicationFee = "₹500 (₹400 refunded after appearing in CBT-1 for UR/OBC) • ₹250 for SC/ST/Women",
        selectionStagesSummary = "CBT-1 ➔ CBT-2 ➔ Typing / CBAT Aptitude ➔ DV & Medical",
        milestones = listOf(
            TimelineMilestone("Official Gazette Release", "14 Sep 2026", isPassed = true),
            TimelineMilestone("Online Registration Starts", "16 Sep 2026", isPassed = true),
            TimelineMilestone("Registration Closes", "25 Oct 2026", isCrucial = true),
            TimelineMilestone("City Intimation Slip", "05 Jan 2027"),
            TimelineMilestone("CBT Stage 1 Exam", "15 Jan 2027", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Unreserved (UR)", 4623, "40.0%"),
            VacancyCategoryQuota("OBC (Non-Creamy)", 3120, "27.0%"),
            VacancyCategoryQuota("Scheduled Caste (SC)", 1733, "15.0%"),
            VacancyCategoryQuota("Scheduled Tribe (ST)", 867, "7.5%"),
            VacancyCategoryQuota("Economically Weaker (EWS)", 1215, "10.5%")
        ),
        examStages = listOf(
            ExamStageInfo("CBT-1 Screening", "Computer Based (Online)", "90 Mins", 100, 100, "1/3rd Negative (-0.33)", listOf("General Awareness (40 Qs)", "Mathematics (30 Qs)", "General Intelligence & Reasoning (30 Qs)")),
            ExamStageInfo("CBT-2 Merit Deciding", "Computer Based (Online)", "90 Mins", 120, 120, "1/3rd Negative (-0.33)", listOf("General Awareness (50 Qs)", "Mathematics (35 Qs)", "Reasoning & Logic (35 Qs)")),
            ExamStageInfo("CBAT / Typing Skill", "Qualifying Speed Test", "10 Mins", 0, 0, "No Negative", listOf("Typing Speed: 30 wpm English or 25 wpm Hindi on PC"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("Unreserved (UR)", "74.82 / 100", CutoffType.OFFICIAL, "2024 RRB Normalized"),
            CutoffEntry("OBC (NCL)", "70.15 / 100", CutoffType.OFFICIAL, "2024 RRB Normalized"),
            CutoffEntry("EWS", "66.40 / 100", CutoffType.OFFICIAL, "2024 RRB Normalized"),
            CutoffEntry("SC", "62.10 / 100", CutoffType.OFFICIAL, "2024 RRB Normalized"),
            CutoffEntry("ST", "58.90 / 100", CutoffType.OFFICIAL, "2024 RRB Normalized"),
            CutoffEntry("UR Expected 2026", "75.00 - 78.50", CutoffType.ESTIMATED, "Post-Mock Projection")
        ),
        syllabusTopics = listOf(
            "General Awareness: Indian Polity, Constitution, Space & Defense, Economic Policies, Railway Milestones",
            "Mathematics: Number Systems, BODMAS, Decimals, Fractions, LCM & HCF, Ratio & Proportion, Mensuration",
            "Reasoning: Analogies, Coding-Decoding, Syllogism, Venn Diagrams, Puzzle & Seating Arrangement"
        )
    ),
    Job(
        id = 2,
        title = "SBI Probationary Officer (PO)",
        category = JobCategory.BANK,
        organization = "State Bank of India",
        level = "Junior Management Grade Scale I",
        salary = "₹41,960 - ₹63,840 (Gross ₹82,000+)",
        location = "All India (Transferable)",
        seats = 2000,
        quota = "UR: 810, OBC: 540, SC: 300, ST: 150, EWS: 200",
        applyUrl = "https://sbi.co.in/careers",
        noticeUrl = "https://sbi.co.in/notice",
        officialSiteUrl = "https://sbi.co.in/",
        state = null,
        minQualification = "Graduate",
        jobOverview = "Premier banking recruitment by the State Bank of India. Direct appointment as Scale-I Probationary Officer leading to rapid career escalation toward Chief General Manager and Managing Director ranks.",
        locationDetails = "Initial posting anywhere across India's 22,000+ SBI branches. Transfer liability every 3-5 years with mandatory rural and semi-urban service tenures.",
        recruitmentExamDetails = "Phase-I Preliminary Exam in Nov 2026, followed by Phase-II Mains (Objective + English Descriptive) in Dec 2026, Psychometric Profiling, and Phase-III Group Exercise & Interview in Feb 2027.",
        examPatternDetails = "Prelims: 100 Questions (English Language: 30 Qs, Quantitative Aptitude: 35 Qs, Reasoning Ability: 35 Qs) in 60 minutes with 20 minutes sectional timing. 1/4th negative marking.",
        preparationInfo = "Emphasize high-speed mental calculation, Data Interpretation (Tabular, Radar, Missing Caselets), Puzzles & Seating Arrangements, and Banking/Financial Current Affairs.",
        seatsAndReservation = "Total 2000 vacancies. Regular: UR: 810, OBC: 540, SC: 300, ST: 150, EWS: 200. PwD reservations (VI, HI, LD, d&e) as per Rights of Persons with Disabilities Act 2016.",
        cutOffInfo = "Official Prelims Cutoff 2024: UR: 59.50, EWS: 59.50, OBC: 59.50, SC: 53.00, ST: 47.50. Mains Cutoff (out of 250): UR: 88.93, OBC: 80.96.",
        otherInfo = "Selected candidates execute a service indemnity bond of ₹2.00 Lakhs agreeing to serve the Bank for a minimum tenure of three years.",
        applicationStartDate = "18 Sep 2026",
        applicationClosingDate = "08 Oct 2026",
        examDate = "18 Nov 2026",
        ageLimit = "21 to 30 Years (Upper age relaxation applicable)",
        applicationFee = "₹750 for General/EWS/OBC • Nil for SC/ST/PwBD",
        selectionStagesSummary = "Phase-I Prelims ➔ Phase-II Mains & Descriptive ➔ Phase-III Group Exercise & Interview",
        milestones = listOf(
            TimelineMilestone("Official Notification", "17 Sep 2026", isPassed = true),
            TimelineMilestone("Online Registration Opens", "18 Sep 2026", isPassed = true),
            TimelineMilestone("Registration Deadline", "08 Oct 2026", isCrucial = true),
            TimelineMilestone("Prelims Admit Card", "05 Nov 2026"),
            TimelineMilestone("Phase-I Prelims Exam", "18 Nov 2026", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("General / UR", 810, "40.5%"),
            VacancyCategoryQuota("OBC (Non-Creamy)", 540, "27.0%"),
            VacancyCategoryQuota("Scheduled Caste (SC)", 300, "15.0%"),
            VacancyCategoryQuota("Economically Weaker (EWS)", 200, "10.0%"),
            VacancyCategoryQuota("Scheduled Tribe (ST)", 150, "7.5%")
        ),
        examStages = listOf(
            ExamStageInfo("Phase-I Prelims", "Online Objective", "60 Mins", 100, 100, "1/4th Negative (-0.25)", listOf("English (30 Qs / 20m)", "Quantitative Aptitude (35 Qs / 20m)", "Reasoning Ability (35 Qs / 20m)")),
            ExamStageInfo("Phase-II Mains", "Objective + Descriptive", "3h 30m", 157, 250, "1/4th Negative (-0.25)", listOf("Reasoning & Computer (40 Qs)", "Data Analysis & Interpretation (30 Qs)", "General / Economy / Banking (50 Qs)", "English Descriptive Letter & Essay (2 Qs / 50 Marks)")),
            ExamStageInfo("Phase-III Interview", "In-Person Board", "30 Mins", 0, 50, "No Negative", listOf("Group Discussion / Exercise (20 Marks)", "Personal Interview (30 Marks)"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("General (UR) Prelims", "59.50 / 100", CutoffType.OFFICIAL, "2024 SBI Final"),
            CutoffEntry("OBC (NCL) Prelims", "59.50 / 100", CutoffType.OFFICIAL, "2024 SBI Final"),
            CutoffEntry("EWS Prelims", "59.50 / 100", CutoffType.OFFICIAL, "2024 SBI Final"),
            CutoffEntry("SC Prelims", "53.00 / 100", CutoffType.OFFICIAL, "2024 SBI Final"),
            CutoffEntry("ST Prelims", "47.50 / 100", CutoffType.OFFICIAL, "2024 SBI Final"),
            CutoffEntry("Mains Overall UR", "88.93 / 250", CutoffType.HISTORICAL, "2024 Final Merit Threshold"),
            CutoffEntry("UR Prelims Expected 2026", "61.00 - 64.00", CutoffType.ESTIMATED, "Aspirant Pool Estimation")
        ),
        syllabusTopics = listOf(
            "Quantitative Aptitude: High-level DI (Bar, Pie, Line, Caselets), Arithmetic Word Problems, Approximations",
            "Reasoning: Multi-variable Puzzles, Floor & Flat arrangements, Reverse Syllogisms, Critical Reasoning",
            "Banking Awareness: RBI Monetary Policy, Basel-III Norms, Financial Instruments, Inflation & Fiscal Deficit"
        )
    ),
    Job(
        id = 3,
        title = "National Defence Academy (NDA)",
        category = JobCategory.DEFENSE,
        organization = "UPSC (Defense)",
        level = "Cadet / Commissioned Officer",
        salary = "₹56,100 - ₹1,77,500 (Level 10)",
        location = "NDA Khadakwasla, Pune ➔ Pan India",
        seats = 400,
        quota = "Purely Merit-Based Selection",
        applyUrl = null,
        noticeUrl = "https://upsc.gov.in/notice",
        officialSiteUrl = "https://upsc.gov.in/",
        state = null,
        minQualification = "12th Pass",
        jobOverview = "Prestigious tri-service military academy of the Indian Armed Forces. Equips young male and female cadets for permanent commissions in the Indian Army, Navy, and Air Force.",
        locationDetails = "3-year institutional joint training at NDA Khadakwasla (Pune), followed by 1 year specialized pre-commission training at IMA Dehradun, INA Ezhimala, or AFA Dundigal.",
        recruitmentExamDetails = "Conducted twice a year by UPSC. Stage 1: Written Examination (900 Marks). Stage 2: 5-Day Services Selection Board (SSB) Interview assessing Officer Like Qualities (OLQs), psychological tests, GTO tasks, and personal conference.",
        examPatternDetails = "Mathematics (Paper I): 120 Questions (300 Marks, 2.5 hours, -0.83 mark per wrong answer). General Ability Test (Paper II): 150 Questions (600 Marks: English 200, General Knowledge 400, -1.33 per wrong answer).",
        preparationInfo = "Calculus, Coordinate Geometry, Trigonometry, Vectors, Newtonian Mechanics, Modern Physics, Modern Indian History, Indian Geography, and English Comprehension.",
        seatsAndReservation = "Army: 208, Navy: 42, Air Force: 120 (including 28 for Flying), Naval Academy (10+2 Cadet Entry): 30. No caste or reservation quotas exist in Armed Forces officer recruitment.",
        cutOffInfo = "Historical written cutoff ranges between 355 and 365 out of 900 marks, with a mandatory minimum qualifying score of 25% in each subject paper.",
        otherInfo = "Candidate must be unmarried and between 16.5 and 19.5 years old. Strict height, weight, and vision standards apply.",
        applicationStartDate = "20 Dec 2026",
        applicationClosingDate = "09 Jan 2027",
        examDate = "21 Apr 2027",
        ageLimit = "16.5 to 19.5 Years",
        applicationFee = "₹100 (Exempted for SC/ST and all female candidates)",
        selectionStagesSummary = "UPSC Written Exam (900 M) ➔ 5-Day SSB Interview (900 M) ➔ Medical Board",
        milestones = listOf(
            TimelineMilestone("UPSC Notification Released", "20 Dec 2026"),
            TimelineMilestone("Online Application Closes", "09 Jan 2027", isCrucial = true),
            TimelineMilestone("E-Admit Card Available", "01 Apr 2027"),
            TimelineMilestone("NDA Written Exam", "21 Apr 2027", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Army Wing", 208, "52.0%"),
            VacancyCategoryQuota("Air Force Wing", 120, "30.0%"),
            VacancyCategoryQuota("Navy Wing", 42, "10.5%"),
            VacancyCategoryQuota("Naval Academy (10+2)", 30, "7.5%")
        ),
        examStages = listOf(
            ExamStageInfo("Mathematics (Paper I)", "Offline Pen-Paper OMR", "150 Mins", 120, 300, "-0.83 Negative", listOf("Algebra", "Matrices & Determinants", "Trigonometry", "Analytical Geometry", "Calculus")),
            ExamStageInfo("General Ability Test (Paper II)", "Offline Pen-Paper OMR", "150 Mins", 150, 600, "-1.33 Negative", listOf("Part A: English (50 Qs / 200 Marks)", "Part B: Physics, Chemistry, Bio, History, Geography, Current Affairs (100 Qs / 400 Marks)")),
            ExamStageInfo("SSB Interview", "5-Day Residential Board", "5 Days", 0, 900, "No Negative", listOf("Stage-I: Screening (OIR & PPDT)", "Stage-II: Psychological Tests (TAT, WAT, SRT)", "Group Testing Officer (GTO) Outdoor Tasks", "Personal Interview & Conference"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("Written Exam Cutoff", "358 / 900", CutoffType.OFFICIAL, "UPSC NDA I 2024"),
            CutoffEntry("Final Recommended (With SSB)", "722 / 1800", CutoffType.OFFICIAL, "UPSC NDA I 2024"),
            CutoffEntry("Written Qualifying Subject Min", "25% in each Paper", CutoffType.OFFICIAL, "Mandatory Sectional Threshold"),
            CutoffEntry("Projected Written 2027", "360 - 370", CutoffType.ESTIMATED, "Competitive Projections")
        ),
        syllabusTopics = listOf(
            "Mathematics: 11th & 12th CBSE curriculum (Differential Calculus, Integral Calculus, Matrices)",
            "GAT English: Spotting Errors, Sentence Ordering, Vocabulary, Comprehension",
            "GAT Science: Laws of Motion, Optics, Electricity, Acids & Bases, Environmental Science"
        )
    ),
    Job(
        id = 4,
        title = "SSC Combined Graduate Level (CGL)",
        category = JobCategory.CENTRAL,
        organization = "Staff Selection Commission",
        level = "Group B & C Non-Technical",
        salary = "₹25,500 - ₹1,51,100 (Level 4-8)",
        location = "All India (Ministries in New Delhi & Field Offices)",
        seats = 17727,
        quota = "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%",
        applyUrl = "https://ssc.nic.in/apply",
        noticeUrl = "https://ssc.nic.in/notice",
        officialSiteUrl = "https://ssc.nic.in/",
        state = null,
        minQualification = "Graduate",
        jobOverview = "Flagship pan-India recruitment for premier executive and inspection roles: Assistant Section Officer (CSS, MEA, IB), Inspector of Central Excise, Preventive Officer, Income Tax Inspector, and Sub-Inspector in CBI.",
        locationDetails = "Ministries located at Central Secretariat (New Delhi) or field postings across all states and union territories. Transfer guidelines vary by cadre controlling authority.",
        recruitmentExamDetails = "Tier I: Computer Based Screening Examination. Tier II: Multi-session CBT with Paper-I (Maths, Reasoning, English, General Awareness, Computer Knowledge Module, and Data Entry Speed Test).",
        examPatternDetails = "Tier I: 100 Questions (200 Marks) in 60 mins. -0.50 negative marking. Tier II Paper I: 130 Questions (390 Marks) + 20 Qs Computer Module (60 Marks qualifying) + 15 min Typing Test (2000 key depressions).",
        preparationInfo = "Master Advanced Mathematics (Geometry, Trigonometry, Mensuration), English Comprehension & Vocabulary, Current Affairs, and ensure typing proficiency on computer keyboards.",
        seatsAndReservation = "Total 17,727 tentative positions across 38 Central Departments. Statutory reservations apply for SC, ST, OBC, EWS, Ex-Servicemen, and Persons with Benchmark Disabilities.",
        cutOffInfo = "Official Tier-I UR Cutoff 2024: 150.04. OBC: 145.38. EWS: 143.22. SC: 126.83. ST: 118.16 marks (Normalized scores).",
        otherInfo = "Age criteria post-dependent: 18-27 yrs for Auditor/Accountant, 18-30 yrs for Inspector/ASO, up to 32 yrs for Junior Statistical Officer (JSO).",
        applicationStartDate = "24 Jun 2026",
        applicationClosingDate = "27 Jul 2026",
        examDate = "09 Sep 2026",
        ageLimit = "18 to 30 / 32 Years depending on post",
        applicationFee = "₹100 (Free for Women, SC, ST, PwBD, Ex-Servicemen)",
        selectionStagesSummary = "Tier I (CBT Screening) ➔ Tier II (CBT Merit + DEST Typing) ➔ Dept Verification",
        milestones = listOf(
            TimelineMilestone("Gazette Release", "24 Jun 2026", isPassed = true),
            TimelineMilestone("Online Application Closes", "27 Jul 2026", isPassed = true),
            TimelineMilestone("Tier-I Hall Ticket", "29 Aug 2026", isPassed = true),
            TimelineMilestone("Tier-I Exam", "09 Sep 2026", isCrucial = true),
            TimelineMilestone("Tier-II Mains Exam", "18 Dec 2026", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Unreserved (UR)", 7111, "40.1%"),
            VacancyCategoryQuota("OBC (Non-Creamy)", 4786, "27.0%"),
            VacancyCategoryQuota("Scheduled Caste (SC)", 2659, "15.0%"),
            VacancyCategoryQuota("Economically Weaker (EWS)", 1772, "10.0%"),
            VacancyCategoryQuota("Scheduled Tribe (ST)", 1399, "7.9%")
        ),
        examStages = listOf(
            ExamStageInfo("Tier-I Preliminary", "Computer Based (Online)", "60 Mins", 100, 200, "-0.50 Negative", listOf("General Intelligence (25 Qs / 50 M)", "General Awareness (25 Qs / 50 M)", "Quantitative Aptitude (25 Qs / 50 M)", "English Comprehension (25 Qs / 50 M)")),
            ExamStageInfo("Tier-II Paper-I", "Multi-Session CBT", "135 Mins", 150, 450, "-1.00 Negative", listOf("Section 1: Maths & Reasoning (60 Qs / 180 M)", "Section 2: English & GA (70 Qs / 210 M)", "Section 3: Computer Knowledge (20 Qs / 60 M qualifying)", "Data Entry Speed Test (DEST 15 mins)"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("UR / General (Tier-I)", "150.04 / 200", CutoffType.OFFICIAL, "2024 Staff Selection Commission"),
            CutoffEntry("OBC (NCL) (Tier-I)", "145.38 / 200", CutoffType.OFFICIAL, "2024 Staff Selection Commission"),
            CutoffEntry("EWS (Tier-I)", "143.22 / 200", CutoffType.OFFICIAL, "2024 Staff Selection Commission"),
            CutoffEntry("SC (Tier-I)", "126.83 / 200", CutoffType.OFFICIAL, "2024 Staff Selection Commission"),
            CutoffEntry("ST (Tier-I)", "118.16 / 200", CutoffType.OFFICIAL, "2024 Staff Selection Commission"),
            CutoffEntry("Inspector (CGST) Tier-II Score", "322 / 390", CutoffType.HISTORICAL, "Previous Year Allocation Benchmarks"),
            CutoffEntry("ASO (MEA) Tier-II Score", "334 / 390", CutoffType.HISTORICAL, "Top Preference Threshold"),
            CutoffEntry("Projected Tier-I Cutoff 2026", "148.00 - 152.00", CutoffType.ESTIMATED, "Normalized Range Forecast")
        ),
        syllabusTopics = listOf(
            "Quantitative Aptitude: Number Theory, Algebra, Geometry (Circles, Triangles), Trigonometry, Heights & Distances",
            "Reasoning: Critical Reasoning, Statement & Assumptions, Non-Verbal Series, Matrix",
            "English: Reading Comprehension, Idioms & Phrases, Cloze Test, Active/Passive Voice, Narration"
        )
    ),
    Job(
        id = 5,
        title = "UP Police Constable Direct Recruitment",
        category = JobCategory.STATE,
        organization = "UP Police Recruitment Board",
        level = "Group C Civil Police & PAC",
        salary = "₹21,700 - ₹69,100 (Grade Pay ₹2,000)",
        location = "Uttar Pradesh (All 75 Districts)",
        seats = 60244,
        quota = "UR: 40%, OBC: 27%, SC: 21%, ST: 2%, EWS: 10%",
        applyUrl = "https://uppbpb.gov.in/apply",
        noticeUrl = "https://uppbpb.gov.in/notice",
        officialSiteUrl = "https://uppbpb.gov.in/",
        state = "Uttar Pradesh",
        minQualification = "12th Pass",
        jobOverview = "Historic mass direct recruitment drive for 60,244 Constables in Uttar Pradesh Police (Civil Police and PAC battalions) to strengthen community policing and law enforcement infrastructure.",
        locationDetails = "Posting across police stations, commissionerates, and PAC headquarters within Uttar Pradesh. Domicile candidates eligible for reservation; open to all Indian citizens for UR quota.",
        recruitmentExamDetails = "Stage 1: Written Examination (OMR-based offline exam across 75 districts). Stage 2: Document Verification and Physical Standard Test (PST). Stage 3: Physical Efficiency Test (PET qualifying run).",
        examPatternDetails = "150 Questions (300 Marks) in 120 Minutes. Four sections: General Knowledge (38 Qs), General Hindi (37 Qs), Numerical & Mental Ability (38 Qs), Mental Aptitude & Reasoning (37 Qs). Negative marking: -0.50 mark per wrong answer.",
        preparationInfo = "Emphasize UP Specific GK, Indian Constitution, Police System & Public Interest, Hindi Grammar (Vyakaran, Sandhi, Samas, Ras, Alankar), and basic arithmetic.",
        seatsAndReservation = "Total 60,244 Posts. UR: 24,102, EWS: 6,024, OBC: 16,264, SC: 12,650, ST: 1,204. Horizontal 20% reservation for female aspirants (~12,000 seats).",
        cutOffInfo = "Official Written Cutoff (Out of 300): UR Males: ~188-192, OBC Males: ~178-182, SC Males: ~155-160, ST Males: ~125-130 marks.",
        otherInfo = "Male PET: 4.8 km run in 25 minutes. Female PET: 2.4 km run in 14 minutes. Minimum male height: 168 cm (160 cm for ST).",
        applicationStartDate = "27 Dec 2026",
        applicationClosingDate = "16 Jan 2027",
        examDate = "17 Feb 2027",
        ageLimit = "18 to 22 Years (Extended up to 25 yrs with state relaxation)",
        applicationFee = "₹400 for all candidate categories",
        selectionStagesSummary = "Offline OMR Written (300 M) ➔ DV & Physical Standards ➔ PET Running Test",
        milestones = listOf(
            TimelineMilestone("Recruitment Gazette", "23 Dec 2026"),
            TimelineMilestone("Online Applications Begin", "27 Dec 2026"),
            TimelineMilestone("Application Deadline", "16 Jan 2027", isCrucial = true),
            TimelineMilestone("District Center Slips", "10 Feb 2027"),
            TimelineMilestone("Written OMR Exam", "17 Feb 2027", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Unreserved (UR)", 24102, "40.0%"),
            VacancyCategoryQuota("OBC", 16264, "27.0%"),
            VacancyCategoryQuota("Scheduled Caste (SC)", 12650, "21.0%"),
            VacancyCategoryQuota("Economically Weaker (EWS)", 6024, "10.0%"),
            VacancyCategoryQuota("Scheduled Tribe (ST)", 1204, "2.0%")
        ),
        examStages = listOf(
            ExamStageInfo("OMR Written Examination", "Offline Pen-Paper OMR", "120 Mins", 150, 300, "-0.50 Negative", listOf("General Knowledge (38 Qs / 76 M)", "General Hindi (37 Qs / 74 M)", "Numerical & Mental Ability (38 Qs / 76 M)", "Mental Aptitude, IQ & Reasoning (37 Qs / 74 M)")),
            ExamStageInfo("Physical Efficiency Test (PET)", "Ground Running Track", "25 Mins (M) / 14 Mins (F)", 0, 0, "Qualifying Only", listOf("Male: 4.8 km in 25 Minutes", "Female: 2.4 km in 14 Minutes"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("UR (General Male)", "189.50 / 300", CutoffType.OFFICIAL, "UPPRPB Gazetted"),
            CutoffEntry("OBC (Non-Creamy)", "180.20 / 300", CutoffType.OFFICIAL, "UPPRPB Gazetted"),
            CutoffEntry("EWS", "172.50 / 300", CutoffType.OFFICIAL, "UPPRPB Gazetted"),
            CutoffEntry("SC", "159.00 / 300", CutoffType.OFFICIAL, "UPPRPB Gazetted"),
            CutoffEntry("ST", "127.80 / 300", CutoffType.OFFICIAL, "UPPRPB Gazetted"),
            CutoffEntry("Female UR Horizontal", "181.00 / 300", CutoffType.HISTORICAL, "Previous Cohort Threshold")
        ),
        syllabusTopics = listOf(
            "General Knowledge: UP Geography, Agriculture, Trade, Revenue Administration, Internal Security & Terrorism",
            "General Hindi: Vyakaran, Tatsam-Tadbhav, Paryayvachi, Vilom, Anekaarthak, Ling, Vachan, Kaarak, Kriya",
            "Numerical Ability: Number System, Simplification, Decimal & Fraction, HCF/LCM, Ratio, Percentage"
        )
    ),
    Job(
        id = 6,
        title = "MPSC State Services Gazetted Exam",
        category = JobCategory.STATE,
        organization = "Maharashtra Public Service Commission",
        level = "Class I & II Gazetted Cadre",
        salary = "₹56,100 - ₹1,77,500 (Level S-20)",
        location = "Maharashtra (Mantralaya & Divisional Cadres)",
        seats = 274,
        quota = "State Constitutional Quotas Apply",
        applyUrl = null,
        noticeUrl = "https://mpsc.gov.in/notice",
        officialSiteUrl = "https://mpsc.gov.in/",
        state = "Maharashtra",
        minQualification = "Graduate",
        jobOverview = "Apex state civil services recruitment for Deputy Collector (Group A), Deputy Superintendent of Police (DySP), Assistant Commissioner of State Tax, and Block Development Officer (BDO).",
        locationDetails = "Postings across 6 administrative divisions of Maharashtra (Konkan, Pune, Nashik, Aurangabad, Amravati, Nagpur). Compulsory command over spoken and written Marathi language.",
        recruitmentExamDetails = "Three-tier examination pattern aligned with UPSC standards: Preliminary Examination (Paper I GS + Paper II CSAT qualifying) ➔ Descriptive Main Examination (6 Papers / 800 Marks) ➔ Interview & Personality Test (100 Marks).",
        examPatternDetails = "Prelims Paper I: 100 Questions (200 Marks, 1/4th negative). Prelims Paper II (CSAT): 80 Questions (200 Marks, qualifying at 33%). Mains: General Studies 1-4 descriptive papers + Compulsory Marathi & English.",
        preparationInfo = "History of Modern Maharashtra and Social Reformers (Jyotirao Phule, Dr. B.R. Ambedkar, Shahu Maharaj), Geography of Maharashtra, Panchayati Raj, and Marathi essay writing.",
        seatsAndReservation = "Total 274 Posts. Reservations for SC (13%), ST (7%), VJ/DT (3%), NT-B (2.5%), NT-C (3.5%), NT-D (2%), OBC (19%), EWS (10%), SEBC/Maratha reservation as per state statutes.",
        cutOffInfo = "Official Prelims GS Paper-I Cutoff: Open/General: 108.50, OBC: 108.50, SC: 102.00, ST: 89.00 marks out of 200.",
        otherInfo = "Physical standards mandatory for DySP post: Male height min 165 cm, Female height min 157 cm.",
        applicationStartDate = "05 Jan 2027",
        applicationClosingDate = "25 Jan 2027",
        examDate = "28 Apr 2027",
        ageLimit = "19 to 38 Years (43 Years for Reserved categories)",
        applicationFee = "₹394 (Open Category) • ₹294 (Reserved Category)",
        selectionStagesSummary = "Prelims (GS+CSAT) ➔ Descriptive Mains (800 M) ➔ Personality Test (100 M)",
        milestones = listOf(
            TimelineMilestone("Preliminary Notification", "02 Jan 2027"),
            TimelineMilestone("Online Applications Close", "25 Jan 2027", isCrucial = true),
            TimelineMilestone("Prelims Admit Card", "15 Apr 2027"),
            TimelineMilestone("Prelims Examination", "28 Apr 2027", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Open / General", 102, "37.2%"),
            VacancyCategoryQuota("OBC", 52, "19.0%"),
            VacancyCategoryQuota("Scheduled Caste (SC)", 36, "13.0%"),
            VacancyCategoryQuota("Economically Weaker (EWS)", 27, "10.0%"),
            VacancyCategoryQuota("Scheduled Tribe (ST)", 19, "7.0%"),
            VacancyCategoryQuota("VJA/NT/SBC", 38, "13.8%")
        ),
        examStages = listOf(
            ExamStageInfo("Preliminary Paper I (GS)", "Objective OMR", "120 Mins", 100, 200, "-0.25 Negative", listOf("History, Geography, Polity, Economy, Ecology, Science & Current Events")),
            ExamStageInfo("Preliminary Paper II (CSAT)", "Objective Qualifying (33%)", "120 Mins", 80, 200, "-0.25 Negative", listOf("Reading Comprehension (Marathi/English), Interpersonal Skills, Logical Reasoning")),
            ExamStageInfo("Descriptive Mains", "Written Subjective", "6 Sessions", 0, 800, "No Negative", listOf("Language Marathi & English (Descriptive)", "GS-I (History/Geo/Agri)", "GS-II (Polity/Law)", "GS-III (HRD)", "GS-IV (Economy/Tech)"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("Open / General (Prelims)", "108.50 / 200", CutoffType.OFFICIAL, "2024 MPSC Official"),
            CutoffEntry("OBC (NCL) Prelims", "108.50 / 200", CutoffType.OFFICIAL, "2024 MPSC Official"),
            CutoffEntry("SC Prelims", "102.00 / 200", CutoffType.OFFICIAL, "2024 MPSC Official"),
            CutoffEntry("ST Prelims", "89.00 / 200", CutoffType.OFFICIAL, "2024 MPSC Official"),
            CutoffEntry("DySP Open Final Merit Cutoff", "512 / 900", CutoffType.HISTORICAL, "Previous Selection Cycle")
        ),
        syllabusTopics = listOf(
            "Maharashtra Heritage: 19th Century Social Renaissance, Chhatrapati Shivaji Maharaj administration",
            "Geography: Western Ghats, Sahyadri river drainage, Agro-climatic zones, Minerals & Industrial nodes",
            "Administration: Maharashtra Land Revenue Code, Right to Public Services Act, Zilla Parishad governance"
        )
    ),
    Job(
        id = 7,
        title = "BPSC Combined Competitive Examination",
        category = JobCategory.STATE,
        organization = "Bihar Public Service Commission",
        level = "Class I & II State Administrative Services",
        salary = "₹53,100 - ₹1,67,800 (Level 9)",
        location = "Bihar (Districts & Patna Secretariat)",
        seats = 346,
        quota = "State Govt Quotas & 35% Women Quota",
        applyUrl = "https://bpsc.bih.nic.in/apply",
        noticeUrl = "https://bpsc.bih.nic.in/notice",
        officialSiteUrl = "https://bpsc.bih.nic.in/",
        state = "Bihar",
        minQualification = "Graduate",
        jobOverview = "The premiere administrative examination of Bihar State. Selects candidates for Sub-Divisional Officer (SDM), Deputy Superintendent of Police (DySP), Commercial Taxes Officer, and Rural Development Officer.",
        locationDetails = "Postings across the 38 administrative districts of Bihar and departments in the Patna Old Secretariat.",
        recruitmentExamDetails = "Stage 1: Preliminary Exam (150 Marks Objective). Stage 2: Main Exam (General Hindi qualifying + GS-I + GS-II + Essay + Optional). Stage 3: Personal Interview (120 Marks).",
        examPatternDetails = "Prelims: 150 Multiple Choice Questions (150 Marks, 2 Hours). 1/3rd negative marking (-0.33 per wrong answer). Mains: General Hindi (100 M qualifying), GS-I (300 M), GS-II (300 M), Essay Paper (300 M).",
        preparationInfo = "Thorough coverage of Bihar History (Maurya, Gupta, Buddhism, 1857 Revolt in Bihar, Champaran Satyagraha), Bihar Geography & Rivers (Ganga basin, Kosi floods), and Statistical Analysis Graphs.",
        seatsAndReservation = "Total 346 Posts. Reservations: Extremely Backward Class (EBC 25%), Backward Class (BC 18%), SC (20%), ST (2%), EWS (10%). Mandatory 35% horizontal reservation for female candidates.",
        cutOffInfo = "Official Prelims Cutoff: Unreserved: 91.67, EWS: 86.33, SC: 79.67, ST: 74.00, BC: 88.67, EBC: 86.33 marks out of 150.",
        otherInfo = "Optional subject in Mains is qualifying with objective MCQ format (100 marks); merit is ranked out of 900 marks (GS1 + GS2 + Essay) + 120 marks Interview.",
        applicationStartDate = "15 Jul 2026",
        applicationClosingDate = "05 Aug 2026",
        examDate = "30 Sep 2026",
        ageLimit = "20/21/22 to 37 Years (40 for BC/EBC/Women, 42 for SC/ST)",
        applicationFee = "₹600 for General/OBC/Other State • ₹150 for SC/ST/Female of Bihar",
        selectionStagesSummary = "Prelims (150 M) ➔ Subjective Mains (900 M) ➔ Personality Interview (120 M)",
        milestones = listOf(
            TimelineMilestone("Official Notification Released", "10 Jul 2026", isPassed = true),
            TimelineMilestone("Online Application Closed", "05 Aug 2026", isPassed = true),
            TimelineMilestone("Admit Card Available", "20 Sep 2026", isPassed = true),
            TimelineMilestone("Prelims Exam Date", "30 Sep 2026", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Unreserved (UR)", 121, "35.0%"),
            VacancyCategoryQuota("Extremely Backward Class (EBC)", 86, "25.0%"),
            VacancyCategoryQuota("Backward Class (BC)", 62, "18.0%"),
            VacancyCategoryQuota("Scheduled Caste (SC)", 42, "12.0%"),
            VacancyCategoryQuota("Economically Weaker (EWS)", 35, "10.0%")
        ),
        examStages = listOf(
            ExamStageInfo("Preliminary Exam", "Objective Offline OMR", "120 Mins", 150, 150, "1/3rd Negative (-0.33)", listOf("General Science", "National & Bihar History", "Geography & Rivers", "Indian Polity & Economy", "Mental Ability")),
            ExamStageInfo("Main Examination", "Descriptive Subjective", "3 Days", 0, 900, "No Negative", listOf("General Hindi (100 M Qualifying)", "General Studies Paper-I (300 Marks)", "General Studies Paper-II (300 Marks)", "Essay Paper (300 Marks)", "Optional Subject (100 M MCQ qualifying)"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("Unreserved (UR) Male", "91.67 / 150", CutoffType.OFFICIAL, "BPSC Official Gazette"),
            CutoffEntry("Unreserved Female", "84.00 / 150", CutoffType.OFFICIAL, "BPSC Official Gazette"),
            CutoffEntry("EBC Male", "86.33 / 150", CutoffType.OFFICIAL, "BPSC Official Gazette"),
            CutoffEntry("BC Male", "88.67 / 150", CutoffType.OFFICIAL, "BPSC Official Gazette"),
            CutoffEntry("SC Male", "79.67 / 150", CutoffType.OFFICIAL, "BPSC Official Gazette"),
            CutoffEntry("SDM Final Allocation Score", "598 / 1020", CutoffType.HISTORICAL, "Previous Final Selection Roster")
        ),
        syllabusTopics = listOf(
            "Bihar Heritage: Patna Qalam, Mauryan Art, Madhubani Painting, Peasant movements under Swami Sahajanand",
            "Economy & Geography: Economic Survey of Bihar, Agricultural roadmap, Floods in North Bihar & Droughts in South",
            "Statistics: Interpretation of Statistical diagrams, Histograms, Pie charts, Line graphs in GS Paper I"
        )
    ),
    Job(
        id = 8,
        title = "IBPS Probationary Officer (CRP PO/MT-XIV)",
        category = JobCategory.BANK,
        organization = "IBPS",
        level = "Officer Scale I",
        salary = "₹36,000 - ₹63,840 (Gross ₹75,000+)",
        location = "All India (11 Participating PSU Banks)",
        seats = 3049,
        quota = "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%",
        applyUrl = "https://ibps.in/apply",
        noticeUrl = "https://ibps.in/notice",
        officialSiteUrl = "https://ibps.in/",
        state = null,
        minQualification = "Graduate",
        jobOverview = "Common Recruitment Process for Probationary Officers and Management Trainees across 11 nationalized Public Sector Banks (Punjab National Bank, Bank of Baroda, Canara Bank, Union Bank, etc.).",
        locationDetails = "Pan-India posting with regular inter-state transfers depending on participating bank preferences chosen during application.",
        recruitmentExamDetails = "Phase I Prelims (100 Marks CBT) ➔ Phase II Mains (225 Marks Objective + Descriptive) ➔ Phase III Common Interview (100 Marks). Final merit 80:20 weightage between Mains and Interview.",
        examPatternDetails = "Prelims: 100 Qs in 60 mins (20 mins each for English, Quant, Reasoning). Mains: 155 Qs in 180 mins + 30 mins English Descriptive (Letter & Essay 25 Marks). 0.25 negative marking.",
        preparationInfo = "Critical Reasoning, High-Level DI, Banking Regulation Acts, RBI Guidelines, Financial Awareness, and formal business essay writing.",
        seatsAndReservation = "Total 3,049 seats. Category-wise quotas distributed proportionally across all participating banks.",
        cutOffInfo = "Official Prelims Overall UR Cutoff: 54.25 / 100. Mains Overall UR Cutoff: 63.00 / 225. Sectional cutoffs apply in every individual subject.",
        otherInfo = "Candidates must hold an active degree from a recognized University on or before the registration closing date.",
        applicationStartDate = "01 Aug 2026",
        applicationClosingDate = "28 Aug 2026",
        examDate = "19 Oct 2026",
        ageLimit = "20 to 30 Years",
        applicationFee = "₹850 for General/EWS/OBC • ₹175 for SC/ST/PwBD",
        selectionStagesSummary = "Prelims CBT ➔ Mains + Descriptive CBT ➔ Common Bank Interview",
        milestones = listOf(
            TimelineMilestone("CRP Notification Released", "30 Jul 2026", isPassed = true),
            TimelineMilestone("Online Registration Closes", "28 Aug 2026", isPassed = true),
            TimelineMilestone("Prelims Admit Card Link", "08 Oct 2026"),
            TimelineMilestone("Preliminary Examination", "19 Oct 2026", isCrucial = true),
            TimelineMilestone("Mains Examination", "30 Nov 2026", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Unreserved (UR)", 1224, "40.1%"),
            VacancyCategoryQuota("OBC (Non-Creamy)", 823, "27.0%"),
            VacancyCategoryQuota("Scheduled Caste (SC)", 462, "15.1%"),
            VacancyCategoryQuota("Economically Weaker (EWS)", 308, "10.1%"),
            VacancyCategoryQuota("Scheduled Tribe (ST)", 232, "7.6%")
        ),
        examStages = listOf(
            ExamStageInfo("Preliminary CBT", "Online Objective", "60 Mins", 100, 100, "-0.25 Negative", listOf("English Language (30 Qs / 20m)", "Quantitative Aptitude (35 Qs / 20m)", "Reasoning Ability (35 Qs / 20m)")),
            ExamStageInfo("Mains CBT & Descriptive", "Online Multi-Section", "210 Mins", 157, 225, "-0.25 Negative", listOf("Reasoning & Computer (45 Qs / 60 M)", "General Economy & Banking (40 Qs / 40 M)", "English Language (35 Qs / 40 M)", "Data Analysis & Interpretation (35 Qs / 60 M)", "English Letter & Essay (2 Qs / 25 M)"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("UR / General Prelims", "54.25 / 100", CutoffType.OFFICIAL, "IBPS Gazetted"),
            CutoffEntry("OBC (NCL) Prelims", "54.25 / 100", CutoffType.OFFICIAL, "IBPS Gazetted"),
            CutoffEntry("EWS Prelims", "54.25 / 100", CutoffType.OFFICIAL, "IBPS Gazetted"),
            CutoffEntry("SC Prelims", "49.75 / 100", CutoffType.OFFICIAL, "IBPS Gazetted"),
            CutoffEntry("ST Prelims", "44.00 / 100", CutoffType.OFFICIAL, "IBPS Gazetted"),
            CutoffEntry("Mains Overall UR Cutoff", "63.00 / 225", CutoffType.HISTORICAL, "Previous Allotment Minimum")
        ),
        syllabusTopics = listOf(
            "Data Interpretation: Radar charts, Funnel charts, Caselets with quadratic equations, Probability",
            "Reasoning: Direction sense with coding, Machine Input-Output, Coded Inequalities, Data Sufficiency",
            "General Awareness: Banking terminology (Repo, Reverse Repo, CRR, SLR), Priority Sector Lending (PSL)"
        )
    ),
    Job(
        id = 9,
        title = "Indian Navy Agniveer (SSR / MR)",
        category = JobCategory.DEFENSE,
        organization = "Indian Navy",
        level = "Sailor Cadre (Agnipath)",
        salary = "₹30,000 / month (Seva Nidhi ₹11.7L)",
        location = "INS Chilka (Training) ➔ Naval Fleets",
        seats = 1365,
        quota = "State-Wise Merit Roster",
        applyUrl = "https://joinindiannavy.gov.in/apply",
        noticeUrl = "https://joinindiannavy.gov.in/notice",
        officialSiteUrl = "https://joinindiannavy.gov.in/",
        state = null,
        minQualification = "10th Pass",
        jobOverview = "Prestigious naval service under the Agnipath scheme for Senior Secondary Recruit (SSR) and Matric Recruit (MR). Sailors serve on board warships, submarines, naval air stations, and technical establishments.",
        locationDetails = "Initial military conditioning and seafaring training at INS Chilka (Odisha), followed by deployment across Western, Eastern, and Southern Naval Commands.",
        recruitmentExamDetails = "Stage I: Indian Navy Entrance Test (INET Computer Based Examination). Stage II: Physical Fitness Test (PFT), Written Test, and Recruitment Medical Examination at designated Naval centers.",
        examPatternDetails = "INET: 100 Multiple Choice Questions (English, Science, Mathematics, General Awareness) in 60 minutes. 0.25 negative marking.",
        preparationInfo = "Class 10/12 Science & Mathematics (Mechanics, Wave optics, Algebra, Trigonometry), Basic Grammar, and Physical Endurance Running.",
        seatsAndReservation = "Total 1365 vacancies across branches. Up to 25% of Agniveers may be enrolled as permanent regular cadre in the Indian Navy after 4 years based on merit and performance.",
        cutOffInfo = "State-specific domicile merit lists determine written cut-offs. Cutoffs historically range from 34.00 to 52.00 marks depending on domicile state.",
        otherInfo = "Physical standards: 1.6 km run in 6.5 mins, 20 squats (Uthak Baithak), 12 push-ups. Minimum height: Male 157 cm, Female 152 cm.",
        applicationStartDate = "12 May 2026",
        applicationClosingDate = "05 Jun 2026",
        examDate = "14 Jul 2026",
        ageLimit = "17.5 to 21 Years",
        applicationFee = "₹550 + 18% GST for all candidates",
        selectionStagesSummary = "INET Online Exam ➔ Physical Fitness Test (PFT) ➔ Final Medical at INS Chilka",
        milestones = listOf(
            TimelineMilestone("Batch Notification Released", "08 May 2026", isPassed = true),
            TimelineMilestone("Application Portal Closed", "05 Jun 2026", isPassed = true),
            TimelineMilestone("Stage-I INET Examination", "14 Jul 2026", isCrucial = true),
            TimelineMilestone("Stage-II PFT & Medicals", "18 Aug 2026", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Agniveer (SSR) Technical", 1000, "73.3%"),
            VacancyCategoryQuota("Agniveer (MR) Chef/Steward", 365, "26.7%")
        ),
        examStages = listOf(
            ExamStageInfo("Stage-I INET Exam", "Online Computer Based", "60 Mins", 100, 100, "-0.25 Negative", listOf("English (25 Qs)", "Science (25 Qs)", "Mathematics (25 Qs)", "General Awareness (25 Qs)")),
            ExamStageInfo("Stage-II Physical (PFT)", "Ground Athletic Test", "6.5 Mins", 0, 0, "Qualifying", listOf("1.6 Km Run in 6 mins 30 secs", "20 Squats (Uthak Baithak)", "12 Push-ups", "10 Bent Knee Sit-ups"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("UP State Domicile INET", "48.50 / 100", CutoffType.OFFICIAL, "Indian Navy Selection List"),
            CutoffEntry("Bihar State Domicile INET", "49.00 / 100", CutoffType.OFFICIAL, "Indian Navy Selection List"),
            CutoffEntry("Rajasthan Domicile INET", "51.25 / 100", CutoffType.OFFICIAL, "Indian Navy Selection List"),
            CutoffEntry("Maharashtra Domicile INET", "38.50 / 100", CutoffType.OFFICIAL, "Indian Navy Selection List")
        ),
        syllabusTopics = listOf(
            "Science: Physical World & Measurement, Kinematics, Laws of Motion, Work Energy & Power, Electrostatics",
            "Mathematics: Relations & Functions, Complex Numbers, Sequences & Series, Trigonometry, Statistics",
            "General Awareness: Defense Ports & Harbours, Rivers & Mountains, Freedom Movement, National Symbols"
        )
    ),
    Job(
        id = 10,
        title = "RRB Group D (Level-1 Track Maintainer & Helper)",
        category = JobCategory.RAILWAY,
        organization = "Indian Railways",
        level = "Level 1 (7th CPC Pay Matrix)",
        salary = "₹18,000 - ₹56,900",
        location = "All India (Railway Zones & Workshops)",
        seats = 103769,
        quota = "UR: 40%, OBC: 27%, SC: 15%, ST: 7.5%, EWS: 10%",
        applyUrl = null,
        noticeUrl = "https://rrbcdg.gov.in/notice",
        officialSiteUrl = "https://rrbcdg.gov.in/",
        state = null,
        minQualification = "10th Pass",
        jobOverview = "Massive foundational recruitment for Level-1 operating staff in Indian Railways, including Track Maintainer Grade IV, Assistant Pointsman, Assistant Bridge, and Workshop Helpers across 16 Zonal Railways.",
        locationDetails = "Posting across railway divisions, maintenance sheds, yards, and stations nationwide according to applied Railway Recruitment Cell (RRC) zone.",
        recruitmentExamDetails = "Stage 1: Computer Based Test (CBT). Stage 2: Physical Efficiency Test (PET). Stage 3: Document Verification and strict Medical Examination.",
        examPatternDetails = "CBT: 100 Multiple Choice Questions (General Science: 25 Qs, Mathematics: 25 Qs, General Intelligence & Reasoning: 30 Qs, General Awareness & Current Affairs: 20 Qs) in 90 Minutes. 1/3 negative marking.",
        preparationInfo = "Focus on Class 10 NCERT Science (Physics, Chemistry, Biology), basic arithmetic word problems, and non-verbal reasoning.",
        seatsAndReservation = "Total 1,03,769 positions. Specific reservations for Ex-Servicemen (20%), CCAA Course Completed Act Apprentices (20%), and PwBD candidates.",
        cutOffInfo = "Official Normalized CBT Cutoff: UR: 62.00 - 75.00 depending on zone (e.g. Allahabad vs Bangalore), OBC: 58.00 - 68.00, SC: 50.00 - 60.00 marks.",
        otherInfo = "Male PET: Run 1000 meters in 4 mins 15 secs and lift/carry 35 kg weight for 100 meters in 2 minutes without dropping.",
        applicationStartDate = "10 Oct 2026",
        applicationClosingDate = "10 Nov 2026",
        examDate = "15 Feb 2027",
        ageLimit = "18 to 33 Years (Relaxation for SC/ST 5 yrs, OBC 3 yrs)",
        applicationFee = "₹500 (₹400 refunded after CBT) • ₹250 for SC/ST/Women",
        selectionStagesSummary = "CBT Online Test (100 M) ➔ Physical Efficiency Test (PET) ➔ DV & Medical",
        milestones = listOf(
            TimelineMilestone("Centrally Managed Notice", "05 Oct 2026"),
            TimelineMilestone("Online Applications Commence", "10 Oct 2026"),
            TimelineMilestone("Application Deadline", "10 Nov 2026", isCrucial = true),
            TimelineMilestone("CBT Stage 1 Window", "15 Feb 2027", isCrucial = true)
        ),
        categoryQuotas = listOf(
            VacancyCategoryQuota("Unreserved (UR)", 42355, "40.8%"),
            VacancyCategoryQuota("OBC (Non-Creamy)", 27378, "26.4%"),
            VacancyCategoryQuota("Scheduled Caste (SC)", 15559, "15.0%"),
            VacancyCategoryQuota("Economically Weaker (EWS)", 10381, "10.0%"),
            VacancyCategoryQuota("Scheduled Tribe (ST)", 8096, "7.8%")
        ),
        examStages = listOf(
            ExamStageInfo("Computer Based Test (CBT)", "Online Multiple Choice", "90 Mins", 100, 100, "1/3rd Negative (-0.33)", listOf("General Science (25 Qs)", "Mathematics (25 Qs)", "General Intelligence & Reasoning (30 Qs)", "General Awareness (20 Qs)")),
            ExamStageInfo("Physical Efficiency Test", "Track Running & Weight Carrying", "Timed", 0, 0, "Qualifying", listOf("Male: Carry 35 kg for 100m in 2 mins & Run 1000m in 4m 15s", "Female: Carry 20 kg for 100m in 2 mins & Run 1000m in 5m 40s"))
        ),
        cutoffBenchmarks = listOf(
            CutoffEntry("UR Normalized (Northern RRC)", "70.15 / 100", CutoffType.OFFICIAL, "RRB Official"),
            CutoffEntry("OBC Normalized (Northern RRC)", "64.80 / 100", CutoffType.OFFICIAL, "RRB Official"),
            CutoffEntry("SC Normalized (Northern RRC)", "58.20 / 100", CutoffType.OFFICIAL, "RRB Official"),
            CutoffEntry("ST Normalized (Northern RRC)", "52.40 / 100", CutoffType.OFFICIAL, "RRB Official"),
            CutoffEntry("CCAA Act Apprentices Cutoff", "40.00 / 100", CutoffType.OFFICIAL, "Apprentice Statutory Floor")
        ),
        syllabusTopics = listOf(
            "General Science: 10th standard Physics, Chemistry and Life Sciences (NCERT based)",
            "Mathematics: Number system, BODMAS, Decimals, Fractions, LCM, HCF, Ratio and Proportion",
            "Reasoning: Analogies, Alphabetical and Number Series, Coding and Decoding, Mathematical operations"
        )
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

@Immutable
data class CategoryThemeInfo(
    val primaryColor: Color,
    val gradient: Brush,
    val icon: ImageVector
)

fun getThemeInfoForCategory(category: JobCategory): CategoryThemeInfo {
    return when (category) {
        JobCategory.RAILWAY -> CategoryThemeInfo(
            primaryColor = Color(0xFF1B365D),
            gradient = Brush.linearGradient(listOf(Color(0xFF154360), Color(0xFF0E2433))),
            icon = Icons.Default.DirectionsTransit
        )
        JobCategory.BANK -> CategoryThemeInfo(
            primaryColor = Color(0xFF0072BC),
            gradient = Brush.linearGradient(listOf(Color(0xFF0D1B2A), Color(0xFF0A192F))),
            icon = Icons.Default.AccountBalance
        )
        JobCategory.DEFENSE -> CategoryThemeInfo(
            primaryColor = Color(0xFF1B2A38),
            gradient = Brush.linearGradient(listOf(Color(0xFF101920), Color(0xFF090E13))),
            icon = Icons.Default.Security
        )
        JobCategory.STATE -> CategoryThemeInfo(
            primaryColor = Color(0xFFD35400),
            gradient = Brush.linearGradient(listOf(Color(0xFF1F1814), Color(0xFF130E0B))),
            icon = Icons.Default.Map
        )
        JobCategory.CENTRAL -> CategoryThemeInfo(
            primaryColor = Color(0xFF1F3A60),
            gradient = Brush.linearGradient(listOf(Color(0xFF111A24), Color(0xFF0B121A))),
            icon = Icons.Default.Flag
        )
        JobCategory.ALL -> CategoryThemeInfo(
            primaryColor = Color(0xFF2C3E50),
            gradient = Brush.linearGradient(listOf(Color(0xFF151A21), Color(0xFF0D1117))),
            icon = Icons.Default.Work
        )
    }
}
