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
- **Koin** 4.2.2 — dependency injection (`koin-core`, `koin-compose`, `koin-compose-viewmodel`)
- **Ktor** 3.5.0 — HTTP client (OkHttp on Android, Darwin on iOS)
- **Room** 2.8.4 — local persistence (KMP Room); use KSP for annotation processing
- **Coil** 3.4.0 — async image loading
- **Navigation** (nav3) 1.1.1 — Compose Multiplatform navigation
- **kotlinx.coroutines** 1.11.0, **kotlinx.serialization** 1.11.0, **kotlinx.datetime** 0.8.0

## Data Model

Defined in `:domain` (see `SPEC.md` §Data Model for full detail):

```kotlin
data class Player(id, name, profPicUrl, preferenceClub, averageBallSpeed, averageDistance,
                  averageMetrics: List<Metric>, lastShot, joined)
data class Metric(value: Double, deltaVsAvg: Double)  // order: [Speed, Angle, Distance, Spin]
data class Shot(n, club, ballSpeed, launchAngle, carryDistance, spinRate, type, time)
```

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
