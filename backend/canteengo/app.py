"""CanteenGo REST API. Run from backend with python -m uvicorn canteengo.app:app."""
from contextlib import asynccontextmanager
from datetime import date, timedelta
from typing import Literal
import csv
import io
import json
import os
from pathlib import Path
import secrets
import time
import uuid
from xml.etree.ElementTree import Element, SubElement, tostring

from dotenv import load_dotenv
from fastapi import Depends, FastAPI, HTTPException, Query, Request, Response
from fastapi.responses import JSONResponse
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
import httpx

from .database import connect, initialize, database_target, begin_order_write, begin_login_write, INTEGRITY_ERRORS
from .schemas import Credentials, RegistrationCredentials, NewOrder, StatusChange, StockChange
from .security import password_hash, password_matches, token_hash

load_dotenv(Path(__file__).resolve().parents[1] / ".env")
BEARER = HTTPBearer(auto_error=False)
SESSION_SECONDS = 8 * 60 * 60
STATUSES = ["PLACED", "PREPARING", "READY", "COMPLETED"]


def order_json(row):
    return {
        "number": row["number"], "customer": row["customer"],
        "items": json.loads(row["items"]), "total_centavos": row["total_centavos"],
        "status": row["status"], "created_at": row["created_at"],
    }


def create_app(db_path=None):
    path = database_target(db_path)
    dummy_hash = password_hash(secrets.token_urlsafe(32))

    @asynccontextmanager
    async def lifespan(_app):
        initialize(path)
        yield

    api = FastAPI(title="CanteenGo API", version="0.1.0", lifespan=lifespan)
    api.state.db_path = path

    @api.middleware("http")
    async def query_allowlist(request: Request, call_next):
        allowed = {
            "/api/reports/orders": {"format"},
            "/api/operating-days": {"start", "count"},
        }.get(request.url.path, set())
        keys = list(request.query_params.keys())
        if request.url.path.startswith("/api/") and (
            set(keys) - allowed or any(len(request.query_params.getlist(k)) > 1 for k in keys)
        ):
            return JSONResponse(status_code=422, content={"detail": "Unknown or duplicate query parameter"})
        response = await call_next(request)
        response.headers["Cache-Control"] = "no-store"
        response.headers["X-Content-Type-Options"] = "nosniff"
        return response

    def current_user(credentials: HTTPAuthorizationCredentials | None = Depends(BEARER)):
        if credentials is None:
            raise HTTPException(401, "Please sign in", headers={"WWW-Authenticate": "Bearer"})
        with connect(path) as db:
            user = db.execute(
                "SELECT u.id,u.username,u.role FROM sessions s JOIN users u ON u.id=s.user_id "
                "WHERE s.token_hash=? AND s.expires_at>?",
                (token_hash(credentials.credentials), int(time.time())),
            ).fetchone()
        if user is None:
            raise HTTPException(401, "Session expired or invalid", headers={"WWW-Authenticate": "Bearer"})
        return dict(user)

    def staff(user=Depends(current_user)):
        if user["role"] not in ("staff", "admin"):
            raise HTTPException(403, "Staff access required")
        return user

    def admin(user=Depends(current_user)):
        if user["role"] != "admin":
            raise HTTPException(403, "Administrator access required")
        return user

    @api.get("/api/health")
    def health():
        with connect(path) as db:
            db.execute("SELECT 1")
        return {"status": "ok", "service": "CanteenGo"}

    @api.post("/api/auth/register", status_code=201)
    def register(body: RegistrationCredentials):
        # Public registration ALWAYS creates students; roles cannot be supplied by clients.
        encoded = password_hash(body.password)
        try:
            with connect(path) as db:
                db.execute("INSERT INTO users(username,password_hash,role) VALUES (?,?,'student')",
                           (body.username, encoded))
        except INTEGRITY_ERRORS:
            raise HTTPException(409, "Username already registered")
        return {"username": body.username, "role": "student"}

    @api.post("/api/auth/login")
    def login(body: Credentials):
        now = int(time.time())
        with connect(path) as db:
            begin_login_write(db)
            db.execute("DELETE FROM login_attempts WHERE attempted_at<=?", (now - 60,))
            attempts = db.execute("SELECT COUNT(*) AS attempts FROM login_attempts WHERE username=?",
                                  (body.username,)).fetchone()["attempts"]
            if attempts >= 5:
                raise HTTPException(429, "Too many attempts; wait one minute",
                                    headers={"Retry-After": "60"})
            db.execute("INSERT INTO login_attempts VALUES (?,?)", (body.username, now))
            row = db.execute("SELECT * FROM users WHERE username=?", (body.username,)).fetchone()
        if not password_matches(body.password, row["password_hash"] if row else dummy_hash) or row is None:
            raise HTTPException(401, "Incorrect username or password")
        raw_token = secrets.token_urlsafe(32)
        with connect(path) as db:
            db.execute("DELETE FROM sessions WHERE expires_at<=?", (now,))
            db.execute("INSERT INTO sessions VALUES (?,?,?)",
                       (token_hash(raw_token), row["id"], now + SESSION_SECONDS))
            db.execute("DELETE FROM login_attempts WHERE username=?", (body.username,))
        return {"access_token": raw_token, "token_type": "bearer", "expires_in": SESSION_SECONDS,
                "username": row["username"], "role": row["role"]}

    @api.post("/api/auth/logout", status_code=204)
    def logout(user=Depends(current_user), credentials=Depends(BEARER)):
        with connect(path) as db:
            db.execute("DELETE FROM sessions WHERE token_hash=?", (token_hash(credentials.credentials),))
        return Response(status_code=204)

    @api.get("/api/auth/me")
    def me(user=Depends(current_user)):
        return {"username": user["username"], "role": user["role"]}

    @api.get("/api/menu")
    def menu(user=Depends(current_user)):
        with connect(path) as db:
            rows = db.execute("SELECT * FROM foods ORDER BY id").fetchall()
        return [dict(row) for row in rows if row["stock"] > 0]

    @api.post("/api/orders", status_code=201)
    def place_order(body: NewOrder, user=Depends(current_user)):
        if user["role"] != "student":
            raise HTTPException(403, "Student account required to place an order")
        ids = [line.food_id for line in body.items]
        if len(ids) != len(set(ids)):
            raise HTTPException(422, "Combine duplicate food items")
        with connect(path) as db:
            # Lock before reading stock; simultaneous orders cannot oversell.
            begin_order_write(db)
            previous = db.execute("SELECT * FROM orders WHERE user_id=? AND request_id=?",
                                  (user["id"], str(body.request_id))).fetchone()
            if previous:
                old = {item["food"]["id"]: item["quantity"] for item in json.loads(previous["items"])}
                new = {line.food_id: line.quantity for line in body.items}
                if previous["customer"] != body.customer or old != new:
                    raise HTTPException(409, "Request ID already used for another order")
                return order_json(previous)
            items = []
            total = 0
            for line in body.items:
                food = db.execute("SELECT * FROM foods WHERE id=?", (line.food_id,)).fetchone()
                if food is None or food["stock"] < line.quantity:
                    raise HTTPException(409, "An item is unavailable; refresh the menu")
                snapshot = dict(food)
                snapshot.pop("stock")
                items.append({"food": snapshot, "quantity": line.quantity})
                total += food["price_centavos"] * line.quantity
                db.execute("UPDATE foods SET stock=stock-? WHERE id=?", (line.quantity, line.food_id))
            number = "CG-" + uuid.uuid4().hex[:12].upper()
            now = int(time.time())
            db.execute("INSERT INTO orders VALUES (?,?,?,?,?,'PLACED',?,?)",
                       (number, user["id"], body.customer, json.dumps(items), total, now, str(body.request_id)))
            db.execute("INSERT INTO audit(actor,action,target,created_at) VALUES (?,'ORDER_PLACED',?,?)",
                       (user["id"], number, now))
            result = db.execute("SELECT * FROM orders WHERE number=?", (number,)).fetchone()
            return order_json(result)

    @api.get("/api/orders")
    def orders(user=Depends(current_user)):
        with connect(path) as db:
            if user["role"] == "student":
                rows = db.execute("SELECT * FROM orders WHERE user_id=? ORDER BY created_at DESC,number DESC",
                                  (user["id"],)).fetchall()
            else:
                rows = db.execute("SELECT * FROM orders ORDER BY created_at DESC,number DESC").fetchall()
        return [order_json(row) for row in rows]

    @api.patch("/api/orders/{number}/status")
    def advance(number: str, body: StatusChange, user=Depends(staff)):
        with connect(path) as db:
            begin_order_write(db)
            row = db.execute("SELECT * FROM orders WHERE number=?", (number,)).fetchone()
            if row is None:
                raise HTTPException(404, "Order not found")
            index = STATUSES.index(row["status"])
            if index == len(STATUSES) - 1 or STATUSES[index + 1] != body.status:
                raise HTTPException(409, "Order must advance one step at a time")
            db.execute("UPDATE orders SET status=? WHERE number=?", (body.status, number))
            # COMPLETED means the staff member explicitly confirms cash receipt and collection.
            db.execute("INSERT INTO audit(actor,action,target,created_at) VALUES (?,?,?,?)",
                       (user["id"], "STATUS_" + body.status, number, int(time.time())))
            return order_json(db.execute("SELECT * FROM orders WHERE number=?", (number,)).fetchone())

    @api.patch("/api/menu/{food_id}/stock")
    def stock(food_id: str, body: StockChange, user=Depends(admin)):
        with connect(path) as db:
            if db.execute("UPDATE foods SET stock=? WHERE id=?", (body.stock, food_id)).rowcount != 1:
                raise HTTPException(404, "Food not found")
            db.execute("INSERT INTO audit(actor,action,target,created_at) VALUES (?,'STOCK_UPDATED',?,?)",
                       (user["id"], food_id, int(time.time())))
        return {"id": food_id, "stock": body.stock}

    @api.get("/api/reports/orders")
    def export(format: Literal["json", "csv", "xml"] = "json", user=Depends(admin)):
        with connect(path) as db:
            rows = db.execute("SELECT number,user_id,total_centavos,status,created_at FROM orders "
                              "ORDER BY created_at DESC").fetchall()
        records = [{"number": row["number"], "student_id": "student-***" + str(row["user_id"])[-2:],
                    "total_centavos": row["total_centavos"], "status": row["status"],
                    "created_at": row["created_at"]} for row in rows]
        if format == "json":
            return records
        if format == "csv":
            buffer = io.StringIO()
            writer = csv.DictWriter(buffer, fieldnames=["number", "student_id", "total_centavos", "status", "created_at"])
            writer.writeheader()
            writer.writerows(records)
            return Response(buffer.getvalue(), media_type="text/csv",
                            headers={"Content-Disposition": 'attachment; filename="orders.csv"'})
        root = Element("orders")
        for record in records:
            node = SubElement(root, "order")
            for key, value in record.items():
                SubElement(node, key).text = str(value)
        return Response(tostring(root, encoding="utf-8"), media_type="application/xml")

    @api.get("/api/audit")
    def audit(user=Depends(admin)):
        with connect(path) as db:
            return [dict(row) for row in db.execute("SELECT * FROM audit ORDER BY id DESC LIMIT 500")]

    @api.get("/api/operating-days")
    def operating_days(start: date, count: int = Query(default=1, ge=1, le=30),
                       user=Depends(current_user)):
        # No fabricated holiday fallback: upstream failures are explicit and retryable.
        years = {start.year, (start + timedelta(days=120)).year}
        holidays = set()
        try:
            with httpx.Client(timeout=8, follow_redirects=False) as client:
                for year in sorted(years):
                    response = client.get(f"https://date.nager.at/api/v3/PublicHolidays/{year}/PH")
                    response.raise_for_status()
                    payload = response.json()
                    if not isinstance(payload, list):
                        raise ValueError("Holiday response must be a list")
                    for item in payload:
                        holidays.add(date.fromisoformat(item["date"]))
        except (httpx.HTTPError, ValueError, KeyError, TypeError):
            raise HTTPException(503, "Holiday service unavailable; please retry")
        day = start
        result = []
        for _ in range(120):
            if day.weekday() < 5 and day not in holidays:
                result.append(day.isoformat())
                if len(result) == count:
                    return {"operating_days": result, "source": "Nager.Date"}
            day += timedelta(days=1)
        raise HTTPException(503, "Could not determine operating days")

    return api


app = create_app()
