# CanteenGo

Campus canteen pre-order app: Kotlin / Jetpack Compose Android frontend and a Python FastAPI / SQLite backend.
Cash on pickup. Android API 24+.

## What works

- Tap-to-continue welcome screen and local demo.
- Server sign-in and student registration; roles come from the server.
- Student menu, cart, checkout, persistent order history and manual refresh.
- Staff order preparation, ready and cash-received/collected actions.
- Administrator stock updates and masked JSON/CSV/XML report views.
- Server-side pricing, stock validation, checkout retry protection and audit records.

Start the backend using **[backend/README.md](backend/README.md)**.
Then run the app from Android Studio and choose **Sign in to server**.
Use `http://10.0.2.2:8000` in the emulator, or follow the physical-phone instructions in that README.
There are no default credentials. Students register; staff/admin accounts are created through the server CLI.

The **Local demo** banner means no server is connected; demo orders are not sent to the canteen.
Sign-in passwords/tokens are not persisted on Android. Reopen and sign in to reload server orders after process death.

## Android development

Use the existing Android Studio project SDK/Gradle configuration.
The project's installed JDK is 21. Source/target Java compatibility remains 11.
Preview the welcome and student home screens using Split/Design in their Kotlin editor tabs.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
Debug builds permit local HTTP. Release builds require HTTPS.
A physical device was not connected during initial development; on-device verification remains necessary.

## Project documents

- [Backend setup and security notes](backend/README.md)
- [API specification](docs/API.md)
- [IT312 implementation checklist](docs/IMPLEMENTATION_STATUS.md)

This is the first connected implementation, not a completed IT312 final submission.
The independent data sources, signed imports, automation, QR pickup verification, cloud deployment
and evaluation/documentation deliverables remain on the checklist.
