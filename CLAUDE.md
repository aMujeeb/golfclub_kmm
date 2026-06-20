# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**GolfGarage** — a Golf Performance Tracker built with Kotlin Multiplatform Mobile (KMM) and Compose Multiplatform, targeting Android and iOS. The full UI spec is in `SPEC.md`; the interactive HTML prototype (`Golf Performance Tracker.dc.html`) is the source of truth for pixel-level fidelity.

Three screens:
1. **Players** — searchable, filterable list of golf players
2. **Player Details** — profile with averages grid, scatter chart (launch angle vs carry distance), and inline shot records
3. **Shots** — full shot history for a selected player

## Build & Run

```bash
# Build Android debug APK
./gradlew :androidApp:assembleDebug

# Run all Android host unit tests
./gradlew :shared:testAndroidHostTest

# Run iOS simulator tests
./gradlew :shared:iosSimulatorArm64Test

# Run tests for a specific module
./gradlew :domain:testAndroidHostTest
./gradlew :data:testAndroidHostTest
./gradlew :presentation:testAndroidHostTest
```

iOS: open `iosApp/` in Xcode and run from there.

## Module Architecture

The project uses a clean-architecture multi-module structure, all modules support KMP (Android + iOS targets):

| Module | Package | Responsibility |
|--------|---------|---------------|
| `:androidApp` | `com.mujapps.golfgarage` | Android entry point; thin shell that calls into `:shared` |
| `:shared` | `com.mujapps.golfgarage` | Compose Multiplatform UI, `App()` composable, iOS `MainViewController` |
| `:presentation` | `com.mujapps.presentation` | ViewModels, UI state holders, navigation logic |
| `:domain` | `com.mujapps.domain` | Business rules, data model classes (`Player`, `Shot`, `Metric`), repository interfaces |
| `:data` | `com.mujapps.data` | Repository implementations, Ktor networking, Room local DB |

Dependency flow: `:androidApp` → `:shared` → `:presentation` → `:domain` ← `:data`

## Key Dependencies (from `gradle/libs.versions.toml`)

- **Compose Multiplatform** 1.11.1 — shared UI across Android and iOS
- **Material 3** 1.11.0-alpha07 (`compose-material3`) — theme, elevation, components
- **Koin** 4.2.2 — dependency injection; currently wired for `koin-core` only (`koin-compose` and `koin-compose-viewmodel` not yet added)
- **Ktor** 3.5.0 — HTTP client (OkHttp on Android, Darwin on iOS)
- **Room** 2.8.4 — local persistence (KMP Room); Gradle plugin and KSP wired, but no `@Entity` / `@Dao` / `@Database` code yet (currently 100% network-backed)
- **Coil** 3.4.0 — async image loading
- **Navigation** (nav3) 1.1.1 — Compose Multiplatform navigation
- **Napier** 2.7.1 — Kotlin Multiplatform structured logging (logs to Android Log, iOS NSLog, etc. automatically)
- **kotlinx.coroutines** 1.11.0, **kotlinx.serialization** 1.11.0, **kotlinx.datetime** 0.8.0

## Logging

**Napier** (`io.github.aakira:napier:2.7.1`) is used for structured, cross-platform logging. It's initialized automatically in `App()` (shared/src/commonMain/kotlin/com/mujapps/golfgarage/App.kt):

```kotlin
remember {
    Napier.base(DebugAntilog())  // Called once at app startup (Android & iOS)
}
```

### Usage

Call Napier directly from anywhere in `commonMain` code (e.g., data layer, presentation layer):

```kotlin
import io.github.aakira.napier.Napier

Napier.d("Debug message")           // Debug
Napier.i("Info message")            // Info
Napier.w("Warning message")         // Warning
Napier.e("Error message", ex)       // Error with throwable
Napier.d(tag = "HttpClient", message = "request sent")  // With tag
```

Napier automatically routes to:
- **Android:** `android.util.Log` (visible in Logcat)
- **iOS:** `NSLog` / os-log (visible in Xcode Console)

All HTTP client logs are tagged `HttpClient` and routed through Napier via the Ktor client's `Logging` plugin (configured in `data/src/commonMain/kotlin/com/mujapps/data/di/DataModule.kt`). Network errors are logged before being returned as `Result.failure(ex)`.

## Dependency Injection

Koin is the service locator for the project. It is started in `androidApp/src/main/kotlin/com/mujapps/golfgarage/MainActivity.kt` via `startKoin { }` during app initialization:

```kotlin
startKoin {
    androidLogger()
    androidContext(this@MainActivity)
    modules(appModule)
}
```

The root Koin module (`appModule`) is defined in `shared/src/commonMain/kotlin/com/mujapps/golfgarage/di/AppModule.kt` and currently combines:

```kotlin
val appModule = listOf(
    dataModule,     // from :data — HTTP client, repositories
    domainModule    // from :domain — use cases
)
```

**Note:** There is currently no `presentationModule` (ViewModels and state holders are not yet injected via Koin). As the presentation layer is built out, a `presentationModule` will be added and included here, along with `koin-compose` and `koin-compose-viewmodel` dependencies.

### Scoping Convention

