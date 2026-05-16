# 📚 BookShelf — KMP Book Management App

A Kotlin Multiplatform (KMP) + Compose Multiplatform application for browsing, adding, and managing books via the FakeRestAPI. Built with Clean Architecture, offline-first support, and a polished UI.

---

## ✨ Features

| Feature | Status |
|---|---|
| Splash Screen | ✅ |
| List Books | ✅ |
| Add Book | ✅ |
| Book Detail Screen | ✅ |
| Delete Book | ✅ |
| Dark Mode | ✅ Bonus (toggle available in App Bar) |
| Pull to Refresh | ✅ Bonus |
| Search / Filter | ✅ Bonus |
| Swipe to Delete | ✅ Bonus |
| Offline-First | ✅ Bonus |
| Unit Tests | ✅ Bonus |

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin Multiplatform (KMP) |
| UI | Compose Multiplatform |
| Networking | Ktor Client |
| Local DB | SQLDelight |
| Preferences | DataStore Preferences |
| DI | Koin |
| Async | Kotlin Coroutines + StateFlow |
| Navigation | Jetpack Navigation Compose (Multiplatform) |
| Testing | kotlin-test + coroutines-test |

---

## 🏗 Architecture

The project follows Clean Architecture with a strict separation of concerns:

```
UI (Composables)
    ↓
ViewModel  (StateFlow, events)
    ↓
UseCase    (business rules, validation)
    ↓
Repository (interface — single source of truth)
    ↓
  ┌──────────────┐
  │  Remote API  │  ←── Ktor (BookApiService)
  │  Local DB    │  ←── SQLDelight (BookLocalDataSource)
  └──────────────┘
```

**Offline-first strategy:** `getBooks()` emits local cached data immediately via a Flow, then kicks off a background network sync. The UI updates automatically when new data arrives.

---

## 📁 Folder Structure

```
BookApp/
├── shared/                          # KMP shared module
│   └── src/commonMain/kotlin/com/bookapp/shared/
│       ├── data/
│       │   ├── local/               # SQLDelight data source + driver factories
│       │   ├── preferences/         # DataStore (dark mode, first-launch)
│       │   ├── remote/              # Ktor API service + DTOs
│       │   └── repository/          # BookRepositoryImpl (offline-first)
│       ├── domain/
│       │   ├── model/               # Book, Resource<T>
│       │   ├── repository/          # BookRepository interface
│       │   └── usecase/             # GetBooks, AddBook, DeleteBook, etc.
│       ├── presentation/
│       │   ├── booklist/            # BookListViewModel + State + Events
│       │   ├── addbook/             # AddBookViewModel + State + Events
│       │   └── bookdetail/          # BookDetailViewModel + State + Events
│       └── di/                      # Koin modules
│
├── composeApp/                      # Compose UI module (Android + iOS)
│   └── src/commonMain/kotlin/com/bookapp/
│       └── ui/
│           ├── App.kt               # Root composable, dark mode wiring
│           ├── navigation/          # NavGraph + Screen routes
│           ├── screens/
│           │   ├── splash/
│           │   ├── booklist/        # List + search + swipe-to-delete
│           │   ├── addbook/
│           │   └── bookdetail/
│           └── theme/               # MaterialTheme, light/dark color schemes
└── gradle/
    └── libs.versions.toml           # Version catalog
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Xcode 15+ (for iOS)

### Clone & Run

```bash
git clone https://github.com/BookApp.git
cd BookApp

# Android
./gradlew :composeApp:installDebug

# iOS — open in Xcode
open composeApp/iosApp.xcworkspace
```

### Run Tests

```bash
./gradlew :shared:cleanAllTests :shared:allTests
```

---

## 🌐 API

Base URL: `https://fakerestapi.azurewebsites.net`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/Books` | Fetch all books |
| POST | `/api/v1/Books` | Create a book |
| GET | `/api/v1/Books/{id}` | Get book by ID |
| DELETE | `/api/v1/Books/{id}` | Delete a book |

> **Note:** The FakeRestAPI is a mock service. POST and DELETE calls return success responses but do not persist data server-side. The app handles this via optimistic local caching in SQLDelight.

---

## 📸 Screenshots

> Add screenshots or a screen recording link here.

---

## 🧪 Testing

Unit tests cover:

- `AddBookUseCase` — input validation (blank title, invalid page count)
- `DeleteBookUseCase` — success and failure paths
- `BookListViewModel` — state emission, search filtering

All tests use a `FakeBookRepository` and `StandardTestDispatcher` to keep tests fast and deterministic.

---

## 💡 Design Decisions

- **SQLDelight over Room** — better KMP support; generates type-safe queries from `.sq` files.
- **Offline-first Flow** — `getBooks()` combines the local DB stream with a background sync so the list is always snappy, even without internet.
- **Sealed `Resource<T>`** — a simple, idiomatic way to express Loading / Success / Error across the whole stack.
- **Koin over Hilt** — Koin works identically on both platforms with no annotation processing.
- **Single-module ViewModels** — all ViewModels live in `shared` so logic is 100% reusable on iOS.

---

## 👤 Author

Built for the KMP Internship Technical Assignment.
