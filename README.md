# JobPulse

> A modern recruitment-information app for discovering and tracking important job and recruitment opportunities.

---

## Overview

**JobPulse** is a native Android application engineered to make recruitment information across India easier to discover, understand, and track. Aspirants and job seekers often struggle with fragmented gazette notifications, opaque timelines, convoluted selection pipelines, and dispersed cut-off data. JobPulse synthesizes this critical data into an accessible, structured, and visually engaging experience.

The application is architected around **remotely updateable recruitment data** using an offline-first synchronization model. The Android client operates purely as a secure consumer of structured, validated recruitment data. It does **not** directly scrape government portals or third-party websites, ensuring fast response times, consistent data validation, and predictable battery and network usage.

---

## Current Status

The application features a complete native Android implementation:

- **Modern Architecture**: Offline-first repository pattern backed by Room persistence, unidirectional data flow, and Jetpack ViewModel.
- **Declarative UI**: Built 100% with Jetpack Compose using Material 3 design principles.
- **Adaptive Organization Branding**: Dynamic branding engine rendering authentic identities for major employers (SBI, Indian Railways, SSC, UPSC, State Police, Defense, State PSCs) while preserving JobPulse brand continuity.
- **Liquid-Glass UI System**: Custom translucent glass floating navigation dock with touch-drag spring physics, specular highlight rims, and adaptive light/dark mode styling.
- **Interactive Recruitment Cards**: Tactile touch feedback, single-tap inline expansion (deadlines, quota breakdown, eligibility summary, and quick CTAs), and double-tap shared-element container transformations into full details.
- **Publication-Grade Detail Screen**: Comprehensive breakdown of vacancies, eligibility criteria, multi-stage exam patterns, syllabus topics, official vs. historical cut-offs, and important dates.
- **Search & Filtering**: Real-time multi-attribute search across post titles, recruiting bodies, categories, and qualification tags.
- **Offline Cache & Synchronization**: Local SQLite storage via Room with reactive Kotlin `Flow` updates and delta synchronization (`updated_since` query parameters).
- **Notification Infrastructure**: Android notification channels (`JobPulse Alerts` and `JobPulse Milestones`) with contextual Android 13+ runtime permission management.
- **Predictable Navigation**: System Back gesture support with hierarchical state dismissal (detail view $\rightarrow$ previous tab $\rightarrow$ home $\rightarrow$ exit).
- **Adaptive Icon System**: Full-color adaptive launcher icon (Deep Indian Indigo `#0B1B3D` and Saffron `#F4A261`), dedicated single-color monochrome layer for Android 13+ Material You dynamic theming, and safe-zone geometry compliant with realme UI / OEM squircle masks.
- **Automated Test Suite**: Unit, Robolectric, and Roborazzi screenshot test coverage.

---

## Technology Stack

| Area | Technology | Version / Specification |
|---|---|---|
| **Language** | Kotlin | 2.2.10 |
| **UI Toolkit** | Jetpack Compose | Material 3 / Compose BOM 2024.09.00 |
| **Architecture** | Offline-First Repository + ViewModel | Modern Android Architecture (MVI/MVVM) |
| **Local Database** | Room (SQLite) | 2.7.0 with Kotlin Symbol Processing (KSP) |
| **Concurrency & Streams** | Kotlin Coroutines & Flow | 1.10.2 |
| **Networking** | Retrofit & OkHttp | Retrofit 2.12.0 / OkHttp 4.10.0 |
| **JSON Serialization** | Moshi Kotlin | 1.15.2 (Code generation) |
| **Image Loading** | Coil Compose | 2.7.0 |
| **Testing** | JUnit 4, AndroidX Test, Robolectric | Robolectric 4.16.1 |
| **Visual Testing** | Roborazzi | 1.59.0 |
| **Build System** | Gradle (Kotlin DSL) | Gradle 9.1.1 / AGP 9.1.1 |
| **Target Platform** | Android | minSdk 24 (Android 7.0), targetSdk 36 (Android 16) |

---

## Architecture

JobPulse enforces a strict separation of concerns between client display and data sourcing:

```text
Official Recruitment Sources (Gazettes, Portals, Notifications)
                       ↓
            Server-side Ingestion
                       ↓
     Structured / Verified Recruitment Data
                       ↓
                 Versioned API
                       ↓
          Android Remote Data Source
                       ↓
            Offline-First Repository
                       ↓
                 Room / SQLite
                       ↓
                   ViewModel
                       ↓
              Jetpack Compose UI
```

The Android client connects to a versioned API endpoint and maps remote DTOs into strongly typed local database entities. Local storage acts as the single source of truth for the presentation layer. The client never attempts direct web scraping, preserving device efficiency, user privacy, and network resilience.