- **`single { }`** — Creates one shared instance for the app lifetime. Use for stateful, expensive-to-construct dependencies: `HttpClient` (connection pooling), repositories (hold data sources), data sources (hold client reference).
- **`factory { }`** — Creates a new instance per injection. Use for cheap, stateless dependencies: use cases (just wrap a repository call), mappers, or ViewModel factories.

Current modules follow this pattern:
- `dataModule`: `single<HttpClient>`, `single<RemoteDataSource>`, `single<IGolferRepository>` (implemented by `GolfersRepository`)
- `domainModule`: `factory` for `GetAllPlayersUseCase` and `GetPlayerDetailsShotsUseCase` (both stateless wrappers around the repository)

## Data Layer & Mappers

The `:data` module contains networking, persistence, and repository implementations.

### DTOs (Network Models)

Defined in `data/src/commonMain/kotlin/com/mujapps/data/remote/models/NetworkModels.kt`. These are `@Serializable` Kotlin Data Classes used by Ktor to deserialize API responses:

```kotlin
@Serializable
data class GolfPlayerDto(
    val id: String,
    val name: String,
    val profPicUrl: String,
    val preferenceClub: String,
    val averageBallSpeed: Double,
    val averageDistance: Double,
    val shots: List<GolfShotDto>? = null
)

@Serializable
data class GolfShotDto(
    val id: String,
    val playerId: String,
    val clubName: String,
    val ballSpeed: Double,
    val launchAngle: Double,
    val carryDistance: Double,
    val spinRate: Double,
    val createdAt: String  // ISO 8601 timestamp
)
```

### Mappers (DTO → Domain)

Conversion functions from DTOs to domain models are defined in `data/src/commonMain/kotlin/com/mujapps/data/mappers/DtoMappers.kt` as extension functions:

```kotlin
fun GolfPlayerDto.toDomain(): GolfPlayer  // Single player, no shots
fun GolfShotDto.toDomain(): GolfShot      // ISO timestamp → Instant
fun GolfPlayerDto.toPlayerWithShots(): PlayerWithShots  // Player + shots aggregate
fun GolfPlayer.toDto(): GolfPlayerDto     // Reverse: domain → DTO (for serialization)
```

Reverse mappers (domain → DTO) are used when sending data; forward mappers (DTO → domain) are used after receiving data from the API or database.

## Domain Models

Defined in `:domain/src/commonMain/kotlin/com/mujapps/domain/models/`. The current implementation is a simplified scaffold:

```kotlin
// Current actual domain models:
data class GolfPlayer(
    val mId: String,
    val mName: String,
    val mProfPicUrl: String,
    val mPreferenceClub: String,
    val mAverageBallSpeed: Double,
    val mAverageDistance: Double
)

data class GolfShot(
    val mId: String,
    val mPlayerId: String,
    val mClubName: String,
    val mBallSpeed: Double,
    val mLaunchAngle: Double,
    val mCarryDistance: Double,
    val mSpinRate: Double,
    val mTimestamp: Instant  // kotlin.time.Instant
)

data class PlayerWithShots(
    val mPlayer: GolfPlayer,
    val mShots: List<GolfShot>
)
```

**Note:** The spec in `SPEC.md` / `README.md` defines a richer data model (`Player`, `Shot`, `Metric` with `averageMetrics`, `lastShot`, `joined`, shot `type`/`time` fields, etc.). The current implementation is a simplified starting point; mappers are wired to convert the network DTOs to these current domain models. As the UI is built out, the domain models may be expanded to match the full spec.

## Navigation & State

Use a sealed `Route` class for navigation (Players / PlayerDetail / Shots) and a single `AppState` (route, selectedPlayerId, darkTheme, searchQuery, clubFilter) as the state holder. Filter logic is AND-combined: search is case-insensitive substring on `Player.name`; club filter is multi-select, matches if `Player.preferenceClub` contains any selected chip substring.

## Theme & Design Tokens

Defined in `:shared` — implement via Material 3 `lightColorScheme` / `darkColorScheme`. Key mappings:
- `surface` → card color (`#F6F8F8` light / `#1E1E1E` dark)
- `primary` → teal accent (`#0F766E` light / `#4ECDC1` dark)
- `secondary` → amber/bronze for shot-type badges
- `success`/`warning` colors for delta lines (not in M3 defaults — add as custom tokens)

Typography uses two families: **Inter** for all UI text and **Roboto Mono** for labels, metric values, and timestamps. See `SPEC.md` §Typography for the full type scale mapped to M3 roles.

## iOS Integration

The shared KMP framework is compiled as a static framework named `Shared` (from `:shared`). `iosApp/iosApp/iOSApp.swift` is the iOS entry point; `ContentView.swift` wraps `MainViewController` from the shared module. The `iosMain` source set in `:shared` contains the `MainViewController.kt` factory.

## Formatting Rules (from SPEC.md)

| Value | Format |
|-------|--------|
| Spin Rate | Thousands-separated + " RPM" (e.g. "2,650 RPM") |
| Launch Angle | 1 decimal + "°" (e.g. "18.5°") |
| Delta negative | U+2212 minus sign (−), not hyphen |
| Relative time | Natural language "2m", "15m", "1h ago" |
