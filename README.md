# GolfClub

This is a Kotlin Multiplatform project targeting Android, iOS.

# Golf Performance Tracker (Mobile)

## Overview
A mobile app for browsing golf players and inspecting each player's performance. Three screens:
1. **Players** — searchable, filterable list of players.
2. **Player Details** — a selected player's profile with three vertical sections: Averages, a Launch-Angle-vs-Carry-Distance scatter chart, and an inline list of that player's shot records.
3. **Shots** — the full shot-record history for the selected player.

Target implementation: **Kotlin Multiplatform Mobile (KMM)** with **Compose Multiplatform** for shared UI.

## About the Design Files
The file in this bundle (`Golf Performance Tracker.dc.html`) is a **design reference created in HTML** — a working prototype showing the intended look, layout, and navigation, **not production code to copy directly**. The task is to **recreate this design in the KMM / Compose Multiplatform codebase** using its established patterns (Composables, `ViewModel`/state holders, theming via `MaterialTheme`, navigation). Treat the HTML/CSS values below as the source of truth for spacing, color, and type.

## Fidelity
**High-fidelity (hifi).** Final colors, typography, spacing, radii, and interactions are specified. Recreate the UI to match these values. Light and dark themes are both defined.

---

## Design Tokens

### Colors — Light theme
| Token | Hex | Usage |
|---|---|---|
| page | `#FFFFFF` | Screen background, headers |
| card | `#F6F8F8` | Cards, search box, chips, filter pills |
| text | `#1C1B1F` | Primary text |
| text2 | `#5A5560` | Secondary/muted text, labels |
| primary | `#0F766E` | Accent (teal), active states, chart dots, links |
| primary-soft | `#E6F5F2` | Shot-type badge background |
| primary-container | `#CCFBF1` | Avatar circle background |
| on-primary | `#FFFFFF` | Text/icon on primary fill |
| secondary | `#B05E2A` | Shot-type accent (amber/brown) |
| success | `#15803D` | Positive delta (≥ 0 vs avg) |
| warning | `#B45309` | Negative delta (< 0 vs avg) |
| error | `#DC2626` | (reserved) |
| border | `#E6EAEA` | Card borders, dividers, axes |
| shadow | `rgba(15,40,38,.06)` | Card drop shadow |

### Colors — Dark theme
| Token | Hex |
|---|---|
| page | `#121212` |
| card | `#1E1E1E` |
| text | `#FFFBFE` |
| text2 | `#A8A5AE` |
| primary | `#4ECDC1` |
| primary-soft | `#16302D` |
| primary-container | `#0D5F56` |
| on-primary | `#003A37` |
| secondary | `#FFB997` |
| success | `#7EE8C1` |
| warning | `#FFD369` |
| error | `#FF8A65` |
| border | `#2C2C2C` |
| shadow | `rgba(0,0,0,.5)` |

### Typography
Two families:
- **Inter** (400/500/600/700) — UI text, names, headings, numbers.
- **Roboto Mono** (400/500/600) — labels, metric values, timestamps, monospace stats.

Key styles (weight / size / line-height / letter-spacing):
| Role | Style |
|---|---|
| Screen title ("Golf Players") | Inter 700 / 26px / 1.1 / -0.025em |
| Player name (details header) | Inter 700 / 22px / 1.1 / -0.02em |
| Top-bar title (Inter) | Inter 600 / 17px |
| Player name (list row) | Inter 600 / 16px / 1.25 |
| Metric value | Inter 700 / 26px / 1 / -0.02em |
| Section label (e.g. "AVERAGES") | Roboto Mono 600 / 12px / letter-spacing 0.08em, color text2 |
| Metric label | Inter 500 / 12px, color text2 |
| Delta line ("+3 mph vs avg") | Roboto Mono 600 / 11px |
| Shot stat value | Roboto Mono 600 / 13–14px |
| Stat micro-label ("SPEED") | Roboto Mono 500 / 9px / 0.06em |
| Body / secondary | Inter 400 / 13–14px, color text2 |

### Spacing & shape
- Screen horizontal padding: **16px** (some headers 12px).
- Card radius: **16px** (shot cards in inline list use **14px**).
- Pills / chips / badges radius: **999px** (full).
- Search box radius: **12px**.
- Card padding: **14–16px**.
- Inter-card gap: **9–11px**.
- Section top margin: **24px**, label-to-content gap **12px**.
- Card shadow: `0 2px 5px shadow`.
- Avatar circles: 64px (details), 46px (list row), radius 50%.
- Icon buttons (back / overflow): 38px circle.

