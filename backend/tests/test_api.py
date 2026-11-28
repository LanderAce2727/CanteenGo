"""Isolated API integration/security tests; no production DB or network access."""
from concurrent.futures import ThreadPoolExecutor
import sqlite3
import os
from contextlib import contextmanager
from urllib.parse import urlsplit, urlunsplit, parse_qsl, urlencode
import psycopg
from psycopg import sql
import time
from uuid import uuid4
import pytest
from fastapi.testclient import TestClient
from canteengo.app import create_app
from canteengo.database import connect
from canteengo.security import password_hash, password_matches

PASSWORD = "Synthetic-Test-Pass-42"


@contextmanager
def isolated_target(kind, tmp_path):
    if kind == "sqlite":
        yield tmp_path / "test.sqlite3"
        return
    target = os.getenv("TEST_DATABASE_URL")
    if not target:
        pytest.skip("TEST_DATABASE_URL not configured; PostgreSQL not verified")
    schema = "canteengo_test_" + uuid4().hex
    # Tests use a separate random schema and remove only that schema afterwards.
    with psycopg.connect(target, autocommit=True, sslmode="require") as admin:
        admin.execute(sql.SQL("CREATE SCHEMA {}").format(sql.Identifier(schema)))
        try:
            uri = urlsplit(target)
            query = dict(parse_qsl(uri.query))
            query["options"] = "-csearch_path=" + schema
            yield urlunsplit(uri._replace(query=urlencode(query)))
        finally:
            admin.execute(sql.SQL("DROP SCHEMA {} CASCADE").format(sql.Identifier(schema)))


@pytest.fixture(params=["sqlite", "postgres"])
def storage_target(request, tmp_path):
    with isolated_target(request.param, tmp_path) as target:
        yield target


@pytest.fixture()
def system(storage_target):
    path = storage_target
    app = create_app(path)
    with TestClient(app) as client:
        with connect(path) as db:
            encoded = password_hash(PASSWORD)
            db.executemany("INSERT INTO users(username,password_hash,role) VALUES (?,?,?)", [
                ("alice", encoded, "student"), ("bob", encoded, "student"),
                ("cook", encoded, "staff"), ("manager", encoded, "admin"),
            ])
        tokens = {}
        for name in ["alice", "bob", "cook", "manager"]:
            response = client.post("/api/auth/login", json={"username": name, "password": PASSWORD})
            assert response.status_code == 200
            tokens[name] = {"Authorization": "Bearer " + response.json()["access_token"]}
        yield client, path, tokens


def order_body(**changes):
    body = {"customer": "Sample Student", "items": [{"food_id": "chicken", "quantity": 2}],
            "request_id": str(uuid4())}
    body.update(changes)
    return body


def place(system, body=None):
    client, _, tokens = system
    return client.post("/api/orders", headers=tokens["alice"], json=body or order_body())


def test_health(system):
    assert system[0].get("/api/health").json()["status"] == "ok"


def test_menu_requires_login(system):
    assert system[0].get("/api/menu").status_code == 401


def test_registration_cannot_assign_staff_role(system):
    response = system[0].post("/api/auth/register", json={
        "username": "mallory", "password": PASSWORD, "role": "admin"})
    assert response.status_code == 422


def test_registration_and_duplicate(system):
    client, _, _ = system
    body = {"username": "newstudent", "password": PASSWORD}
    assert client.post("/api/auth/register", json=body).json()["role"] == "student"
    assert client.post("/api/auth/register", json=body).status_code == 409


@pytest.mark.parametrize("password", ["short", "", "a" * 129])
def test_invalid_passwords(system, password):
    assert system[0].post("/api/auth/register", json={
        "username": "newstudent", "password": password}).status_code == 422


def test_salted_password_storage(system):
    with connect(system[1]) as db:
        stored = db.execute("SELECT password_hash FROM users WHERE username='alice'").fetchone()["password_hash"]
    assert PASSWORD not in stored
    assert password_matches(PASSWORD, stored)
    assert not password_matches("wrong password", stored)
    assert password_hash(PASSWORD) != stored


def test_sql_injection_login_is_rejected(system):
    response = system[0].post("/api/auth/login", json={
        "username": "' OR 1=1 --", "password": PASSWORD})
    assert response.status_code == 422


def test_bad_password_and_rate_limit(system):
    client, _, _ = system
    for _ in range(5):
        assert client.post("/api/auth/login", json={
            "username": "unknown", "password": PASSWORD}).status_code == 401
    assert client.post("/api/auth/login", json={
        "username": "unknown", "password": PASSWORD}).status_code == 429


def test_expired_token(system):
    client, path, tokens = system
    with connect(path) as db:
        db.execute("UPDATE sessions SET expires_at=?", (int(time.time()) - 1,))
    assert client.get("/api/orders", headers=tokens["alice"]).status_code == 401


def test_logout_revokes_session(system):
    client, _, tokens = system
    assert client.post("/api/auth/logout", headers=tokens["alice"]).status_code == 204
    assert client.get("/api/auth/me", headers=tokens["alice"]).status_code == 401


def test_price_is_calculated_on_server(system):
    response = place(system)
    assert response.status_code == 201
    assert response.json()["total_centavos"] == 13000
    assert response.json()["number"].startswith("CG-")


