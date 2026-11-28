# CanteenGo Python backend

A working first version of the cash-on-pickup service. Uses Python 3.11+, FastAPI, SQLite locally and PostgreSQL for cloud hosting.
All starter menu records are synthetic. This is an implementation foundation, not the completed IT312 submission.

## Setup (PowerShell, from the backend folder)

```powershell
py -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
.\.venv\Scripts\python.exe -m uvicorn canteengo.app:app --host 127.0.0.1 --port 8000
```

The virtual environment has already been created on this computer. Keep the server terminal open.
Interactive API documentation: http://127.0.0.1:8000/docs
Health check: http://127.0.0.1:8000/api/health

The first server start creates `data/canteengo.sqlite3` and seeds eight foods with 100 servings each.
Restarting the server preserves accounts, orders, stock, sessions and audit records.
There are NO default passwords or built-in privileged accounts.

Students register from the Android sign-in screen or POST /api/auth/register.
Create staff and administrator accounts locally, from a separate terminal:

```powershell
.\.venv\Scripts\python.exe -m canteengo.cli create-user --username kitchen --role staff
.\.venv\Scripts\python.exe -m canteengo.cli create-user --username manager --role admin
```

The tool prompts for the password twice. Use a unique password of 8–128 characters containing at least one letter and one number.
Symbols and uppercase letters are optional. Existing accounts keep their original passwords.
Never put passwords on the command line or in committed files.
Only someone with access to the server should run this account-provisioning tool.

## Android connection

1. Run the backend.
2. Run the Android debug app in an emulator.
3. Choose **Sign in to server** on the welcome screen.
4. Use `http://10.0.2.2:8000` (standard Android emulator host mapping).
5. Register a synthetic student account. Order food and record its order number.
6. Sign out and sign in with the staff account. Advance the order through Preparing and Ready.
7. Only confirm **cash received & collected** after payment and handover.
8. Sign back in as the student. Tap **Refresh** to see the updated order.
9. Sign in as administrator to manage stock, open masked reports, and manage orders.

Orders shown in **Local demo** mode are stored only in memory and never reach the server.
Server mode never silently falls back to demo data after a network error.
Passwords and bearer tokens are held only in memory on Android, so process death requires signing in again.
The cart is in-memory. Server orders survive restarting the app and signing back into the same account.
Refreshing is manual in this first version.

### Physical phone

For USB debugging, you can forward the backend without exposing it on Wi-Fi:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:8000 tcp:8000
```

Then use `http://127.0.0.1:8000` in the Android **debug** app.
Alternatively run the backend with `--host 0.0.0.0`, use a private Wi-Fi network shared by the phone and computer,
and enter `http://<computer-Wi-Fi-IP>:8000`. Windows Firewall may need a private-network rule.
Do not disable the firewall. HTTPS is required by release builds; no trust-all certificate workaround is installed.

## Hosted backend

For an HTTPS server that phones can reach over the internet, see
[the Render deployment guide](../docs/DEPLOYMENT.md) and the root `render.yaml`.
The configuration uses Render Free and an external Neon Free PostgreSQL database.
Set DATABASE_URL privately in Render; no persistent Render disk is used.
It is prepared but has not been deployed or verified in the cloud.

## Configuration

Optionally create an ignored `.env` file beside this README, using `.env.example` as a template.
Environment variables take precedence over the file.

| Variable | Meaning |
| --- | --- |
| CANTEENGO_DB | Local SQLite path; defaults to data/canteengo.sqlite3 |
| DATABASE_URL | PostgreSQL connection URL; overrides CANTEENGO_DB; required on Render |
| TEST_DATABASE_URL | Dedicated PostgreSQL test database; never use production for integration tests |

Always run both the CLI and server from the same backend folder, with the same configuration.

## Tests

```powershell
.\.venv\Scripts\python.exe -m pytest -q
```

Default tests use temporary SQLite databases and synthetic identities. PostgreSQL cases
skip unless TEST_DATABASE_URL points to a dedicated test database; they create and drop
isolated schemas there. Never use the production URL for these tests.
The holiday tests mock the external service. They cover weekend/holiday exclusion and service failure,
not live upstream availability.

## Security implemented

- PBKDF2-HMAC-SHA256 passwords, 600,000 iterations and a random salt.
- Random bearer sessions, stored as SHA256 hashes server-side, valid for eight hours; logout revokes them.
- Student-only registration. Staff/admin roles are provisioned locally.
- Student order isolation and server-enforced staff/admin access.
- Strict request bodies, quantity bounds and query parameter allow-lists.
- Parameterized SQLite/PostgreSQL queries; request values are never concatenated into SQL.
- Server-side pricing, atomic stock reservations, sequential status changes, and audit records.
- Idempotent checkout: retrying the same request UUID does not duplicate an order.
- Masked student identifiers and no pickup names in reports.
- Five failed login attempts per username per minute. Production also needs deployment-level rate limits.
- Internet permission only on Android; local HTTP is debug-only. Production deployment still needs configured HTTPS.

## Remaining IT312 work

The current menu is seeded from a JSON fixture, not pulled from an independent vendor service.
The following are NOT implemented yet:

- Separate menu API source with retries and robust response validation.
- CSV sales/XML inventory importers and their documented source mappings.
- Signed HMAC manifests, replay rejection, reject logs, archiving, scheduled automation and daily summaries.
- Signed pickup tokens/QR generation, scan verification and replay prevention.
- Staff-configured closure days, cached holiday results and enforcing operating days during checkout.
- Cloud deployment and its HTTPS configuration.
- Import/security tests for the above features; the API tests are not a replacement for mapping evidence.
- Track B approval, team-owned Git commits, final documentation, actual outside evaluations and consented photos.

See ../docs/IMPLEMENTATION_STATUS.md for a requirement-by-requirement checklist.
