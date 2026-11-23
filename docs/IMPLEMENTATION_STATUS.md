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

## Suggested ownership

1. Lead: approval, architecture, emerging technology justification, milestones and final presentation.
2. API developer: maintain backend endpoints, independent menu source and holiday behavior.
3. Integration engineer: CSV/XML mappings, signed imports, exports and scheduled processing.
4. Security engineer: review access controls, import signatures, pickup tokens and deployment.
5. Frontend/QA: Android integration, error/retry flows, previews, test evidence and user guide.

Each member must understand and commit their own contribution. This folder currently has no Git repository.
Do not claim other members authored generated code. Initialize a repository and agree on ownership before collaboration.
Use synthetic data only.