def test_client_cannot_supply_price(system):
    assert place(system, order_body(total_centavos=1)).status_code == 422


@pytest.mark.parametrize("quantity", [0, -1, 100, 1.5, "2", True])
def test_quantity_allowlist(system, quantity):
    assert place(system, order_body(items=[{"food_id": "chicken", "quantity": quantity}])).status_code == 422


def test_unknown_food(system):
    assert place(system, order_body(items=[{"food_id": "missing", "quantity": 1}])).status_code == 409


def test_duplicate_food(system):
    body = order_body(items=[{"food_id": "chicken", "quantity": 1}] * 2)
    assert place(system, body).status_code == 422


def test_idempotent_retry(system):
    body = order_body()
    first = place(system, body).json()
    assert place(system, body).json()["number"] == first["number"]
    with connect(system[1]) as db:
        assert db.execute("SELECT stock FROM foods WHERE id='chicken'").fetchone()["stock"] == 98


def test_idempotency_key_cannot_change_order(system):
    body = order_body()
    assert place(system, body).status_code == 201
    body["customer"] = "Different Student"
    assert place(system, body).status_code == 409


def test_other_student_cannot_read_order(system):
    assert place(system).status_code == 201
    client, _, tokens = system
    assert client.get("/api/orders", headers=tokens["bob"]).json() == []
    assert len(client.get("/api/orders", headers=tokens["cook"]).json()) == 1


def test_wrong_role_cannot_update_order(system):
    number = place(system).json()["number"]
    client, _, tokens = system
    assert client.patch(f"/api/orders/{number}/status", headers=tokens["alice"],
                        json={"status": "PREPARING"}).status_code == 403


def test_status_sequence_and_audit(system):
    number = place(system).json()["number"]
    client, _, tokens = system
    url = f"/api/orders/{number}/status"
    assert client.patch(url, headers=tokens["cook"], json={"status": "COMPLETED"}).status_code == 409
    for status in ["PREPARING", "READY", "COMPLETED"]:
        assert client.patch(url, headers=tokens["cook"], json={"status": status}).status_code == 200
    assert client.patch(url, headers=tokens["cook"], json={"status": "READY"}).status_code == 409
    audit = client.get("/api/audit", headers=tokens["manager"]).json()
    assert len(audit) == 4
    assert audit[0]["action"] == "STATUS_COMPLETED"


def test_insufficient_stock_rolls_back_entire_order(system):
    body = order_body(items=[{"food_id": "chicken", "quantity": 1},
                             {"food_id": "missing", "quantity": 1}])
    assert place(system, body).status_code == 409
    with connect(system[1]) as db:
        assert db.execute("SELECT stock FROM foods WHERE id='chicken'").fetchone()["stock"] == 100
        assert db.execute("SELECT COUNT(*) AS count FROM orders").fetchone()["count"] == 0


def test_concurrent_orders_cannot_oversell(system):
    client, path, tokens = system
    with connect(path) as db:
        db.execute("UPDATE foods SET stock=1 WHERE id='chicken'")
    with ThreadPoolExecutor(max_workers=2) as pool:
        responses = list(pool.map(lambda _: place(system, order_body(
            items=[{"food_id": "chicken", "quantity": 1}])), range(2)))
    assert sorted(r.status_code for r in responses) == [201, 409]


@pytest.mark.parametrize("format,content_type", [
    ("json", "application/json"), ("csv", "text/csv"), ("xml", "application/xml")])
def test_report_formats_and_masking(system, format, content_type):
    place(system)
    client, _, tokens = system
    response = client.get("/api/reports/orders", params={"format": format}, headers=tokens["manager"])
    assert response.status_code == 200
    assert content_type in response.headers["content-type"]
    assert "student-***" in response.text
    assert "Sample Student" not in response.text
    assert "alice" not in response.text


def test_staff_cannot_read_admin_report(system):
    assert system[0].get("/api/reports/orders", headers=system[2]["cook"]).status_code == 403


def test_invalid_query_parameters(system):
    client, _, tokens = system
    assert client.get("/api/menu?role=admin", headers=tokens["alice"]).status_code == 422
    assert client.get("/api/reports/orders?format=csv&format=json",
                      headers=tokens["manager"]).status_code == 422


def test_persistence_across_app_restart(system):
    number = place(system).json()["number"]
    with TestClient(create_app(system[1])) as restarted:
        response = restarted.get("/api/orders", headers=system[2]["alice"])
        assert response.json()[0]["number"] == number


def test_holidays_skip_weekend_and_holiday(system, monkeypatch):
    import httpx
    def fake_get(_client, url):
        return httpx.Response(200, json=[{"date": "2026-11-30"}], request=httpx.Request("GET", url))
    monkeypatch.setattr(httpx.Client, "get", fake_get)
    response = system[0].request("GET", "/api/operating-days?start=2026-11-28&count=2", headers=system[2]["alice"])
    assert response.json()["operating_days"] == ["2026-12-01", "2026-12-02"]


def test_holiday_upstream_error_is_explicit(system, monkeypatch):
    import httpx
    def fail(_client, url):
        raise httpx.ReadTimeout("upstream timed out")
    monkeypatch.setattr(httpx.Client, "get", fail)
    response = system[0].request("GET", "/api/operating-days?start=2026-11-28", headers=system[2]["alice"])
    assert response.status_code == 503
