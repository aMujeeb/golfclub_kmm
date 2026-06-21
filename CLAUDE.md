# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**GolfGarage** — a Golf Performance Tracker built with Kotlin Multiplatform Mobile (KMM) and Compose Multiplatform, targeting Android and iOS. The full UI spec is in `SPEC.md`; the interactive HTML prototype (`Golf Performance Tracker.dc.html`) is the source of truth for pixel-level fidelity.

Three screens:
1. **Players** — searchable, filterable list of golf players
2. **Player Details** — profile with averages grid, scatter chart (launch angle vs carry distance), and a "View Shots History" button
3. **Shots** — full shot history for a selected player, with a club-filter dropdown

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

# Generate a code coverage report for a module (Kover)
./gradlew :domain:koverHtmlReport       # report at domain/build/reports/kover/html/index.html
./gradlew :data:koverHtmlReport
./gradlew :presentation:koverHtmlReport
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
- **Koin** 4.2.1 — dependency injection; `koin-core`, `koin-compose`, `koin-compose-viewmodel`, and `koin-compose-navigation3` are all wired and used in `:shared` and `:presentation`
- **Ktor** 3.5.0 — HTTP client (OkHttp on Android, Darwin on iOS)
- **Room** 2.8.4 — local persistence (KMP Room); fully implemented as the offline source of truth. Entities (`PlayerEntity`, `GolfShotEntity`), DAOs (`PlayerDao`, `ShotDao`), and `GolfDatabase` live in `data/src/commonMain/kotlin/com/mujapps/data/local/`; platform-specific database builders (`DatabaseModule.android.kt` / `DatabaseModule.ios.kt`) live in `:shared`
- **Paging** 3.4.0 (`androidx-paging`) — drives the players list (`GolferPagingSource` in `:data`, consumed via `collectAsLazyPagingItems` in `:shared`)
- **Coil** 3.4.0 — async image loading, wired through Ktor (`KtorNetworkFetcherFactory`) for cross-platform network fetches; Eva Icons (`compose-icons` eva-icons 1.1.1) supplies placeholder/error avatar fallbacks
- **Navigation** (nav3) 1.1.1 — Compose Multiplatform navigation
- **Napier** 2.7.1 — Kotlin Multiplatform structured logging (logs to Android Log, iOS NSLog, etc. automatically)
- **kotlinx.coroutines** 1.11.0, **kotlinx.serialization** 1.11.0, **kotlinx.datetime** 0.8.0
- **Kover** 0.7.6 (`org.jetbrains.kotlinx.kover`) — code coverage for unit tests; applied to the root project and to `:domain`, `:data`, `:presentation` (the pure-logic modules with `commonTest` suites). **Not** applied to `:shared`/`:androidApp` (mostly Compose UI, no coverage target yet). No custom `kover { }` block exists yet — reports/thresholds use plugin defaults. Kover instruments JVM bytecode only, so coverage is measured from each module's `testAndroidHostTest` task, not the iOS simulator tests.

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
    dataModule,         // from :data — HTTP client, repositories
    domainModule,       // from :domain — use cases
    presentationModule, // from :presentation — ViewModels
    databaseModule      // from :shared — Room database instance (expect/actual per platform)
)
```

`presentationModule` (`presentation/src/commonMain/kotlin/com/mujapps/presentation/di/PresentationModule.kt`) registers `PlayerListingViewModel` and `PlayerDetailsViewModel` via Koin's `viewModel { }` DSL. `databaseModule` (`shared/src/commonMain/kotlin/com/mujapps/golfgarage/di/DatabaseModule.kt`, with `DatabaseModule.android.kt` / `DatabaseModule.ios.kt` actuals) supplies the `GolfDatabase` instance.

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
fun GolfPlayer.toDto(): GolfPlayerDto     // Reverse: domain → DTO (for serialization)
fun GolfShot.toDto(): GolfShotDto         // Reverse: domain → DTO (for serialization)
```

Reverse mappers (domain → DTO) are used when sending data; forward mappers (DTO → domain) are used after receiving data from the API or database.

### Local Persistence (Room)

`data/src/commonMain/kotlin/com/mujapps/data/local/` contains:
- `entities/` — `PlayerEntity`, `GolfShotEntity` (FK to `PlayerEntity`, CASCADE delete), and a Room-specific `PlayerWithShots` relation class (distinct from the domain `PlayerWithShots` below)
- `dao/` — `PlayerDao` (`getAllPlayersOffline()`, `getPlayerWithShots(id): Flow<PlayerWithShots?>`, `insertPlayers()`), `ShotDao` (`insertShots()`, `deleteShotsForPlayer()`)
- `database/GolfDatabase.kt` — the `@Database` class

DTO ↔ entity mappers live in `mappers/EntityMappers.kt`, alongside `DtoMappers.kt`. The app is offline-first: API responses sync into Room, and Room is the UI's source of truth (repositories read from Room, not directly from the network).

