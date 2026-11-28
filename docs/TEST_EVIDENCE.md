# Development verification

## 2026-11-28 — Frontend integration

Branch: integration/frontend-cloud. Incoming frontend: dc70bc5.
Command: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest --console=plain`
Result: BUILD SUCCESSFUL in 4m 22s, 44 tasks executed, exit 0.
Gradle sync subsequently finished successfully.
The existing Android unit-test task passed. IDE policy excludes the generated test
report directory, so no test count is asserted here.
Warnings include deprecated ReceiptLong icons and a native library packaged without
symbol stripping. Neither prevented the debug build.
Before integration, Login/Signup/Welcome previews rendered; Home/Checkout/Admin
initially failed because the added icons dependency had not been synced.
After sync, Home, Login, Signup, ServerSignIn wrapper and Checkout previews rendered.
The wrapper's initial stale cached Login signature error cleared after refreshing
Login and rendering the wrapper again. See FRONTEND_INTEGRATION.md.
Device/server journeys remain manual and pending. Backend tests were not rerun
because backend code and schema were unchanged in this merge.


## 2026-11-28 — Free hosting and database compatibility

Command from backend:
`$env:PYTHONDONTWRITEBYTECODE='1'; ./.venv/Scripts/python.exe -m pytest -q -p no:cacheprovider --tb=short`

Corrective full run: **48 passed, 37 skipped, 1 warning in 140.55 seconds**, exit 0.
The 37 skipped cases require TEST_DATABASE_URL for live PostgreSQL integration.
The passes cover the 37 existing API cases on SQLite plus 11 database configuration,
parameter binding, lock selection and TLS option tests (PostgreSQL connections mocked).
This does not verify PostgreSQL SQL execution, live Neon connectivity, deployed HTTPS,
cloud persistence or phone-to-cloud behavior.

The initial run exposed a seed INSERT placeholder count error (37 setup errors);
the INSERT was corrected before the successful full rerun.
Installed new dependency psycopg[binary] 3.3.9 successfully.
Existing Starlette/httpx deprecation warning remains; dependencies were not upgraded.
No Android changes were made for this backend task, and no APK rebuild was run.

Cloud acceptance checklist: see DEPLOYMENT.md.

## Historical development verification — 2026-11-14

Synthetic data only. These are development checks, not the outside-rater evaluation.

## Python API

Command, from backend: `.venv\\Scripts\\python.exe -m pytest -q`

Initial run: 35 passed; two holiday tests failed because mocking httpx.Client.get
also intercepted the test client's GET convenience method.
The tests were corrected to use the client's request method, without changing the service implementation.

Corrective run: `.venv\\Scripts\\python.exe -m pytest -q -k holiday`
Result: 2 passed, 35 deselected.

All 37 test cases have passed across these runs. The test runner emits a dependency warning:
Starlette currently deprecates its httpx-based test client in favor of httpx2.
This does not prevent the tests from running.

Covered:
- Health, missing authentication, student registration and duplicate usernames.
- Privileged-role injection, invalid passwords and SQL-injection login input.
- Salted password verification, bad login rate limiting, token expiry and logout revocation.
- Server pricing, forbidden client prices, quantity allow-list and unknown/duplicate foods.
- Checkout idempotency and conflicting reuse of request IDs.
- Student order isolation and staff/admin permissions.
- Ordered status transitions and audit entries.
- Transaction rollback and concurrent stock reservation.
- Masked CSV/JSON/XML reports and query parameter allow-lists.
- Persistence after restarting the API.
- Weekend/holiday exclusion and explicit upstream failure.

## Android

Command: `:app:assembleDebug :app:testDebugUnitTest`
Build succeeded. Unit tests: 3 passed, 0 skipped, 0 failed.

The existing tests exercise cart math and the local demo order flow.
No connected-device or Android-to-server UI test has been run yet.
Manual verification should follow backend/README.md with separate student, staff and admin accounts.
Signed-import mapping tests and QR security tests require their respective future implementations.
