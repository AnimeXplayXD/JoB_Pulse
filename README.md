<div align="center">

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="docs/assets/jobpulse_hero_banner.jpg">
  <img alt="JobPulse — recruitment information for Android" src="docs/assets/jobpulse_hero_banner.jpg" width="100%" style="border-radius: 16px; margin-bottom: 24px;">
</picture>

<br/>

<img src="docs/assets/jobpulse_logo.svg" alt="JobPulse wordmark" width="340px" />

<br/>
<br/>

**Recruitment information, thoughtfully organized.**  
*A native Android experience for exploring opportunities, saving jobs, and understanding what comes next.*

[![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Status](https://img.shields.io/badge/Status-In%20development-8E8E93?style=for-the-badge)](#current-status)

[Overview](#overview) · [Experience](#the-experience) · [Architecture](#architecture) · [Data accuracy](#data-accuracy) · [Build](#build-and-run) · [Testing](#testing) · [Roadmap](#roadmap)

</div>

---

## Overview

JobPulse is a Kotlin and Jetpack Compose Android app for browsing recruitment opportunities across Indian public-sector organizations. It brings job summaries, eligibility information, dates, source links, and preparation context into a single interface.

The design favors readable typography, calm surfaces, expandable cards, and restrained glass effects over dense dashboard styling. Room provides a local recruitment cache; bookmarks and preferences are stored separately on the device.

## Current status

**The Android client is in development, not a verified production recruitment service.**

| Area | Current implementation |
|---|---|
| Browsing | Home, search, announcements, full job details, and local preferences |
| Data | Debug builds use labeled development samples by default |
| Release builds | The default production data source is deliberately unconfigured; the sample announcement feed is excluded |
| Saved state | Persistent local bookmarks, theme, and notification interests; saveable card previews and tab content state |
| Notifications | Android permission handling and notification channels; automatic delivery is not connected |
| Verification | Source metadata and conservative mappings; no production ingestion/review service yet |
| Validation | Regression tests are present; the experience upgrade still requires build, device, accessibility, and performance validation |

A release build must be connected to a real data service before it can deliver current recruitment notices. There is no automatic production endpoint configuration through `.env` today.

## The experience

### Two-stage job cards

1. **Tap a card** to expand an inline preview of the deadline, qualification, age, and reservation information.
2. Choose **View full details** to open the full-screen view with a shared-container transition.
3. Tap the card again to collapse the preview. The bookmark button acts independently.

There is no double-tap requirement. Expanded preview state is saveable, and returning from details restores the originating browsing context.

### Glass and motion

- **Floating dock:** dragging the indicator changes the destination before release. Fixed-width slots, boundary hysteresis, and right-to-left coordinate handling help keep selection predictable.
- **Backdrop glass:** on Android 12+ with hardware acceleration and a recorded backdrop, the dock draws a GPU-layer blur of the scene behind it. Older or unsupported configurations use a translucent fallback. Foreground labels and icons are not blurred.
- **Theme reveal:** a one-shot graphics-layer snapshot preserves the old appearance while the new theme expands from the theme control. The entire palette does not crossfade at the same time. Disabled system animations use an immediate theme change.
- **Browsing performance:** cards avoid decorative shadows and gradient stacks; search filtering runs off the main thread and is cancellable. These are implementation choices, not measured frame-rate guarantees.

### Details that support decisions

The full-screen view organizes available information into overview, eligibility, application dates, selection stages, preparation, reservation, cutoffs, and other conditions. Source links and provider-reported freshness metadata appear alongside the listing.

Preparation suggestions are distinguished from official exam requirements. Historical cutoffs and estimates are not predictions. **Share a correction report** opens Android's share chooser; it does not submit to a dedicated correction-review backend.

### Notifications without duplicate prompts

A dismissible explanation replaces the old first-open app modal. Choosing to enable notifications requests Android's system permission where applicable, or opens settings when necessary.

Permission, channel availability, saved interests, and actual alert delivery are different states. Enabling permission does **not** activate a recruitment subscription. The current channel IDs are `recruitment_alerts` and `exam_milestones`.

## Architecture

```mermaid
flowchart TD
    Demo["Debug-only development samples"] --> Remote["RemoteJobDataSource"]
    Backend["Production ingestion and review — pending"] -.-> API["Retrofit API adapter — requires configuration"]
    API -.-> Remote
    Remote --> Repository["OfflineFirstJobRepository"]
    Repository <--> Cache[("Room recruitment cache")]
    Cache --> Mapping["JobMappers"]
    Mapping --> VM["MainViewModel / StateFlow"]
    Preferences[("Local user preferences") ] <--> VM
    VM --> UI["Compose screens and shared components"]
    Preferences <--> Settings["Preferences screen"]
```

| Location | Responsibility |
|---|---|
| `app/src/main/java/com/example/MainActivity.kt` | App shell, navigation, permissions, and backdrop wiring |
| `app/src/main/java/com/example/MainViewModel.kt` | Recruitment state, filters, bookmarks, and refresh coordination |
| `app/src/main/java/com/example/data/` | Room entities/DAOs, remote adapters, mappings, repository, and local preferences |
| `app/src/main/java/com/example/ui/screens/` | Home, search, announcements, details, and preferences |
| `app/src/main/java/com/example/ui/components/` | Expandable cards, dock, and glass rendering |
| `app/src/main/java/com/example/theme/` | Circular theme-reveal controller and compositing |
| `app/src/main/java/com/example/ui/theme/` | Colors, presentation tokens, and typography |

User preferences currently use `SharedPreferences`, independently of the recruitment cache. DataStore migration, richer DTO persistence, and tested Room schema migrations remain follow-up work.

## Data accuracy

**Missing information must not become plausible-looking facts.** The DTO/cache mapping now keeps missing qualifications, ages, fees, dates, and selection stages as unavailable information rather than inventing them. Unknown source classification is not marked official, and unknown application status maps conservatively to draft rather than open.

Important boundaries:

- `Not announced` currently also represents missing fields in the dataset; it is not proof that an authority has confirmed a future announcement.
- An official-source flag or provider-reported check timestamp is not independent verification.
- Cached information can miss corrections, deadline extensions, or cancellations.
- Development samples are not suitable for application decisions.
- Legacy model defaults and some rich-data persistence gaps still need cleanup; production ingestion must not rely on fixture defaults.

Before production release, the project needs source-document retention, field-level evidence, validation, review of ambiguous information, and a correction/revision workflow. Candidates should always check the latest authority notice before applying or paying fees.

## Technology stack

The build files are the source of truth for versions:

| Component | Configuration |
|---|---|
| Kotlin | `2.2.10` |
| Jetpack Compose | BOM `2024.09.00`, Material 3 |
| Android | `minSdk 24`, `targetSdk 36`, compile SDK `36.1` |
| Room | `2.7.0`, KSP |
| Networking | Retrofit `2.12.0`, OkHttp `4.10.0` |
| Serialization | Moshi `1.15.2`, generated adapters and Kotlin adapter support |
| Build | Gradle wrapper `9.3.1`, Android Gradle Plugin `9.1.1`, Java toolchain `17` |
| Testing | JUnit `4.13.2`, Robolectric `4.16.1`, Roborazzi `1.59.0`, Compose UI tests |

See [the version catalog](gradle/libs.versions.toml) and [app build configuration](app/build.gradle.kts).

## Build and run

Use Android Studio compatible with the configured Android Gradle Plugin, the Java 17 toolchain, and the Android SDK platform required by compile SDK `36.1`. Configure the SDK location through Android Studio or your local SDK settings. Use the repository's Gradle wrapper rather than a separately installed Gradle version.

```bash
git clone https://github.com/AnimeXplayXD/JoB_Pulse.git
cd JoB_Pulse

# On macOS/Linux, if the wrapper is not executable:
chmod +x gradlew

./gradlew assembleDebug
./gradlew testDebugUnitTest
```

On Windows, use `.\gradlew.bat` instead of `./gradlew`.

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.aistudio.govtjobs.pxrtwa/com.example.MainActivity
```

The debug build uses development data by default. `.env.example` supplies defaults for the configured properties plugin; adding `.env` does not connect production ingestion or notification delivery. Release signing also requires the keystore and environment configuration defined in `app/build.gradle.kts`.

## Testing

The platform-independent validation entry point is [`scripts/validate-android.sh`](scripts/validate-android.sh). On macOS/Linux, run it from a configured development environment:

```bash
bash scripts/validate-android.sh
```

It runs unit/Robolectric tests, lint, debug APK assembly, release Kotlin compilation, and instrumented-test APK assembly. It does not sign a production release or execute device tests. On Windows, the equivalent command is:

```powershell
.\gradlew.bat --no-daemon --stacktrace --continue testDebugUnitTest lintDebug assembleDebug compileReleaseKotlin assembleDebugAndroidTest
```

For individual checks:

```bash
# Unit, Robolectric, and configured Compose tests
./gradlew testDebugUnitTest

# Android lint
./gradlew lintDebug

# Instrumented tests: requires a connected device or emulator
./gradlew connectedDebugAndroidTest
```

Selected tests cover:

- `JobCardInteractionTest`: preview/details separation, independent bookmarks, and preview restoration.
- `DockSelectionTest`: selection boundaries, hysteresis, and out-of-bounds input.
- `DeadlineBoundaryTest`: date-only deadlines around calendar-day boundaries.
- `EmptyRepositoryTest`: empty caches are not replaced with sample listings.
- `JobMappingTest`: conservative missing-data behavior and explicit-field preservation.
- `MainViewModelTest`, `JobRepositoryTest`, and `ThemeRevealTest`: state, repository, and reveal behavior.
- `BrandIdentityTest`: drawable loading, app naming, notification channels, and monochrome geometry.

The [JobPulse validation workflow](.github/workflows/android-build-test.yml) calls the same script on pushes to `main`, pull requests targeting `main`, or manual dispatch. It uploads available HTML/XML reports and debug APKs, including when validation fails. An uploaded APK alone is not a release-readiness signal; check the validation step and reports for the exact commit. The workflow has not been verified on the destination repository by this change.

Before release, validate interrupted gestures, rapid tab switching, large text, TalkBack, offline/empty states, denied permissions, launcher masks, and light/dark appearance. Measure startup and scrolling on physical devices, including high-refresh-rate and lower-end hardware; smoothness has not been established by screenshots or token-value tests.

## Brand assets

- **Launcher:** JP is the dominant letterform, with a smaller separate pulse underneath and a glare-free tonal background.
- **Adaptive icon:** foreground/background vectors referenced by `mipmap-anydpi-v26` resources.
- **Themed icon:** white-only monochrome geometry for supporting launchers.
- **Older Android versions:** vector-based `mipmap-anydpi` launcher resources.
- **Splash:** a brief pulse animation configured for Android 12+. Launcher-controlled tap/close icon animations are not implemented or guaranteed.
- **Marketing:** the banner, SVG wordmark, and JPG icon concept remain in [`docs/assets`](docs/assets). They are concept artwork and may differ from the current installed icon; they are not screenshots or performance evidence.

## Roadmap

### Release prerequisites
- [ ] Compile and validate the experience upgrade across supported Android versions.
- [ ] Measure startup, scrolling, blur, and transition frame timing; add performance baselines.
- [ ] Complete accessibility, large-text, state-restoration, and notification-denial testing.
- [ ] Connect production recruitment ingestion with evidence, review, and corrections.
- [ ] Preserve richer API fields through the cache and add tested database migrations.
- [ ] Remove remaining legacy fixture defaults from production-facing models.
- [ ] Connect notification delivery, background refresh, and job-specific notification navigation.

### Further refinement
- [ ] Migrate local preferences to DataStore.
- [ ] Add paging as recruitment volume grows.
- [ ] Add source-backed preparation resources and optional study planning.
- [ ] Introduce personalized alerts and explainable eligibility assistance only when reliable data is available.

## Disclaimer

JobPulse is an independent recruitment-information application. It is not affiliated with, authorized by, or endorsed by any government department or recruiting organization represented in the app. Its interface and branding do not establish the authenticity of a listing.

Verify recruitment details, eligibility, dates, application procedures, and fees against the latest official notification and authority portal.

## License

**To be determined.** No open-source license grant is stated here.

## Project links

- [Source repository](https://github.com/AnimeXplayXD/JoB_Pulse)
- [Project wiki](https://github.com/AnimeXplayXD/JoB_Pulse/wiki) — supplementary material; consult the current code and build files for implementation details.