---

## Recruitment Data Model

Recruitment records are structured with comprehensive, normalized metadata fields:

- **Identification**: Unique identifier, recruitment slug, internal reference codes.
- **Organisation**: Organisation name, authority type, jurisdiction, and branding tokens.
- **Position Overview**: Post title, department, cadre, employment type (Permanent / Contractual), total vacancies.
- **Eligibility & Limits**: Educational requirements, minimum/maximum age limits, age relaxation rules.
- **Financial Details**: Pay scale, basic pay, grade pay, and application fee categories.
- **Important Dates**: Notification date, application open/close dates, fee deadline, admit card release, exam dates, result dates.
- **Examination Schema**: Examination stages, mode (CBT / OMR / Interview), duration, question count, negative marking scheme, and syllabus topics.
- **Quota & Cut-offs**: Category reservation breakdown (UR, OBC, SC, ST, EWS, PwBD) and historical/gazetted cut-off scores.
- **Official References**: Application portal URL, official gazette PDF download link, source verification URL, publication timestamp, and recruitment status (`ACTIVE`, `UPCOMING`, `CLOSED`, `EXPIRED`).

### Data Integrity Rule
Unknown or pending information is never fabricated. When official notifications have not yet released specific details (e.g., examination dates or admit card links), the system represents them with semantic states:
- `Not announced`
- `Not available`
- `Not applicable`

---

## Official Source Model

Production recruitment information is curated from official recruitment authorities and published gazette notices, including:

- Union Public Service Commission (UPSC)
- Staff Selection Commission (SSC)
- Railway Recruitment Boards (RRB)
- State Bank of India (SBI) & Institute of Banking Personnel Selection (IBPS)
- State Public Service Commissions (e.g., UPPSC, BPSC, MPSC)
- State Police Recruitment Boards

> **Notice**: JobPulse is an independent recruitment discovery platform and is **not** operated by, affiliated with, or endorsed by these authorities. Official recruitment documents are credited to their respective publishing bodies.

---

## Organisation Branding

Recruitment opportunities in JobPulse feature organisation-specific visual identities to ensure immediate recognizability for aspirants:

- **State Bank of India (SBI)**: Authentic SBI Blue (`#0072BC`) with banking badge accents.
- **Indian Railways / RRB**: Deep iron navy (`#1B365D`) with warm gold brass accents (`#C59B27`).
- **Staff Selection Commission (SSC)**: National administrative slate and deep navy (`#1F3A60`).
- **Union Public Service Commission (UPSC)**: Prestigious deep navy and imperial gold seal styling (`#16253D`, `#D4AF37`).
- **State Police**: Khaki patrol navy and crimson chevron badge accents.
- **Defense Services**: Maritime navy and tactical insignia styling.

Organisation branding is strictly isolated to individual recruitment cards and detail headers. **JobPulse** remains the product brand, maintaining an independent design identity that never implies official government ownership.

---

## Offline-First Architecture

JobPulse is built to function reliably in low-connectivity or offline scenarios:

```text
Open application
      ↓
Read cached Room data
      ↓
Display available recruitment records instantly
      ↓
Attempt background synchronization
      ↓
Persist delta changes to local database
      ↓
UI automatically updates via reactive Kotlin Flow
```

If network connectivity is unavailable or synchronization fails, the application continues to display previously cached recruitment records without errors or disruptions.

---

## Synchronization

The application synchronizes recruitment data via a versioned REST API contract:

```http
GET /api/v1/jobs?updated_since=<timestamp>
GET /api/v1/organisations
```

- **Delta Synchronization**: Transmits only records modified or created since the previous local synchronization timestamp.
- **Entity Deletions**: Detects expired or retracted notices and reconciles the local SQLite database.
- **Cache Consistency**: Atomic database transactions guarantee data integrity across sync intervals.

*(Note: During current development and staging phases, the application utilizes the structured `RemoteJobDataSource` fixture to simulate the versioned REST contract).*

---

## Security

JobPulse adheres to Android security best practices:

- **Zero Privileged Secrets in Client**: The Android application does **not** embed database master credentials, service-role keys, backend administrative tokens, or private ingestion credentials.
- **Secure Transport**: Network communication requires encrypted HTTPS connections.
- **Strict Permission Handling**: Requests only runtime permissions strictly necessary for user-facing functionality (e.g., `POST_NOTIFICATIONS` on Android 13+).

---

## Development Data

Development fixtures (`RemoteJobDataSource`) provide representative recruitment notices across categories to facilitate testing of UI components, transitions, and database queries. Development records are explicitly tagged as fixtures and must not be confused with verified live gazettes.

