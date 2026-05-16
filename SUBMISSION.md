# KMP Assignment — What to Submit

## 1. GitHub link (required)
https://github.com/anandzhaa/bookapp

Push latest code from this folder:
```bash
cd C:\Users\ASUS\Downloads\BookApp
git add .
git commit -m "Complete KMP book app submission"
git push origin main
```

## 2. README (required)
- Root: `README.md`
- Full docs: `BookApp/README.md`

## 3. APK (required — pick one)
After build succeeds:
```
BookApp\composeApp\build\outputs\apk\debug\composeApp-debug.apk
```
Copy for submission:
```
BookApp\releases\BookShelf-debug.apk
```

Build command (run inside `BookApp` folder):
```bash
gradlew.bat :composeApp:assembleDebug
```

**If build fails on PC:** Open `BookApp` in Android Studio → **Build → Build APK(s)**. APK path is shown in the notification.

## 4. Screen recording (optional if APK provided)
Record: Splash → List → Search → Add book → Detail → Delete → Dark mode toggle.

## 5. Assignment checklist (all in code)
| Item | Status |
|------|--------|
| KMP + Compose | Yes |
| Coroutines + StateFlow | Yes |
| Clean Architecture UI→VM→UseCase→Repo | Yes |
| SQLDelight + DataStore | Yes |
| Splash, List, Add, Detail, Delete | Yes |
| API fakerestapi.azurewebsites.net | Yes |
| Dark mode, Search, Refresh, Delete, Offline, Tests | Bonus |

## 6. Evaluator quick run
1. Clone repo
2. Open `BookApp` in Android Studio
3. Sync Gradle
4. Run on emulator or install `releases/BookShelf-debug.apk`
