# Free CanteenGo hosting: Render + Neon

Status: PostgreSQL support and free-service configuration implemented. Live Neon
connectivity and cloud deployment remain unverified. Related: R4, R8, R12, R13, R17.

## Architecture and data

Android -> HTTPS Render FastAPI -> TLS Neon PostgreSQL.
Local development still defaults to SQLite when DATABASE_URL is absent.
Render requires DATABASE_URL and refuses to start without it, preventing accidental
use of its temporary filesystem. No paid disk is needed.

Choose Free plans on both providers and stay within their current usage limits.
Render can sleep after inactivity; its first response can take about a minute.
The current Android timeouts are shorter: open /api/health in the phone browser,
wait for status ok, then sign in. Do not repeatedly register while waking the server.
If registration succeeded but login timed out, try Sign in with that same account.
Neon Free also has storage/compute quotas; monitor both provider dashboards.

A new Neon database starts with the synthetic menu and no accounts. This change
does not transfer local SQLite users/orders or alter the existing SQLite file.
Register synthetic students in the deployed app. Moving existing data needs a
separate reviewed migration. Never commit databases, .env files or connection URLs.

## Before deployment

Review and commit the changes on feature/free-hosting-postgres, then push the
approved branch yourself. The agent does not commit, push or merge automatically.
Render must deploy that updated branch/commit; the old main branch does not yet
contain PostgreSQL support.

## Render form (manual setup)

- Name: canteengo-api (or another available name)
- Language: Python 3
- Branch: the branch containing these changes
- Region: Singapore, preferably matching Neon
- Root Directory: backend
- Build Command: pip install -r requirements.txt
- Start Command: python -m uvicorn canteengo.app:app --host 0.0.0.0 --port $PORT --workers 1
- Compute: Free ($0), no disk
- Health Check Path (Advanced): /api/health
- Automatic deploys: Off / manual

Environment variables (set privately in Render):
- PYTHON_VERSION = 3.13.14
- DATABASE_URL = the PostgreSQL URL copied from Neon's Connect dialog

Copy only the URL beginning postgresql://, not a psql command or surrounding quotes.
Keep sslmode=require (and any channel_binding setting provided by Neon).
Never paste the URL in chat, screenshots, Git or logs. The backend requires TLS.
Set this in the Render form BEFORE deploying. Do not set CANTEENGO_DB on Render.

Alternatively, create a Blueprint from root render.yaml and enter DATABASE_URL
when prompted. That Blueprint now explicitly selects the Free plan.

## Connect Android

After Render says Live, on the phone open:
https://YOUR-ACTUAL-SERVICE.onrender.com/api/health

Wait for {"status":"ok","service":"CanteenGo"}.
In the app enter only https://YOUR-ACTUAL-SERVICE.onrender.com as Server address.
Do not append /api or /api/health. Use HTTPS.
Register/sign in. The existing installed APK can use this address without rebuilding.
The computer can be off once the cloud backend is deployed.

## Staff/admin creation on the Free plan

Render Free does not provide a service shell. Use the local CLI against Neon:

1. Install backend requirements into backend/.venv.
2. Create ignored backend/.env from .env.example locally.
3. Set DATABASE_URL in that private file using the same Neon URL.
4. From backend, run:
   .venv\Scripts\python.exe -m canteengo.cli create-user --username kitchen --role staff
   .venv\Scripts\python.exe -m canteengo.cli create-user --username manager --role admin
5. Enter passwords at the hidden prompts. Never put them in commands.
6. Remove DATABASE_URL from local .env when you want local SQLite again.

The CLI uses the configured cloud database while DATABASE_URL is set. Check which
environment you are administering before creating accounts. No default privileged
passwords or public privileged-registration endpoint is added.

## Verification

Run from backend after installing requirements:
    .venv\Scripts\python.exe -m pytest -q

SQLite regression tests and configuration tests can run without cloud credentials.
For PostgreSQL integration checks, set TEST_DATABASE_URL privately to a dedicated
test database URL, then run pytest. Tests create/drop isolated random schemas;
never supply the production database or commit this credential. The Neon role must
be allowed to create schemas. PostgreSQL cases skip when TEST_DATABASE_URL is absent.

Cloud checks still pending:
- HTTPS health and register/login on a phone outside the computer's network.
- Student order, staff transitions, admin stock/report permissions.
- Restart/redeploy; verify the same accounts, orders and stock remain.
- Concurrent last-item checkout and duplicate checkout retries against PostgreSQL.
- Wrong password, duplicate username, expired/revoked session and invalid role.
- Cold start and network interruption recovery.

## Limits and recovery

The current order write implementation serializes PostgreSQL checkout/status
transactions using table locks. This favors correctness for the campus prototype;
it is not a high-throughput design. SQL values remain parameterized.
Existing API routes, JSON shapes and password/session formats are preserved.

Configure abuse controls, monitoring, retention and tested PostgreSQL backups
before handling real student data. Review Neon's restore limits; a free plan is not
a complete backup policy. Deleting the Neon project deletes its records.
No existing local database has been migrated or deleted.

## References

https://render.com/docs/free
https://render.com/docs/blueprint-spec
https://neon.com/pricing
https://www.psycopg.org/psycopg3/docs/basic/usage.html

Suggested commit: feat: support Neon PostgreSQL and free Render hosting