**Sync failures must propagate.** `GolfersRepository.syncPlayerShots(playerId)` calls `response.getOrThrow()` on the `RemoteDataSource` result — it logs failures via Napier but always rethrows. This is intentional: `PlayerDetailsViewModel.getUserDetails()` wraps the call in a try/catch specifically to set `DetailsUiState.mErrorMessage` when sync fails (e.g. offline with nothing cached yet for that player), so the UI can show a "shot data unavailable offline" message instead of silently rendering empty/`"--"` state. Don't swallow this exception inside the repository — the ViewModel layer owns turning it into user-facing error state, while Room (read independently via `observePlayerShots`) remains the source of truth for whatever *is* cached.

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

**Note:** The spec in `SPEC.md` / `README.md` defines a richer data model (`Player`, `Shot`, `Metric` with `averageMetrics`, `lastShot`, `joined`, shot `type`/`time` fields, etc.). The current implementation is a simplified starting point; mappers are wired to convert the network DTOs to these current domain models. As the UI is built out, the domain models may be expanded to match the full spec. Don't confuse this domain `PlayerWithShots` with the Room-specific `PlayerWithShots` in `data/.../local/entities/`.

`IGolferRepository.getPlayersPagingFlow(query: String? = null, clubs: List<String> = emptyList())` and `GetAllPlayersUseCase.executePaging(query, clubs)` both take optional search/club-filter params now, threaded down to `GolferPagingSource`/`PlayerDao` (see **Navigation & State** above for how the UI wires these).

## Navigation & State

Navigation uses AndroidX **Navigation3** (`androidx.navigation3.runtime`), not a hand-rolled sealed `Route`/`AppState`:
- Routes are `NavRoutes` (`@Serializable sealed interface NavRoutes : NavKey`) in `shared/src/commonMain/kotlin/com/mujapps/golfgarage/navigation/NavRoutes.kt`, with three cases: `Listing` (object), `Details(val mPlayerId: String)`, and `Shots(val mPlayerId: String)`.
- `GolfersNavRoute(isDarkTheme: () -> Boolean, onToggleDarkTheme: () -> Unit)` (`shared/.../navigation/GolfersNavRoute.kt`) owns a `rememberNavBackStack` and wraps everything in a single `Scaffold`. The `Scaffold`'s `topBar` is route-aware: it switches on `mBackStack.lastOrNull()` and renders `PlayersListHeader` for `NavRoutes.Listing` or `PlayerDetailsHeader` for both `NavRoutes.Details` and `NavRoutes.Shots` (both header composables in `ui/components/PlayersListHeader.kt`). The `NavDisplay`/`NavEntry` content renders below, padded by the Scaffold's `innerPadding`.
- **Collapsing toolbar on Player Details**: `GolfersNavRoute` owns `topBarTitle`, `collapseProgress: Float`, and `profilePicUrl: String?` state, fed into `PlayerDetailsHeader(title, collapseProgress, profilePicUrl, ...)`. `GolfPlayerDetailsView` accepts `listState: LazyListState`, `onScrollStateChanged: (Float) -> Unit`, and `onProfileImageLoaded: (String?) -> Unit` callbacks; as the `LazyColumn` scrolls past the profile card, it reports a 0f–1f progress value (based on `firstVisibleItemScrollOffset` / first item size) that `PlayerDetailsHeader` uses to cross-fade in the player's name/avatar into the top bar. `NavRoutes.Shots` always renders the header fully collapsed (`collapseProgress = 1f`, no avatar).
- `PlayerListingViewModel` is hoisted **once** at the `GolfersNavRoute` level via `koinViewModel()` and shared between the topBar (search box + filter chips) and `GolfersListView`'s content (passed in as `mViewModel`), rather than each screen creating its own instance.
- There is no single `AppState`. Each screen owns its own UI state class instead: `ListingUiState`/`PlayerListingViewModel` and `DetailsUiState`/`PlayerDetailsViewModel` (under `presentation/src/commonMain/kotlin/com/mujapps/presentation/features/`).
- **Search & club filter are implemented** in `PlayerListingViewModel`: `searchQuery: StateFlow<String>` and `selectedClubs: StateFlow<Set<String>>`, updated via `onSearchQueryChanged(query)` / `onClubFilterToggled(club)`. Both are `combine`d, debounced (300ms) on the query, and `flatMapLatest`'d into `mGetAllPlayersUseCase.executePaging(query, clubs)` — filtering triggers a new paging load rather than filtering an already-loaded client-side list. `GolferPagingSource` (`:data`) passes `search` through to the API and falls back to `PlayerDao.searchPlayersOffline()` when offline; club matching (`preferenceClub.contains(club, ignoreCase = true)`, OR-combined across selected clubs) is applied client-side on each loaded page, both online and offline.
- **Club filter chip labels are placeholder/seed data**, not the design spec's clubs: `PlayersListHeader.kt` hardcodes `listOf("Dynamites", "Pirates", "Dinosours", "Gladiators", "Drunken Warriors")` — these match the actual API's seeded `preferenceClub` values, not `SPEC.md`'s `Driver`/`Fairway Woods`/`Irons`/`Wedges`.
- Dark theme: `App.kt` owns `var darkTheme by remember { mutableStateOf(false) }` at the root and passes it down as `isDarkTheme = { darkTheme }` / `onToggleDarkTheme = { darkTheme = !darkTheme }` — lambdas, not raw values, so the read is deferred to wherever it's actually consumed. `ThemeModeToggleButton` (`ui/components/ThemeModeToggleButton.kt`) renders a pill reading "Dark Mode" / "Light Mode" and is wired into both `PlayersListHeader` and `PlayerDetailsHeader`, so toggling affects the single root `GolfGarageTheme` instance — the whole app, not a per-screen theme.