### Animation
- `fadeUp`: opacity 0→1, translateY 10px→0, 0.45s ease-out. Applied to metric cards.
- `accFill`: left accent bar scaleY 0→1 (origin bottom), 0.6s cubic-bezier(.2,.7,.3,1). On each metric card's 5px-wide left accent stripe.
- Metric cards stagger by index: delay = `index * 90ms`.
- Respect reduced-motion: disable animations when the OS setting is on.
- Device frame: max content width **430px**, centered.

---

## Screens / Views

### 1. Players (list)
**Purpose:** Browse, search, and filter players; tap a player to open details.

**Layout:** Vertical flex, full height. Fixed header + scrolling list.
- **Header** (page bg, bottom border):
  - Row: title "Golf Players" (left) + theme-toggle button (right). Toggle is a pill (Roboto Mono 600 / 12px, border, radius 999px) reading "Dark mode" / "Light mode".
  - **Search box** (margin-top 14px): card bg, border, radius 12px, padding 11×13. Magnifier SVG (16px, stroke text2) + text input placeholder "Search players…". Filters list by name (case-insensitive, substring).
  - **Club filter chips** (horizontal scroll, margin-top 10px, gap 8px): chips for `Driver`, `Fairway Woods`, `Irons`, `Wedges`. Multi-select toggle. Selected = primary bg / on-primary text / primary border; unselected = card bg / text2 / border. Filter matches if a player's `preferenceClub` contains any selected chip (substring).
- **List** (scroll, padding 14×16×28, gap 11px): one button per matched player:
  - Avatar circle 46px (primary-container bg, primary text, first initial, Inter 700 18px).
  - Name (Inter 600 16px), club (Inter 400 13px text2), avg line (Roboto Mono 600 12px primary): `"Avg speed {averageBallSpeed} mph · {averageDistance} yd"`.
  - Right-aligned last-shot line (Roboto Mono 500 11px text2): `"{lastShot} ago"`, top-aligned.
- **Empty state:** when no players match → centered "No players match your search." (Inter 500 14px text2, padding 48×16).

### 2. Player Details (profile)
**Purpose:** Review a player's averages, distribution chart, and recent shots.

**Layout:** Fixed top bar + scrolling content (padding 20×16, bottom 100px).
- **Top bar:** back chevron (38px circle) + player name (Inter 600 17px) + overflow "⋯" button.
- **Profile header** (bottom border, padding-bottom 20px): 64px avatar + name (Inter 700 22px) + club (Inter 400 14px text2) + joined line (Roboto Mono 500 11px text2, e.g. "Member since Jun 2026").
- **Section 1 — AVERAGES** (label "AVERAGES"): **2×2 grid**, gap 11px, of metric cards. Each card:
  - 5px primary accent stripe on the left edge (animated `accFill`).
  - Label (Inter 500 12px text2), value (Inter 700 26px) + unit (Roboto Mono 500 12px text2), delta line.
  - Four metrics in order: **Average Ball Speed** (mph), **Average Launch Angle** (°, 1 decimal), **Average Carry Distance** (yd), **Average Spin Rate** (RPM, thousands-separated).
  - Delta line: `"{+/−}{abs} {unit} vs avg"`; color = success when delta ≥ 0, warning when < 0. (− is the U+2212 minus sign.)
- **Section 2 — LAUNCH ANGLE vs CARRY DISTANCE** (label): a card containing an SVG scatter plot. X = launch angle (°, domain ~10–44), Y = carry distance (yd, domain ~70–270). Gridlines at carry 100/160/220; X ticks at 15°/25°/35°/42°; one dot per shot (r=6, primary fill 82% opacity, 2px card-colored stroke). Below: axis captions "← launch angle (°)" / "carry (yd) ↑".
- **Section 3 — SHOT RECORDS** (label row): label `"SHOT RECORDS · {n} shot(s)"` + "See all ›" link (primary) → navigates to Shots screen. Below: vertical list (gap 9px) of compact shot cards. Each card (radius 14px) is tappable → Shots screen, and shows:
  - Row: club name (Inter 600 13px) + shot-type badge (Roboto Mono 600 10px, secondary color, primary-soft bg, pill).
  - Wrapped stat group (gap 6×16): SPEED / LAUNCH / CARRY / SPIN micro-labels with values.
  - Footer row: `"SHOT #{n}"` + time (Roboto Mono 500 11px text2).

### 3. Shots (full history)
**Purpose:** Full detail of every shot for the selected player.

