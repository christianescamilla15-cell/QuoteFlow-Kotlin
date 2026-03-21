# QuoteFlow — Kotlin Native Port of MindScrolling

## Overview
QuoteFlow is the Kotlin + Jetpack Compose native Android port of MindScrolling.
Same backend (Render), same database (Supabase), same feed algorithm — native Android experience.

## Architecture
- **UI**: Jetpack Compose + Material 3 (Light/Dark/Dynamic)
- **State**: ViewModels + StateFlow + Repository Pattern (MVVM)
- **Local DB**: Room (offline cache)
- **Network**: Retrofit + Gson (connects to MindScrolling backend)
- **DI**: Manual (Application class provides singletons)
- **Navigation**: Navigation Compose with bottom nav
- **Testing**: JUnit 4 + Coroutines Test

## Backend
- **URL**: https://mindscrolling.onrender.com
- **Auth**: X-Device-ID header (UUID v4, stored in SharedPreferences)
- **Rate limit**: 60 req/min

## Key Rules
- Every API call requires X-Device-ID header
- Use .enqueue() or suspend functions for network calls — never block main thread
- All strings must support EN/ES
- Swipe directions are canonical: UP=stoicism, RIGHT=discipline, LEFT=reflection, DOWN=philosophy
- NEVER change the swipe direction mapping
- Repository pattern: ViewModel → Repository → RemoteDataSource + LocalDataSource
- Room for offline cache, Retrofit for API calls

## Endpoints Used
- GET /quotes/feed?lang=en&limit=20 — Adaptive feed
- POST /swipes — Log swipe event
- GET /vault — Saved quotes
- POST /vault — Save quote
- DELETE /vault/:id — Remove from vault
- GET /challenges/today?lang=en — Daily challenge
- POST /challenges/:id/progress — Increment progress
- GET /profile — User profile
- POST /profile — Update profile
- GET /premium/status — Premium state
- GET /packs — Pack catalog
- GET /packs/:id/preview — Pack preview quotes
- GET /map — Philosophy map scores

## Build
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Testing
```bash
./gradlew test
```
