# CanteenGo API specification — first working version

Interactive schemas and request examples are at GET /docs and GET /openapi.json.
All /api endpoints return JSON unless the export format is CSV/XML.
Authenticated requests use `Authorization: Bearer <token>`.
Use placeholders in documentation; never paste an actual token into Git.

| Method | Path | Role | Body/query | Success |
| --- | --- | --- | --- | --- |
| GET | /api/health | Public | None | 200 health |
| POST | /api/auth/register | Public | username, password | 201 student account |
| POST | /api/auth/login | Public | username, password | 200 token, expiry, username, role |
| POST | /api/auth/logout | Signed in | None | 204 |
| GET | /api/auth/me | Signed in | None | 200 username, role |
| GET | /api/menu | Signed in | None | 200 available food array |
| GET | /api/orders | Signed in | None | 200 own orders for students; all for staff/admin |
| POST | /api/orders | Student | customer, items, request_id | 201 saved order |
| PATCH | /api/orders/{number}/status | Staff/admin | status | 200 updated order |
| PATCH | /api/menu/{food_id}/stock | Admin | stock | 200 updated stock |
| GET | /api/reports/orders | Admin | format=json/csv/xml | 200 masked report |
| GET | /api/audit | Admin | None | 200 latest 500 audit entries |
| GET | /api/operating-days | Signed in | start=YYYY-MM-DD, count=1..30 | 200 next operating dates |

Status codes: 401 invalid/expired session or login, 403 wrong role, 404 unknown resource,
409 duplicate username/insufficient stock/invalid status transition/idempotency conflict,
422 invalid input, 429 login rate limit, 503 unavailable holiday service.
Unexpected server errors use normal 500 responses without exposing debug tracebacks.

## Checkout example

```json
{
  "customer": "Sample Student",
  "items": [{"food_id": "chicken", "quantity": 2}],
  "request_id": "69473aba-76fc-4e9a-9bd9-950b19d4b18d"
}
```

Generate a new UUID for each intended order and retain it for retries of that same checkout.
Price and totals are calculated by the server. Money uses integer Philippine centavos.
Maximum: 30 unique lines, each with a quantity of 1–99.
Stock is reserved atomically on placement. Cancellation/refund flows are not part of this version.
A saved food snapshot preserves historical prices and descriptions.

Order flow: PLACED -> PREPARING -> READY -> COMPLETED.
The final action records the staff member's confirmation of cash receipt and collection.

The operating-days endpoint includes the starting date if it is an eligible weekday.
It excludes Philippine public holidays and weekends, asks Nager.Date for relevant years,
and returns 503 on upstream timeouts, HTTP errors or malformed payloads.
It does not yet restrict checkout dates or account for special canteen closures.

## Primary implementation references

- https://fastapi.tiangolo.com/tutorial/security/
- https://fastapi.tiangolo.com/tutorial/testing/
- https://docs.python.org/3/library/sqlite3.html
- https://docs.python.org/3/library/hashlib.html