**Layout:** Top bar (back + "{name} · Shots") + filter row + scrolling list.
- **Filter row** (bottom border): two display-only pills — "All clubs ▾" and "Recent ↓". (Static in the prototype; wire to real sort/filter in the build.)
- **List** (padding 16×16, bottom 100px): per shot — a label "SHOT #{n} · {club}" (Roboto Mono 600 12px primary) above a card (radius 16px) of label/value rows separated by dividers: Ball Speed, Launch Angle, Carry Distance, Spin Rate, Shot Type (secondary color), Time (text2). Each row: padding 11px vertical, label Inter 400 13px text2, value Roboto Mono 600 14px.

---

## Interactions & Behavior
- **Navigation:** Players → (tap player) → Player Details → (tap "See all" or any shot card) → Shots. Back chevrons return one level (Shots → Details, Details → Players).
- **Search:** live substring match on player name, case-insensitive.
- **Club filter:** multi-select chips; empty selection = show all; otherwise match if `preferenceClub` contains any selected club (substring, case-insensitive). Combined with search via AND.
- **Theme toggle:** flips light/dark; affects all token colors.
- **Reduced motion:** all animations disabled when OS reduce-motion is on.

## State Management
Suggested shared state (e.g. a `ViewModel` / `StateHolder`):
- `screen`: enum `Players | Profile | Shots` (current route).
- `selectedPlayerId`: String.
- `darkTheme`: Boolean.
- `query`: String (search text).
- `clubFilter`: Set<String> (selected club chips).

Derived: filtered player list, the selected player's metric cards, shot list, and chart points are all computed from the above + the source data. Navigation is just `screen` transitions.

## Data Model

The current domain layer implements a simplified scaffold of the full design-target model. As the UI is built out, these models will expand toward the richer shape documented in `SPEC.md` (with `averageMetrics`, `joined`, `lastShot`, and per-shot `type`/`time` fields).

**Current implementation** (domain models in `:domain/src/commonMain/kotlin/com/mujapps/domain/models/`):

