# BookShelf — KMP Book Management App

Kotlin Multiplatform internship assignment: browse, add, and manage books via [FakeRestAPI](https://fakerestapi.azurewebsites.net).

## Repository

- **GitHub:** https://github.com/anandzhaa/bookapp
- **Project root:** `BookApp/` (Gradle project lives in this folder)

## Quick start

```bash
git clone https://github.com/anandzhaa/bookapp.git
cd bookapp/BookApp

# Windows
gradlew.bat :composeApp:assembleDebug

# macOS / Linux
./gradlew :composeApp:assembleDebug
```

Install on a device or emulator:

```bash
gradlew.bat :composeApp:installDebug
```

## Submission artifacts

| Artifact | Location |
|----------|----------|
| Debug APK | `BookApp/releases/BookShelf-debug.apk` |
| Full documentation | [BookApp/README.md](BookApp/README.md) |

## Assignment checklist

- KMP + Compose Multiplatform, Coroutines, StateFlow
- Clean Architecture: UI → ViewModel → UseCase → Repository
- SQLDelight + DataStore
- Splash, list, add, detail, delete
- Bonus: dark mode, pull-to-refresh, search, swipe-delete, offline-first, unit tests

See [BookApp/README.md](BookApp/README.md) for architecture, API details, and testing.