## Theme & Design Tokens

Defined in `:shared` (`ui/theme/Color.kt`, `ui/theme/Theme.kt`) — implemented via Material 3 `lightColorScheme` / `darkColorScheme`. Key mappings:
- `surface` → card color (`#F6F8F8` light / `#1E1E1E` dark)
- `primary` → teal accent (`#0F766E` light / `#4ECDC1` dark)
- `secondary` → amber/bronze for shot-type badges
- `success`/`warning`/`primarySoft`/`shadow` — M3's `ColorScheme` has no slot for these, so they're exposed via a custom `ExtendedColors` data class + `ExtendedTheme.colors` CompositionLocal accessor (`ui/theme/ExtendedColors.kt`), provided by `GolfGarageTheme` alongside `MaterialTheme`. Consume as `ExtendedTheme.colors.success` / `.warning` / `.primarySoft` / `.shadow` (e.g. `PlayerRowItem`'s card shadow uses `ExtendedTheme.colors.shadow`).

Typography uses real **Inter** and **Roboto Mono** TTFs loaded as Compose Multiplatform resources (`shared/src/commonMain/composeResources/font/`). `Theme.kt` exposes a `@Composable fun golfGarageTypography(): Typography` (not a static `val`, since `Font(Res.font.*, ...)` needs a resource-aware context) that builds `FontFamily`s from the font resources and matches `SPEC.md`'s numeric type scale (sizes/line-heights/letter-spacing) exactly.

## Implementation Status (screens)

- **Players list** (`GolfersListView.kt` + `PlayersListHeader.kt`) — functional: paginated via Paging 3 with loading/error/append states, a debounced search box (name substring match), multi-select club filter chips, and the shared theme-toggle pill in the header. `PlayerRowItem.kt` renders each row as a themed card (Coil `AsyncImage` avatar with Eva Icons placeholder/error fallback, name/club/avg-speed-and-distance line) — the design's right-aligned "last shot" timestamp is intentionally skipped. Average speed/distance are displayed with relabeled units ("Km/h", "m") using the raw API values — no unit conversion math is applied.
- **Player Details** (`GolfPlayerDetailsView.kt`) — fully built: profile card (Coil avatar, name, club), a 2×2 `MetricCard` grid (Average Ball Speed/Launch Angle/Carry Distance/Spin Rate) with delta-vs-all-players-average text computed from `mDetailsUiState.mAllShots`, a `ScatterPlot` (launch angle vs carry distance), and a "View Shots History" button that navigates to `NavRoutes.Shots`. Drives the collapsing toolbar described above. If a player has no cached shots and the background sync failed (e.g. offline and never synced before), a warning card reading "Shot data unavailable offline..." is shown instead of silently rendering all-`"--"` metric cards — see `mDetailsUiState.mErrorMessage` / **Data Layer & Mappers** note below.
- **Shots** (`GolfPlayerShotsView.kt` + `PlayerShotDetailsViewModel`) — functional: a club-filter dropdown ("All clubs ▾") and a `LazyColumn` of `ShotCard`s. Shots are numbered by ascending chronological order (stable `SHOT #n` labels) but displayed newest-first; club filtering is done in-memory via `StateFlow` combiners (no extra DB/network calls). See **Performance & Caching Tuning** in `README.md`.
- Reusable components: `PlayerRowItem`, `PlayersListHeader`/`PlayerDetailsHeader` (collapsing-toolbar aware), `ThemeModeToggleButton`, `MetricCard`, `ScatterPlot`, `ShotCard` — all of `SPEC.md`'s component list now exists except a dedicated `AvatarCircle` (avatars are inlined via Coil `AsyncImage` + Eva Icons fallback wherever needed).

## iOS Integration

The shared KMP framework is compiled as a static framework named `Shared` (from `:shared`). `iosApp/iosApp/iOSApp.swift` is the iOS entry point; `ContentView.swift` wraps `MainViewController` from the shared module. The `iosMain` source set in `:shared` contains the `MainViewController.kt` factory.

## Formatting Rules (from SPEC.md)

| Value | Format |
|-------|--------|
| Spin Rate | Thousands-separated + " RPM" (e.g. "2,650 RPM") |
| Launch Angle | 1 decimal + "°" (e.g. "18.5°") |
| Delta negative | U+2212 minus sign (−), not hyphen |
| Relative time | Natural language "2m", "15m", "1h ago" |
