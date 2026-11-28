# CanteenGo session handoff — 2026-11-28

## Resume here

The user wants brief instructions, one step at a time, and wants to save chat credits.
Read this file before repeating discovery. Inspect Git status before making edits.
The next task is phone-to-cloud verification, not a backend rewrite.

## Current deployment

- Render service: CanteenGo-api, Python 3, Free plan, Oregon (observed in screenshots).
- Neon project: canteengo, PostgreSQL, Free setup, Singapore endpoint.
- Latest user screenshot showed Render "Your service is live", "Application startup
  complete", and Uvicorn listening on port 10000.
- This is screenshot evidence, not an independently executed cloud test.
- Failed deployment was caused by malformed DATABASE_URL: two URLs were concatenated.
  User corrected the value and redeployed successfully.
- A password was exposed in a screenshot. User was instructed to reset it in Neon.
  Completion of password rotation has not been confirmed. Never copy the old secret.
- Public root / returns 404 by design. Test /api/health instead.
- Verified public URL: https://canteengo-api-rzl0.onrender.com (lowercase letter l,
  then digit zero). Direct GET /api/health returned HTTP 200 with status ok.
- User typed rz10 (digit one) instead of rzl0 (letter l), causing HTTP 404.
  Both hosts were checked directly: rz10 returned 404; rzl0 returned 200.
- Registration/login through the corrected URL is still awaiting user confirmation.
- Android Server address: that HTTPS base URL ONLY, without /api or /api/health.
- Browser health check: append /api/health; expect status ok and service CanteenGo.
- Existing installed debug APK accepts HTTPS; changing the server address needs no APK.
- Cloud database is separate from local SQLite. No records were migrated.
- No default account. Register a new synthetic student on the cloud service.

## Immediate next steps

1. Latest source now uses ServerConfig.BASE_URL automatically; the URL field and
   emulator/Wi-Fi hints were removed from ServerSignInScreen. CanteenGoApp supplies
   the verified URL to the ViewModel. Rebuild and install the updated APK to see it.
   Older installed APKs still require manual entry of the verified base URL.
   Build/device verification of this UI change is pending.
2. On phone browser, open that URL + /api/health and wait for status ok.
3. Register/sign in, place an order, sign out/in, confirm saved order remains.
4. Verify persistence after Render restart/redeploy.
5. Provision staff/admin only through the CLI and test role workflows.
6. Test PostgreSQL concurrency and idempotency against a dedicated test database.

Free Render sleeps when idle. Wake it in the browser before app sign-in; current app
timeouts are shorter than a cold start. Computer/Wi-Fi backend is no longer needed
when using the deployed URL.

## Implementation completed

- backend/canteengo/database.py: SQLite locally; PostgreSQL when DATABASE_URL set.
  TLS required; generated IDs; bound parameters; transactional startup seeding;
  PostgreSQL order table locks preserve stock/idempotency; login-attempt lock.
- backend/canteengo/app.py: database target selection, PostgreSQL-compatible queries,
  health checks database access; API JSON contracts retained.
- backend/canteengo/cli.py: supports configured PostgreSQL and duplicate users.
- backend/requirements.txt: added psycopg[binary]==3.3.9.
- render.yaml: Render Free, no disk, one worker, DATABASE_URL supplied privately.
- backend/.env.example and README updated.
- docs/DEPLOYMENT.md: complete deployment/CLI instructions and acceptance checklist.
- docs/TEST_EVIDENCE.md: actual local test results.
- Android session-expiry cleanup implemented earlier in CanteenViewModel.kt, still
  uncommitted at last Git check and not verified on a device.

Render settings:
- Root Directory: backend
- Build Command: pip install -r requirements.txt
- Start Command: python -m uvicorn canteengo.app:app --host 0.0.0.0 --port $PORT --workers 1
- PYTHON_VERSION: 3.13.14
- DATABASE_URL: private complete Neon URL (one URL, no quotes, no psql prefix)
- Health Check Path: /api/health

## Git checkpoint

Local check at handoff:
- Branch feature/free-hosting-postgres tracks origin/feature/free-hosting-postgres.
- Latest local commit: 84ca271 feat: support Neon PostgreSQL and free Render hosting.
- Modified: app/src/main/java/com/example/canteengo/ui/CanteenViewModel.kt.
- Untracked: .idea/vcs.xml. Preserve it.
- User created PR #1; Render screenshot showed merged main deployment commit 66fc034.
  Remote merge inferred from screenshot; local main was not pulled or inspected.
- No agent push/merge. User performed Git operations.
- This handoff file is newly created and needs committing if desired.

## Testing evidence

Full local backend run:
48 passed, 37 skipped, 1 warning in 140.55 seconds, exit 0.
37 API cases passed on SQLite; 11 configuration/adapter/TLS tests passed.
37 live PostgreSQL cases skipped: TEST_DATABASE_URL not configured.
Never call these skipped cases passes.
PostgreSQL startup success is supported by latest Render logs; full cloud role,
concurrency, phone registration and persistence tests remain unverified.
Existing Starlette/httpx deprecation warning remains.
No Android build was run for the backend change.

## Broader project status

Read docs/IMPLEMENTATION_STATUS.md for R1-R18; its deployment checklist predates the
latest screenshots and should be reconciled after actual cloud verification.
Signed imports, independent menu integration, HMAC exchange/replay protection,
QR pickup, automation and evaluation remain incomplete.
Instructor briefing, rating sheet, DNSC reference PDFs/DOCX and starter kit were not
found in the workspace. Do not invent instructor approval or evaluation results.

## Rules

Keep existing architecture and API contracts. Use Android Studio studio_ tools.
Do not expose or commit database URLs/passwords/.env files. No automatic push/merge.
Ask before major architecture/dependency/schema changes; PostgreSQL support was
explicitly approved. Keep guidance concise and perform authorized code work.
