# BookShelf — KMP Book Management App

A **Kotlin Multiplatform (KMP)** + **Compose Multiplatform** app for browsing, adding, and managing books via the [FakeRestAPI](https://fakerestapi.azurewebsites.net). Built with **Clean Architecture**, **offline-first** caching, and a polished Material 3 UI.

**Repository:** https://github.com/anandzhaa/bookapp

---

## Features

| Feature | Status |
|---------|--------|
| Splash Screen | Done |
| List Books (GET) | Done |
| Add Book (POST) | Done |
| Book Detail (GET by id) | Done |
| Delete Book (DELETE) | Done |
| Dark Mode | Bonus |
| Pull to Refresh | Bonus |
| Search / Filter | Bonus |
| Swipe to Delete | Bonus |
| Offline-First | Bonus |
| Unit Tests | Bonus |

---

## Tech stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin Multiplatform |
| UI | Compose Multiplatform |
| Networking | Ktor Client |
| Local DB | SQLDelight |
| Preferences | DataStore |
| DI | Koin |
| Async | Coroutines + StateFlow |
| Navigation | Navigation Compose (Multiplatform) |
| Testing | kotlin-test, coroutines-test |

---

## Architecture

Data flow follows the assignment pattern:

```
UI (Composables)
    ↓
ViewModel  (StateFlow, events)
    ↓
UseCase    (validation, business rules)
    ↓
Repository (interface)
    ↓
  Remote API (Ktor)  +  Local DB (SQLDelight)
```

**Offline-first:** `getBooks()` emits the SQLDelight cache immediately, then syncs from the network in the background.

---

## Folder structure

```
BookApp/
├── shared/
│   └── src/commonMain/kotlin/com/bookapp/shared/
│       ├── data/          # remote, local, preferences, repository
│       ├── domain/        # models, repository interface, use cases
│       ├── presentation/  # ViewModels (shared across platforms)
│       └── di/            # Koin modules
├── composeApp/
│   └── src/commonMain/kotlin/com/bookapp/ui/
│       ├── navigation/
│       ├── screens/       # splash, booklist, addbook, bookdetail
│       └── theme/
└── releases/              # submission APK
```

Platform-specific code: `shared/src/androidMain`, `shared/src/iosMain`.

---

## Getting started

### Prerequisites

- Android Studio Hedgehog or newer (or JDK 17+ and Android SDK)
- Android SDK API 35, min SDK 24

### Clone and run

```bash
git clone https://github.com/anandzhaa/bookapp.git
cd bookapp/BookApp

# Build debug APK
gradlew.bat :composeApp:assembleDebug    # Windows
./gradlew :composeApp:assembleDebug      # macOS / Linux

# Install on device/emulator
gradlew.bat :composeApp:installDebug
```

### Pre-built APK (submission)

Install without building:

```
BookApp/releases/BookShelf-debug.apk
```

### Run tests

```bash
gradlew.bat :shared:cleanAllTests :shared:allTests
```

---

## API

**Base URL:** `https://fakerestapi.azurewebsites.net`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/Books` | Fetch all books |
| POST | `/api/v1/Books` | Create a book |
| GET | `/api/v1/Books/{id}` | Get book by ID |
| DELETE | `/api/v1/Books/{id}` | Delete a book |

**Book JSON fields:** `id`, `title`, `description`, `pageCount`, `excerpt`, `publishDate` (ISO 8601).

> The FakeRestAPI does not persist POST/DELETE on the server. The app uses SQLDelight for optimistic local storage so add/delete feel correct offline and across sessions.

---

## Screenshots

| Splash | Book list | Add book | Detail |
|--------|-----------|----------|--------|
| Animated splash with app branding | List with search, pull-to-refresh, swipe-delete | Form with validation | Full book info + delete |

Run the app or install `releases/BookShelf-debug.apk` to see all screens. A screen recording can be attached to the GitHub release or README if required by your evaluator.

---

## Testing

Unit tests in `shared/src/commonTest`:

- `AddBookUseCase` — blank title, invalid page count, success path
- `DeleteBookUseCase` — success and failure
- `BookListViewModel` — load, search filter, empty search

Uses `FakeBookRepository` and `StandardTestDispatcher`.

---

## Design decisions

- **SQLDelight** — strong KMP support, type-safe `.sq` queries
- **Offline-first Flow** — local DB stream + background network sync
- **`Resource<T>`** — Loading / Success / Error across layers
- **Koin** — multiplatform DI without annotation processors
- **ViewModels in `shared`** — business logic reusable on iOS framework consumers

---

## Author

Built for the **KMP Internship Technical Assignment**.
