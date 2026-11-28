# IT312 group implementation status

Scope: five-member Track B proposal, cash on pickup. Instructor approval is still pending.
This checklist distinguishes working code from the remaining final-project deliverables.

| ID | Current status | Next work |
| --- | --- | --- |
| R1 | Not built | Signed drop-folder pipeline, log, reject handling and archive |
| R2 | Not built | Schedule imports and daily sales/stock summaries |
| R3 | Proposal drafted in conversation | Approved proposal, solution document and diagrams |
| R4 | Not complete | Signed QR pickup + actual cloud deployment and justification |
| R5 | Not built | Independent menu API client with error handling |
| R6 | Partial | Holiday date API works; checkout enforcement, caching and closures remain |
| R7 | Working first version | 13 endpoints; refine API spec as integration features are added |
| R8 | Working first version | Native student, staff and admin views call the backend API |
| R9 | Not built | JSON/CSV/XML mapping tables, adapters and rejection logs |
| R10 | Working first version | Masked order reports in CSV, JSON and XML |
| R11 | Partial | API body/query validation works; importer and upstream schemas remain |
| R12 | Working first version | Salted password hashes, expiring/revocable sessions and three roles |
| R13 | Partial | Parameterized SQL and ignored config; deploy with HTTPS |
| R14 | Not built | HMAC manifests and durable replay rejection |
| R15 | Not built | Signed, expiring, single-use pickup code/QR workflow |
| R16 | Working first version | Report masking; audit placement, status changes and stock updates |
| R17 | Partial | API/security tests added; mapping and tampered-file tests still required |
| R18 | External work required | 30 real raters, signed sheets, consented photos, masterlist and summaries |

## Development change log

### 2026-11-28 — Password policy update (R11, R12, R17)

- New accounts require at least 8 characters, one letter and one number; symbols
  and uppercase are optional. Existing 128-character maximum retained.
- App signup, backend registration and account CLI use the same policy.
- Login uses length bounds only, preserving older account passwords.
- [x] Targeted backend tests: 13 passed, 1 existing warning (5.27s).
- [x] Updated debug APK built (15s) and Signup preview rendered.
- [ ] Deploy backend update and install updated APK; verify on phone.
- No database migration or existing-account changes.



### 2026-11-28 — Frontend/cloud integration (R8, R11, R12, R17)

- Combined collaborator frontend dc70bc5 with automatic cloud authentication on
  integration/frontend-cloud, preserving a local checkpoint of prior work.
- Wired designed auth, live menu/prices, category/navigation callbacks, cash-only
  checkout and real admin stock updates; local-only menu editing remains demo-only.
- Excluded imported machine-specific emulator selection and retained prior JDK name.
- [x] Debug APK build and existing Android unit-test task passed (4m 22s).
- [x] Gradle sync succeeded; Home/Login/Signup/ServerSignIn/Checkout previews rendered.
- [ ] Phone/server role and order workflows verified.
- See FRONTEND_INTEGRATION.md and TEST_EVIDENCE.md. Requirements remain partial
  until connected workflows and the wider acceptance criteria are verified.



### 2026-11-28 — Free hosting and PostgreSQL support (R4, R8, R12, R13, R17)

- Supersedes the paid SQLite hosting configuration below: Render Free + Neon Free,
  with DATABASE_URL entered privately and no paid disk.
- PostgreSQL schema initialization, bound SQL parameters, TLS connections, generated
  IDs and transaction locks added; local SQLite and API response shapes retained.
- Render refuses to start with missing/invalid DATABASE_URL instead of losing data
  in temporary SQLite storage. Existing SQLite records are not migrated.
- New dependency: psycopg[binary] 3.3.9. Work branch: feature/free-hosting-postgres.
- Existing API suite can also run in isolated PostgreSQL test schemas when a
  dedicated TEST_DATABASE_URL is supplied.
- [x] Implement support and update deployment instructions.
- [x] Local suite: 48 passed, 37 PostgreSQL cases skipped, 1 warning (140.55s).
  Live PostgreSQL is not verified; see TEST_EVIDENCE.md.
- [ ] Verify live Neon connectivity and PostgreSQL concurrency tests.
- [ ] User commits/pushes reviewed changes and deploys the updated branch.
- [ ] Verify phone HTTPS access and restart/redeploy persistence.
- R4/R13/R17 remain incomplete; configuration alone is not deployment evidence.



### 2026-11-28 — Hosted backend preparation (R4, R8, R13, R17)

- Added root `render.yaml`: paid Python service, one worker/instance, persistent
  SQLite disk, health check, pinned Python runtime and manual deploys.
- Added `docs/DEPLOYMENT.md`: deployment, Android URL setup, privileged account
  provisioning, persistence checks and operational limitations.
- No cloud resources created, data migrated, dependencies changed or Git push made.
- [x] Prepare configuration and instructions.
- [ ] Review provider costs and commit/push approved files.
- [ ] Deploy and verify HTTPS, phone connectivity, roles and restart/redeploy persistence.
- R4/R13 remain incomplete until actual cloud verification and remaining controls.



### 2026-11-28 — Expired Android session recovery (R8, R12, R17)

- Implemented: authenticated HTTP 401 responses clear the local session, role, cart,
  orders, report and pending checkout identity. The existing navigation observer
  returns the user to Welcome, with a message requesting sign-in.
- Incorrect credentials during initial sign-in retain their original API error.
  Network errors and HTTP 403 responses do not clear the session.
- Sign-out uses the same local cleanup.
- Verification pending: run the app, sign in, revoke that session from a separate
  authenticated API client, then tap Refresh. Expect Welcome, an expiry message,
  and no previous account data. Sign in again to reload saved server orders.
- Known behavior: unsent cart contents are cleared when the session is invalidated.
- Checklist: [x] implement recovery; [ ] verify on device; [ ] commit reviewed changes.

## Suggested ownership

1. Lead: approval, architecture, emerging technology justification, milestones and final presentation.
2. API developer: maintain backend endpoints, independent menu source and holiday behavior.
3. Integration engineer: CSV/XML mappings, signed imports, exports and scheduled processing.
4. Security engineer: review access controls, import signatures, pickup tokens and deployment.
5. Frontend/QA: Android integration, error/retry flows, previews, test evidence and user guide.

Each member must understand and commit their own contribution. This folder currently has no Git repository.
Do not claim other members authored generated code. Initialize a repository and agree on ownership before collaboration.
Use synthetic data only.