---

## UI / UX Design Principles

- **Clear Information Hierarchy**: Bold metadata tags, structured milestone timelines, and scannable requirement lists.
- **Spatial Continuity**: Shared-element transitions (`SharedTransitionLayout`) seamlessly expand cards into full-screen details and contract them back upon return.
- **Tactile Feedback**: Touch scale reactions (`0.982f`) with spring release physics.
- **Liquid-Glass Navigation Dock**: Floating bottom dock with touch-drag sliding, active pill morphing, and auto-hiding behavior on scroll.
- **Balanced Color Systems**: Obsidian dark mode (`#0D1117`) and warm-paper light mode (`#F6F8FA`) calibrated for reading comfort and contrast accessibility.

---

## Notifications

JobPulse includes native Android notification infrastructure configured for recruitment tracking:

- **Channels**:
  - `JobPulse Alerts` (`jobpulse_alerts`): Urgent notices such as deadline reminders and sudden schedule changes.
  - `JobPulse Milestones` (`jobpulse_milestones`): Application opening announcements and admit card releases.
- **Runtime Permissions**: Android 13+ (API 33+) notification permissions are requested contextually rather than aggressively on initial launch.

---

## Navigation Hierarchy

JobPulse maintains a predictable back-stack flow:

```text
Job Detail Screen
       ↓ (Back gesture / button)
Previous Tab (Search / Feed / Account / Home)
       ↓ (Back gesture / button)
Home Screen
       ↓ (Back gesture)
Exit Application
```

Inline card expansions and transient UI states are dismissed prior to navigating away from the active screen.

---

## Testing & Verification

The project includes an automated test suite verifying business logic, database queries, DTO mapping, and brand identity:

```powershell
# Run unit, Robolectric, and integration tests
.\gradlew.bat testDebugUnitTest

# Assemble and verify debug APK package
.\gradlew.bat assembleDebug
```

CI workflows (`.github/workflows/android-build-test.yml`) run these verification steps automatically on pushes and pull requests to `main`.

---

## Project Structure

```text
app/
└── src/
    ├── main/
    │   ├── java/com/example/
    │   │   ├── data/
    │   │   │   ├── local/          # Room database, DAOs, and database entities
    │   │   │   ├── remote/         # Retrofit API service, DTOs, RemoteJobDataSource
    │   │   │   └── repository/     # OfflineFirstJobRepository, JobMappers
    │   │   ├── model/              # Domain models (Job, Organisation, Category)
    │   │   ├── ui/
    │   │   │   ├── components/     # GlassyDock, JobCardItem, JobPulseBrand
    │   │   │   ├── screens/        # HomeScreen, JobDetailScreen, SearchScreen, FeedScreen, AccountScreen
    │   │   │   └── theme/          # Color, Theme, Type, ThemeTokens, OrgBranding
    │   │   ├── util/               # NotificationHelper
    │   │   ├── JobPulseApp.kt      # Application class
    │   │   └── MainActivity.kt     # Root Activity, navigation host, shared transitions
    │   ├── res/                    # Drawables, mipmaps, adaptive/monochrome icons, strings
    │   └── AndroidManifest.xml
    └── test/                       # Unit and Robolectric tests
docs/
└── walkthrough.md                  # Detailed implementation and verification walkthrough
```

---

## Roadmap

### Implemented
- [x] Jetpack Compose UI with liquid-glass dock and tactile job cards
- [x] Double-tap shared-element container transform into publication-grade detail screen
- [x] Organisation-specific visual branding engine
- [x] Room database persistence with reactive offline-first repository
- [x] Versioned remote API contracts and DTO mapping layer
- [x] Android 13+ Material You dynamic themed icon & realme-safe adaptive icon system
- [x] Hierarchical system Back navigation
- [x] Notification channel setup with runtime permission scaffolding
- [x] Automated test suite and GitHub Actions CI workflow

### Future Work
- [ ] Production cloud API and PostgreSQL backend deployment
- [ ] Automated server-side gazette ingestion and verification pipeline
- [ ] Push notification dispatch service via Firebase Cloud Messaging (FCM)
- [ ] Background delta sync using Android WorkManager
- [ ] Expansion of supported state public service commissions and regional recruiting bodies

---

## Disclaimer

> JobPulse is an independent recruitment-information application.
>
> Official recruitment information belongs to the respective recruiting organisations. Users should verify important eligibility requirements, dates, vacancies, fees, and application instructions against the relevant official notification before applying.
>
> JobPulse does not represent or impersonate any government organisation, recruiting authority, bank, railway organisation, commission, or employer.

---

## License

> License information will be added when the project's distribution and contribution policy is finalized.