```kotlin
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

**Data flow:** Ktor fetches DTOs from the API (`GolfPlayerDto`, `GolfShotDto` in `data/src/commonMain/kotlin/com/mujapps/data/remote/models/NetworkModels.kt`), mappers convert them to domain models, and repositories expose them via `suspend` functions wrapped in `Flow` by use cases.

**Formatting rules** (for display, match the prototype):
- Launch Angle value: 1 decimal + "°".
- Spin Rate: rounded, thousands separators (e.g. "2,650").
- Delta sign uses U+2212 (−) for negatives.

See `CLAUDE.md` **Data Layer & Mappers** section for mapper details.

## Assets
No bitmap assets. All icons are inline SVG strokes (search magnifier, chevrons). Avatars are generated from the player's first initial. Replace inline SVGs with your icon set / `Icons.*` equivalents.

## Files
- `Golf Performance Tracker.dc.html` — the full interactive prototype (all three screens, light/dark themes, seed data, chart logic). Open in a browser to interact; read the source for exact values.
- `SPEC.md` — detailed design spec including type scale, spacing rules, animations, data model, and implementation checklist.
- `CLAUDE.md` — codebase architecture, module structure, build commands, and technical documentation.

## Project Structure

The project follows a clean-architecture multi-module structure:

| Module | Responsibility |
|--------|---------------|
| `:androidApp` | Android entry point; initializes Koin DI and launches the shared UI. |
| `:iosApp` | iOS entry point; minimal wrapper for the shared KMP framework. |
| `:shared` | Compose Multiplatform UI, the `App()` composable, iOS `MainViewController` factory. |
| `:presentation` | ViewModels, UI state holders, navigation logic (under development). |
| `:domain` | Business rules, data model classes (`GolfPlayer`, `GolfShot`, etc.), repository interfaces, and use cases. |
| `:data` | Repository implementations, Ktor networking, local persistence (TODO: Room entities/DAOs). |

For full module architecture details and build/test commands, see `CLAUDE.md` **Module Architecture** and **Build & Run** sections.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

## Kotlin Multiplatform (KMP) Architecture

The project leverages Kotlin Multiplatform (KMP) to share code across Android and iOS platforms. By modularizing the project, we achieve clear separation of concerns and maximum code reuse:

*   **Shared Business & Data Layer**: 
    *   `:domain`: Exposes pure Kotlin business models, repositories (interfaces), and use cases.
    *   `:data`: Handles Ktor networking, Room local persistence database, and repository implementations.
    *   `:presentation`: ViewModels and UI state definitions.
*   **Shared UI Layer (`:shared`)**: Houses Compose Multiplatform code (Composables, theming, visual components) used directly by both platforms.
*   **Platform Wrappers**: 
    *   `:androidApp`: Initializes Koin DI with `androidContext` and boots the shared UI (`App()`).
    *   `:iosApp`: Minimally wraps the shared iOS framework (`MainViewController.kt`) via Swift/Xcode.

---

## Offline-First Functionality

The application follows an **offline-first pattern** utilizing the local Room database as the single source of truth for UI content:

1.  **Reactive Observation**: The UI (via ViewModels and Use Cases) reactively listens to Flow streams exposed by the Room database.
2.  **Unidirectional Data Flow**: 
    *   When the user opens a player's profile, the ViewModel issues a background sync trigger (`getUserDetails() -> syncPlayerShots()`).
    *   The sync job executes on `Dispatchers.IO`, requesting fresh records from `RemoteDataSource` via Ktor.
    *   Upon receiving a successful API response, the repository deletes stale cache and writes new records to the Room database.
    *   Since the UI is observing Room, it immediately receives the database updates and renders the new data.
3.  **Graceful Degradation**: If the network request fails (e.g., connection timed out or offline), the ViewModel catches the exception to present a descriptive error toast/banner, while the user continues to see the previously cached data from the Room database.

---

## Unit Testing & Coverage

We maintain high-fidelity unit tests across our core architecture layers targeting **mobile platforms** (Android and iOS). The tests are written in pure Kotlin and reside in `commonTest` source sets, ensuring they validate business logic across both target platforms.

### Test Coverage Areas

1.  **`:domain` (Business Logic)**
    *   Unit tests for Use Cases (`GetAllPlayersUseCaseTest`, `GetPlayerDetailsShotsUseCaseTest`).
    *   Validates correct delegation of data, error handling, and reactive flow emissions using **Turbine**.
    *   Uses a `MockGolfRepository` to stub repository responses.
2.  **`:presentation` (UI State & ViewModels)**
    *   Unit tests for ViewModels (`PlayerListingViewModelTest`, `PlayerDetailsViewModelTest`).
    *   Uses `kotlinx-coroutines-test` with `UnconfinedTestDispatcher` to intercept and test `viewModelScope` calls.
    *   Asserts flow progression of UI state (e.g., initial state -> loading state -> database data emission).
    *   Asserts API sync failures write correct error details to `mDetailsUiState`.
3.  **`:data` (Data Mappers & Network Clients)**
    *   **Mappers** (`MappersTest`): Tests mapping conversions between API DTOs, Local Database Entities, and Domain Models (`DtoMappers`, `EntityMappers`).
    *   **RemoteDataSource** (`RemoteDataSourceTest`): Tests HTTP network requests utilizing Ktor's `MockEngine` (asserting HTTP 200 parses DTOs correctly and HTTP error status codes produce a failure `Result`).

### Running Tests

Run the following Gradle commands from your terminal:

*   **Run all tests in all modules**:
    ```bash
    ./gradlew testAndroidHostTest
    ```
*   **Run Domain tests**:
    ```bash
    ./gradlew :domain:testAndroidHostTest
    ```
*   **Run Presentation tests**:
    ```bash
    ./gradlew :presentation:testAndroidHostTest
    ```
*   **Run Data tests**:
    ```bash
    ./gradlew :data:testAndroidHostTest
    ```

---

## Logging

The project uses **Napier** for structured, cross-platform logging. All logging is automatically initialized at app startup and routes to:
- **Android:** `android.util.Log` (visible in Logcat)
- **iOS:** `NSLog` / os-log (visible in Xcode Console)

Developers can call `Napier.d()`, `Napier.e()`, `Napier.w()` directly from shared code (`commonMain`). HTTP client requests and network errors are automatically logged. For details, see the **Logging** section in `CLAUDE.md`.

## Data Layer

The `:data` module handles networking via Ktor and converts API responses (DTOs) to domain models using mapper functions:

- **DTOs** (`NetworkModels.kt`): `@Serializable` classes matching the API response shape.
- **Mappers** (`DtoMappers.kt`): Extension functions (`GolfPlayerDto.toDomain()`, `GolfShotDto.toDomain()`, etc.) convert DTOs to domain models.
- **RemoteDataSource** (`RemoteDataSource.kt`): Fetches data from the API and wraps responses in `Result<T>`, logging errors via Napier.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…