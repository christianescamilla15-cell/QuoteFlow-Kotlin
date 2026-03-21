# QuoteFlow

A minimalist quote reading app built with **Kotlin + Jetpack Compose** following **MVVM architecture**. Swipe through curated quotes on stoicism, philosophy, discipline, and reflection. Save favorites, complete daily challenges, and customize your experience.

This is a native Android version of [MindScrolling](https://github.com/christianhernandez/MindScrolling) (Flutter), rebuilt from scratch to demonstrate proficiency in modern Android development.

## Screenshots

<!-- Add screenshots here -->
| Feed | Vault | Challenge | Profile |
|------|-------|-----------|---------|
| _screenshot_ | _screenshot_ | _screenshot_ | _screenshot_ |

## Architecture

```
MVVM (Model-View-ViewModel)

┌─────────────────────────────────────────────────┐
│                    UI Layer                       │
│  Composable Screens → ViewModels → StateFlow     │
├─────────────────────────────────────────────────┤
│                  Domain Layer                     │
│            QuoteRepository                        │
├─────────────────────────────────────────────────┤
│                  Data Layer                       │
│         Room Database → QuoteDao                  │
└─────────────────────────────────────────────────┘
```

- **Unidirectional data flow**: UI observes `StateFlow` from ViewModels
- **Repository pattern**: Single source of truth for quote data
- **Room persistence**: Local database with 60+ prepopulated quotes
- **Compose Navigation**: Type-safe navigation with bottom nav bar

## Features

- **Swipe Cards**: Drag gesture-based card swiping with spring animations. Swipe right to save, left to skip.
- **Quote Vault**: Browse and manage saved quotes with category badges.
- **Daily Challenge**: Complete a swipe goal each day to build a streak.
- **Dark Mode**: Toggle between light and dark themes (Material 3 + Dynamic Color on Android 12+).
- **Bilingual**: Switch between English and Spanish at any time.

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + StateFlow |
| Navigation | Navigation Compose |
| Database | Room (SQLite) |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit 4 + Coroutines Test |
| Min SDK | 26 (Android 8.0) |

## Building

1. Open the project in **Android Studio Hedgehog** (2023.1.1) or newer.
2. Let Gradle sync complete.
3. Run on an emulator or physical device (API 26+).

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test
```

## Project Structure

```
app/src/main/java/com/christianhernandez/quoteflow/
├── QuoteFlowApp.kt           # Application class with DI
├── MainActivity.kt            # Single activity entry point
├── navigation/NavGraph.kt     # Compose Navigation + Bottom Nav
├── data/
│   ├── model/                 # Quote, Challenge, SwipeEvent
│   ├── local/                 # Room Database + DAO
│   └── repository/            # QuoteRepository
├── ui/
│   ├── theme/                 # Material 3 Light/Dark themes
│   ├── components/            # QuoteCard, SwipeableCard, ChallengeCard
│   ├── feed/                  # Main swipe feed
│   ├── vault/                 # Saved quotes
│   ├── challenge/             # Daily challenge
│   └── profile/               # Settings & stats
└── util/Constants.kt          # App constants & enums
```

## Author

**Christian Hernandez** — IA & Automation Specialist

---

Built with Kotlin + Jetpack Compose
