# Golf Performance Tracker — Implementation Specification

**Platform:** Kotlin Multiplatform Mobile (KMM) + Compose Multiplatform  
**Target:** High-fidelity recreation of design prototype  
**Version:** 1.0  
**Last Updated:** June 2026

---

## Table of Contents
1. [Design Tokens](#design-tokens)
2. [Typography](#typography)
3. [Spacing & Layout](#spacing--layout)
4. [Components](#components)
5. [Screens](#screens)
6. [Animations](#animations)
7. [Navigation & State](#navigation--state)
8. [Data Model](#data-model)

---

## Design Tokens

### Color Palette — Light Theme

| Token | Hex | Usage | CSS/Compose |
|-------|-----|-------|-------------|
| `page` | `#FFFFFF` | Screen background, headers | `Color(0xFFFFFFFF)` |
| `card` | `#F6F8F8` | Cards, search box, chips, filter pills | `Color(0xFFF6F8F8)` |
| `text` | `#1C1B1F` | Primary text | `Color(0xFF1C1B1F)` |
| `text2` | `#5A5560` | Secondary/muted text, labels | `Color(0xFF5A5560)` |
| `primary` | `#0F766E` | Accent (teal), active states, chart dots, links | `Color(0xFF0F766E)` |
| `primary-soft` | `#E6F5F2` | Shot-type badge background | `Color(0xFFE6F5F2)` |
| `primary-container` | `#CCFBF1` | Avatar circle background | `Color(0xFFCCFBF1)` |
| `on-primary` | `#FFFFFF` | Text/icon on primary fill | `Color(0xFFFFFFFF)` |
| `secondary` | `#B05E2A` | Shot-type accent (amber/brown) | `Color(0xFFB05E2A)` |
| `success` | `#15803D` | Positive delta (≥ 0 vs avg) | `Color(0xFF15803D)` |
| `warning` | `#B45309` | Negative delta (< 0 vs avg) | `Color(0xFFB45309)` |
| `error` | `#DC2626` | (reserved) | `Color(0xFFDC2626)` |
| `border` | `#E6EAEA` | Card borders, dividers, axes | `Color(0xFFE6EAEA)` |
| `shadow` | `rgba(15,40,38,.06)` | Card drop shadow | `Color(0x0F280620)` (approx) |

### Color Palette — Dark Theme

| Token | Hex | Usage | CSS/Compose |
|-------|-----|-------|-------------|
| `page` | `#121212` | Screen background | `Color(0xFF121212)` |
| `card` | `#1E1E1E` | Cards, search box | `Color(0xFF1E1E1E)` |
| `text` | `#FFFBFE` | Primary text | `Color(0xFFFFFBFE)` |
| `text2` | `#A8A5AE` | Secondary/muted text, labels | `Color(0xFFA8A5AE)` |
| `primary` | `#4ECDC1` | Accent (teal), active states, chart dots | `Color(0xFF4ECDC1)` |
| `primary-soft` | `#16302D` | Shot-type badge background | `Color(0xFF16302D)` |
| `primary-container` | `#0D5F56` | Avatar circle background | `Color(0xFF0D5F56)` |
| `on-primary` | `#003A37` | Text/icon on primary fill | `Color(0xFF003A37)` |
| `secondary` | `#FFB997` | Shot-type accent (light bronze) | `Color(0xFFFFB997)` |
| `success` | `#7EE8C1` | Positive delta | `Color(0xFF7EE8C1)` |
| `warning` | `#FFD369` | Negative delta | `Color(0xFFFFD369)` |
| `error` | `#FF8A65` | Error states | `Color(0xFFFF8A65)` |
| `border` | `#2C2C2C` | Card borders, dividers | `Color(0xFF2C2C2C)` |
| `shadow` | `rgba(0,0,0,.5)` | Card drop shadow | `Color(0xFF000000)` (80% opacity) |

### Theme Implementation

**Compose:**
```kotlin
val LightColors = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF003A37),
    secondary = Color(0xFFB05E2A),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color(0xFFFFFFFF),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFF6F8F8),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF5A5560),
    onSurfaceVariant = Color(0xFF5A5560),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF4ECDC1),
    onPrimary = Color(0xFF003A37),
    primaryContainer = Color(0xFF0D5F56),
    onPrimaryContainer = Color(0xFFA2F1E7),
    secondary = Color(0xFFFFB997),
    onSecondary = Color(0xFF6A3821),
    tertiary = Color(0xFFFFB1C6),
    onTertiary = Color(0xFF46263B),
    error = Color(0xFFFF8A65),
    onError = Color(0xFF000000),
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFBFE),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFFFFBFE),
    surfaceVariant = Color(0xFFA8A5AE),
    onSurfaceVariant = Color(0xFFA8A5AE),
)
```

---

## Typography

### Type Families
- **Inter** (weights: 400, 500, 600, 700) — UI text, names, headings, numbers
- **Roboto Mono** (weights: 400, 500, 600) — labels, metric values, timestamps, monospace stats

### Type Scale & Styles

| Role | Font | Weight | Size | Line Height | Letter Spacing | Usage |
|------|------|--------|------|-------------|----------------|-------|
| Screen Title | Inter | 700 | 26px | 1.1 | -0.025em | "Golf Players" |
| Player Name (Details Header) | Inter | 700 | 22px | 1.1 | -0.02em | Player profile header |
| Top-bar Title | Inter | 600 | 17px | 1.2 | — | Navigation bar headings |
| Player Name (List Row) | Inter | 600 | 16px | 1.25 | — | Player card in list |
| Metric Value | Inter | 700 | 26px | 1 | -0.02em | Large stat numbers (speed, distance) |
| Section Label | Roboto Mono | 600 | 12px | 1.2 | 0.08em | "AVERAGES", "SHOT RECORDS" |
| Metric Label | Inter | 500 | 12px | 1.3 | — | "Average Ball Speed" |
| Delta Line | Roboto Mono | 600 | 11px | 1.2 | — | "+3 mph vs avg" |
| Shot Stat Value | Roboto Mono | 600 | 13–14px | 1.2 | — | Shot detail values |
| Stat Micro-label | Roboto Mono | 500 | 9px | 1 | 0.06em | "SPEED", "ANGLE", "CARRY" |
| Body / Secondary | Inter | 400 | 13–14px | 1.5 | — | Descriptive text, secondary info |
| Badge / Chip Text | Roboto Mono | 600 | 10px | 1 | — | Shot-type badges, club filters |

### Typography Composables (Compose)

```kotlin
val Typography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 28.6.sp,
        letterSpacing = (-0.025).em
    ),
    headlineLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 24.2.sp,
        letterSpacing = (-0.02).em
    ),
    headlineMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 20.4.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = robotoMonoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 14.4.sp,
        letterSpacing = 0.08.em
    ),
    labelMedium = TextStyle(
        fontFamily = robotoMonoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 13.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = robotoMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        lineHeight = 9.sp,
        letterSpacing = 0.06.em
    ),
)
```

---

## Spacing & Layout

### Grid System
- **Base unit:** 4px (dp in Compose)
- **Scale:** 4, 8, 12, 16, 20, 24, 28, 32, 40, 48, 56, 64, 72, 80...

### Padding & Margins

| Element | Value | Notes |
|---------|-------|-------|
| Screen horizontal padding | 16dp | Default edge spacing |
| Header horizontal padding | 12dp (optional) | For compact headers |
| Card padding | 14–16dp | Interior card spacing |
| Section top margin | 24dp | Between major sections |
| Section label to content | 12dp | Gap between section header and content |
| Inter-card vertical gap | 11dp | Between card elements |
| Chip/filter horizontal gap | 8dp | Between filter pills |
| Search box padding | 11dp (v) × 13dp (h) | Search input interior |
| List bottom padding | 100dp | Scroll-end safety margin |
| Avatar circle | 46dp (list) / 64dp (detail) | Size, radius 50% |

### Border Radius

| Element | Value |
|---------|-------|
| Card | 16dp |
| Shot card (inline) | 14dp |
| Search box | 12dp |
| Pills / chips / badges | 999dp (fully round) |
| Icon buttons | 38dp circle (38dp diameter) |

### Shadows

| Element | Value |
|---------|-------|
| Card shadow | `0 2px 5px shadow` |
| Elevation | `elevation(8.dp)` for cards (Compose Material 3) |

### Device Frame & Content Width
- **Max content width:** 430dp (centered)
- **Device frame:** Standard mobile viewport

---

## Components

### Search Box
```
┌─────────────────────────────────┐
│ 🔍 Search players…             │
└─────────────────────────────────┘
```
- **Background:** card color
- **Padding:** 11dp vertical × 13dp horizontal
- **Border radius:** 12dp
- **Border:** 1dp, color `border`
- **Icon:** 16dp SVG magnifier (stroke `text2`)
- **Text:** placeholder "Search players…", color `text2`, Inter 400 14sp
- **Behavior:** live substring filter on player name (case-insensitive)

### Filter Chip
```
┌─────────────┐     ┌──────────────────┐
│   Driver    │     │ ✓ Fairway Woods  │
└─────────────┘     └──────────────────┘
 Unselected          Selected
```
- **Unselected:** card bg, text color, border 1dp `border`, radius 999dp, padding 8×12dp
- **Selected:** primary bg, `on-primary` text, border 1dp primary, radius 999dp
- **Font:** Roboto Mono 600 10sp
- **Behavior:** multi-select; show all players when no chips selected; match if `preferenceClub` contains chip (substring, case-insensitive)

### Metric Card (Detail Screen)
```
┌────────────────────────────────┐
│█ Average Ball Speed            │ ← 5dp accent bar (left edge)
│  142 mph                       │
│  +3 mph vs avg                 │
└────────────────────────────────┘
```
- **Layout:** padding 16dp, rounded 16dp, card bg, border 1dp `border`
- **Left accent bar:** 5dp wide, full height, color `primary` (animated `accFill`)
- **Label:** Metric Label style (Inter 500 12sp `text2`)
- **Value:** Metric Value style (Inter 700 26sp, unit as subscript Roboto Mono 500 12sp `text2`)
- **Delta:** Delta Line style (Roboto Mono 600 11sp, color `success` if ≥ 0, `warning` if < 0)
- **Animation:** `fadeUp` (0.45s) + `accFill` on accent bar (0.6s), staggered by index (90ms each)

### Shot Card (Inline List)
```
┌──────────────────────────────┐
│ Lob Wedge      [Approach]    │
│ SPEED 72  ANGLE 40°          │
│ CARRY 153yd  SPIN 2,650      │
│ SHOT #47      2:34 PM        │
└──────────────────────────────┘
```
- **Layout:** padding 12–14dp, rounded 14dp, card bg, border 1dp `border`
- **Header row:** club name (Inter 600 13sp), shot-type badge (Roboto Mono 600 10sp `secondary`, primary-soft bg, radius 999dp, padding 6×10dp)
- **Stat grid:** 2-column wrapped, gap 6dp (horiz) × 16dp (vert), each stat: Stat Micro-label (Roboto Mono 500 9sp `text2`) above Shot Stat Value (Roboto Mono 600 13sp)
- **Footer row:** "SHOT #{n}" (Roboto Mono 600 12sp primary) + time (Roboto Mono 500 11sp `text2`)

### Avatar Circle
- **List row:** 46dp diameter, bg `primary-container`, first initial (Inter 700 18sp `primary`)
- **Detail header:** 64dp diameter, bg `primary-container`, first initial (Inter 700 24sp `primary`)

### Top Bar / App Bar
- **Height:** 56dp
- **Background:** page color
- **Border:** 1dp bottom, color `border`
- **Content:** back chevron (38dp circle, icon 20dp), title (Inter 600 17sp `text`), overflow menu button (38dp circle)

### Scatter Plot (Launch Angle vs Carry Distance)
- **Container:** card bg, rounded 16dp, padding 16dp, border 1dp `border`
- **Chart area:** aspect ratio 1:1 or flexible, SVG overlay
    - **X-axis:** launch angle (°), domain ~10–44, ticks at 15°/25°/35°/42°
    - **Y-axis:** carry distance (yd), domain ~70–270, gridlines at 100/160/220
    - **Gridlines:** 1dp stroke, color `border`
    - **Axis ticks/labels:** Roboto Mono 500 10sp `text2`
    - **Data dots:** radius 6dp, fill `primary` @ 82% opacity, stroke 2dp card color
- **Axis captions** (below): "← launch angle (°)" / "carry (yd) ↑" (Roboto Mono 500 11sp `text2`)

---

## Screens

### Screen 1: Players (List)

**Layout:** Fixed header + scrolling list

#### Header Section
- **Top row:** title "Golf Players" (Inter 700 26sp) + theme toggle button (right-aligned)
    - **Toggle button:** pill shape (radius 999dp), border 1dp `border`, padding 6×12dp, Roboto Mono 600 12sp, text "Light mode" / "Dark mode"
- **Search box** (margin-top 14dp): as described in Components section
- **Filter chips** (margin-top 10dp, horizontal scroll, gap 8dp): Driver, Fairway Woods, Irons, Wedges

#### List Section
- **Scroll container:** padding 14dp (v) + 16dp (h), gap 11dp (v)
- **Player row button:** full-width, no button style, tap → navigate to Player Details
    - **Layout:** 46dp avatar + [name / club / avg line] (column, gap 2dp) + [right-aligned last-shot time]
    - **Avatar:** as Component description
    - **Name:** Inter 600 16sp `text`
    - **Club:** Inter 400 13sp `text2`
    - **Avg line:** Roboto Mono 600 12sp `primary`, format: `"Avg speed {averageBallSpeed} mph · {averageDistance} yd"`
    - **Last-shot time:** Roboto Mono 500 11sp `text2`, e.g. "2m ago"

#### Empty State
- When no players match: centered message (Inter 500 14sp `text2`), "No players match your search.", padding 48×16dp

---

### Screen 2: Player Details (Profile)

**Layout:** Fixed top bar + scrolling content (padding 20dp × 16dp, bottom 100dp safe area)

#### Top Bar
- **Left:** back chevron (38dp circle)
- **Center:** player name (Inter 600 17sp `text`)
- **Right:** overflow "⋯" button (38dp circle)

#### Profile Header (border-bottom 1dp `border`, padding-bottom 20dp)
- **Avatar:** 64dp circle, as Component description
- **Name:** Inter 700 22sp `text`
- **Club:** Inter 400 14sp `text2`, e.g. "Dinosaurs"
- **Joined:** Roboto Mono 500 11sp `text2`, e.g. "Member since Jun 2026"

#### Section 1: AVERAGES
- **Label:** "AVERAGES" (Section Label style: Roboto Mono 600 12sp `text2`, 0.08em spacing)
- **Grid:** 2×2 cards, gap 11dp
- **Metrics (in order):**
    1. Average Ball Speed (value in mph)
    2. Average Launch Angle (value + "°", 1 decimal)
    3. Average Carry Distance (value in yd)
    4. Average Spin Rate (value in RPM, thousands-separated)
- **Card:** as Metric Card Component; each includes delta line colored success/warning
- **Animation:** cards fade-in staggered by index (90ms delay each)

#### Section 2: LAUNCH ANGLE vs CARRY DISTANCE
- **Label:** "LAUNCH ANGLE vs CARRY DISTANCE" (Section Label style)
- **Chart card:** rounded 16dp, card bg, border 1dp, padding 16dp
    - **SVG scatter plot:** as described in Components section
    - **One dot per shot:** X = launchAngle (°), Y = carryDistance (yd)

#### Section 3: SHOT RECORDS
- **Header row:** "SHOT RECORDS · {n} shot(s)" (Roboto Mono 600 12sp `text2`) + "See all ›" link (primary, Inter 500 12sp)
    - **Link behavior:** tap → navigate to Shots screen
- **Inline list:** vertical stack (gap 9dp) of compact shot cards
    - **Max 3 shots shown**; "See all" link for full history
    - **Card layout & style:** as Shot Card Component

#### Spacing
- **Between sections:** 24dp top margin, 12dp label-to-content gap

---

### Screen 3: Shots (Full History)

**Layout:** Fixed top bar + filter row + scrolling list

#### Top Bar
- Same as Player Details: back chevron + "{name} · Shots" title + overflow menu

#### Filter Row (border-bottom 1dp `border`, padding 12×16dp)
- **Left pill:** "All clubs ▾" (static in prototype)
- **Right pill:** "Recent ↓" (static in prototype)
- **Pill style:** card bg, border 1dp `border`, radius 999dp, padding 8×12dp, Roboto Mono 600 10sp

#### List (padding 16dp, bottom 100dp safe area)
- **Per shot:** label row + detail card
    - **Label row:** "SHOT #{n} · {club}" (Roboto Mono 600 12sp `primary`)
    - **Detail card:** rounded 16dp, card bg, border 1dp `border`, padding 12–14dp
        - **Four rows:** Ball Speed, Launch Angle, Carry Distance, Spin Rate
        - **Each row:** label (Inter 400 13sp `text2`) left, value (Roboto Mono 600 14sp `text`) right
        - **Dividers:** 1dp `border` between rows (except last)
        - **Extra rows:** Shot Type (secondary color), Time (text2 color)
- **Shot Type badge (in detail):** `secondary` color, primary-soft bg, rounded 999dp, padding 6×10dp
- **Divider between shots:** 16dp vertical gap

---

## Animations

### Animation Definitions

#### `fadeUp` (Metric Cards)
- **Duration:** 450ms (0.45s)
- **Easing:** ease-out (cubic-bezier 0.25, 0.46, 0.45, 0.94)
- **Initial state:** opacity 0, translateY +10dp
- **Final state:** opacity 1, translateY 0
- **Applied to:** all metric cards in Averages section

#### `accFill` (Accent Bar Animation)
- **Duration:** 600ms (0.6s)
- **Easing:** cubic-bezier(0.2, 0.7, 0.3, 1)
- **Initial state:** scaleY 0 (origin: bottom)
- **Final state:** scaleY 1
- **Applied to:** 5dp left accent stripe on each metric card
- **Delay:** staggered by card index, 90ms per card

### Stagger Pattern
```
Card 0: delay 0ms,    start 0ms,    end 600ms
Card 1: delay 90ms,   start 90ms,   end 690ms
Card 2: delay 180ms,  start 180ms,  end 780ms
Card 3: delay 270ms,  start 270ms,  end 870ms
```

### Reduced Motion
- **Detect:** `prefers-reduced-motion: reduce` (CSS) or `isSystemAnimationEnabled()` (Android)
- **Behavior:** disable all animations (set opacity to 1, scaleY to 1 immediately, no delay)
- **Compose implementation:**
  ```kotlin
  val animationDuration = if (isReducedMotionEnabled) 0 else 450
  val animationDelay = if (isReducedMotionEnabled) 0 else staggerIndex * 90
  ```

---

## Navigation & State

### Screen Enum
```kotlin
sealed class Route {
    object Players : Route()
    data class PlayerDetail(val playerId: String) : Route()
    data class Shots(val playerId: String) : Route()
}
```

### State Holder
```kotlin
data class AppState(
    val currentRoute: Route = Route.Players,
    val selectedPlayerId: String? = null,
    val darkTheme: Boolean = false,
    val searchQuery: String = "",
    val clubFilter: Set<String> = emptySet(), // "Driver", "Fairway Woods", "Irons", "Wedges"
)
```

### Navigation Rules
- **Players → Detail:** tap player card, set `selectedPlayerId`
- **Detail → Shots:** tap "See all" or any shot card
- **Shots → Detail:** tap back chevron
- **Detail → Players:** tap back chevron
- **Any screen → any screen:** tap theme toggle (state updates, no navigation)

### Search & Filter Logic
- **Search:** case-insensitive substring match on `Player.name`
- **Club filter:** multi-select; empty = show all; otherwise match if `Player.preferenceClub` contains any selected chip (substring, case-insensitive)
- **Combined:** AND logic (both search AND club filter must match)

---

## Data Model

### Kotlin Data Classes
```kotlin
data class Player(
    val id: String,
    val name: String,
    val profPicUrl: String,       // URL to profile picture
    val preferenceClub: String,   // e.g. "Driver | Irons"
    val averageBallSpeed: Int,    // mph
    val averageDistance: Int,     // yards (carry distance)
    val averageMetrics: List<Metric>, // 4 entries: [Speed, Angle, Distance, Spin]
    val lastShot: String,         // relative time, e.g. "2m"
    val joined: String,           // e.g. "Member since Jun 2026"
)

// Metric: aggregated data point with delta vs. average
data class Metric(
    val value: Double,     // The actual value
    val deltaVsAvg: Double // Difference from average (can be negative)
)

data class Shot(
    val n: Int,                  // Shot number (sequential ID)
    val club: String,            // Club name, e.g. "Lob Wedge"
    val ballSpeed: Int,          // mph
    val launchAngle: Double,     // degrees (1 decimal precision)
    val carryDistance: Int,      // yards
    val spinRate: Int,           // RPM (thousands-separated in display)
    val type: String,            // Shot type, e.g. "Tee", "Approach", "Pitch"
    val time: String,            // HH:MM AM/PM, e.g. "2:34 PM"
)
```

### Formatting Rules
| Value | Format | Example |
|-------|--------|---------|
| Average Ball Speed | integer + " mph" | "142 mph" |
| Average Distance | integer + " yd" | "245 yd" |
| Launch Angle | 1 decimal + "°" | "18.5°" |
| Spin Rate | thousands-separated + " RPM" | "2,650 RPM" |
| Delta (positive) | "+" + value + " " + unit + " vs avg" | "+3 mph vs avg" |
| Delta (negative) | "−" + value + " " + unit + " vs avg" (U+2212) | "−2 yd vs avg" |
| Relative time | natural language | "2m", "15m", "1h ago" |

---

## Implementation Checklist

### Phase 1: Foundation
- [ ] Set up Compose Multiplatform project (KMM structure)
- [ ] Define color tokens in theme file (light & dark)
- [ ] Define typography scale and composables
- [ ] Create reusable components (MetricCard, FilterChip, AvatarCircle, etc.)

### Phase 2: Screens
- [ ] **Players screen:** list, search, club filter, theme toggle
- [ ] **Player Details screen:** header, averages grid, scatter chart, shot records inline
- [ ] **Shots screen:** full shot history with details

### Phase 3: Interactions
- [ ] Navigation between screens
- [ ] Search live filtering
- [ ] Club filter multi-select
- [ ] Theme toggle (light/dark)
- [ ] Tap targets and button interactions

### Phase 4: Polish
- [ ] Animations (fadeUp on metric cards, accFill on accent bars)
- [ ] Reduced motion detection and handling
- [ ] Shadow and elevation (Material 3)
- [ ] Responsive layout testing (430dp max width)

### Phase 5: Data & State
- [ ] Seed data (6 players + sample shots)
- [ ] State management (ViewModel / StateHolder)
- [ ] Data binding (Compose state hoisting)
- [ ] Error handling and empty states

---

## Notes for Developers

1. **Design fidelity:** This spec is high-fidelity. Recreate colors, typography, spacing, and animations to match exactly.
2. **Compose Material 3:** Use `androidx.compose.material3` theme system for colors, typography, and elevation.
3. **Assets:** No bitmap assets. Use SVG/code for icons (search magnifier, chevrons, etc.) or replace with your icon set.
4. **Testing:** Check light/dark theme switching, search/filter accuracy, animation smoothness, and accessibility (WCAG AA color contrast).
5. **Responsive:** Content max-width 430dp; center on wider screens.
6. **Multiplatform:** This spec is for both iOS and Android via Compose Multiplatform. Platform-specific behaviors (back button, status bar) should follow each platform's conventions.

---

**End of Specification**
