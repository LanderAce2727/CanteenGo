"""SQLite/PostgreSQL storage. All request-supplied values use bound parameters."""
from contextlib import contextmanager
from pathlib import Path
import json
import sqlite3
import os
from urllib.parse import urlsplit

import psycopg
from psycopg.rows import dict_row

INTEGRITY_ERRORS = (sqlite3.IntegrityError, psycopg.IntegrityError)

SCHEMA = """
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY, username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL, role TEXT NOT NULL CHECK(role IN ('student','staff','admin'))
);
CREATE TABLE IF NOT EXISTS sessions (
    token_hash TEXT PRIMARY KEY, user_id INTEGER NOT NULL REFERENCES users(id),
    expires_at INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS login_attempts (
    username TEXT NOT NULL, attempted_at INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS foods (
    id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL,
    category TEXT NOT NULL, price_centavos INTEGER NOT NULL CHECK(price_centavos > 0),
    emoji TEXT NOT NULL, stock INTEGER NOT NULL CHECK(stock >= 0)
);
CREATE TABLE IF NOT EXISTS orders (
    number TEXT PRIMARY KEY, user_id INTEGER NOT NULL REFERENCES users(id),
    customer TEXT NOT NULL, items TEXT NOT NULL, total_centavos INTEGER NOT NULL,
    status TEXT NOT NULL, created_at INTEGER NOT NULL,
    request_id TEXT NOT NULL, UNIQUE(user_id, request_id)
);
CREATE TABLE IF NOT EXISTS audit (
    id INTEGER PRIMARY KEY, actor INTEGER NOT NULL REFERENCES users(id),
    action TEXT NOT NULL, target TEXT NOT NULL, created_at INTEGER NOT NULL
);
"""

def database_target(explicit=None):
    """Explicit test/CLI targets take priority; never silently use SQLite on Render."""
    if explicit is not None:
        return str(explicit)
    configured = os.getenv("DATABASE_URL")
    if configured is not None:
        if not configured.strip():
            raise ValueError("DATABASE_URL must not be empty")
        validate_postgres_url(configured)
        return configured
    if os.getenv("RENDER") == "true":
        raise ValueError("Set DATABASE_URL to the Neon connection string in Render")
    return os.getenv("CANTEENGO_DB", "data/canteengo.sqlite3")


def is_postgres(target):
    return str(target).startswith(("postgresql://", "postgres://"))


def validate_postgres_url(target):
    try:
        uri = urlsplit(target)
        valid = uri.scheme in ("postgresql", "postgres") and bool(uri.hostname) and bool(uri.path.strip("/"))
    except ValueError:
        valid = False
    if not valid:
        raise ValueError("DATABASE_URL must be a PostgreSQL connection URL")


class PostgresConnection:
    """Adapt this application's fixed qmark SQL; values always remain bound parameters."""

    def __init__(self, connection):
        self.connection = connection

    def execute(self, statement, parameters=()):
        # Only developer-authored SQL is accepted here; never format request data into SQL.
        return self.connection.execute(statement.replace("?", "%s"), parameters)

    def executemany(self, statement, parameters):
        with self.connection.cursor() as cursor:
            cursor.executemany(statement.replace("?", "%s"), parameters)


@contextmanager
def connect(path):
    if is_postgres(path):
        validate_postgres_url(path)
        # Require TLS even if a pasted URL omits sslmode.
        options = psycopg.conninfo.conninfo_to_dict(str(path))
        if options.get("sslmode") not in ("require", "verify-ca", "verify-full"):
            options["sslmode"] = "require"
        options["connect_timeout"] = 10
        with psycopg.connect(**options, row_factory=dict_row) as db:
            yield PostgresConnection(db)
        return
    db = sqlite3.connect(path, timeout=10)
    db.row_factory = sqlite3.Row
    db.execute("PRAGMA foreign_keys=ON")
    try:
        with db:
            yield db
    finally:
        db.close()


def begin_login_write(db):
    """Keep PostgreSQL login-attempt counting and insertion in one serialized write."""
    if isinstance(db, PostgresConnection):
        db.execute("LOCK TABLE login_attempts IN SHARE ROW EXCLUSIVE MODE")


def begin_order_write(db):
    """Serialize checkout/status writes, preserving retries and stock reservations."""
    if isinstance(db, PostgresConnection):
        # Small-campus workload: table locks serialize duplicate keys and stock reads.
        # Locks are held until transaction commit/rollback and conflict with stock updates.
        db.execute("LOCK TABLE orders, foods IN SHARE ROW EXCLUSIVE MODE")
    else:
        db.execute("BEGIN IMMEDIATE")


def initialize(path):
    postgres = is_postgres(path)
    if not postgres:
        Path(path).parent.mkdir(parents=True, exist_ok=True)
    with connect(path) as db:
        if postgres:
            # Serialize simultaneous startups before creating/seeding shared tables.
            db.execute("SELECT pg_advisory_xact_lock(731204312)")
            schema = SCHEMA.replace("id INTEGER PRIMARY KEY,", "id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,")
            for statement in schema.split(";"):
                if statement.strip():
                    db.execute(statement)
        else:
            db.execute("PRAGMA journal_mode=WAL")
            db.executescript(SCHEMA)
        if not db.execute("SELECT 1 FROM foods LIMIT 1").fetchone():
            menu = json.loads((Path(__file__).parent / "sample_menu.json").read_text(encoding="utf-8"))
            db.executemany(
                "INSERT INTO foods(id,name,description,category,price_centavos,emoji,stock) VALUES (?,?,?,?,?,?,?)",
                [(food["id"], food["name"], food["description"], food["category"],
                  food["price_centavos"], food["emoji"], food["stock"]) for food in menu],
            )
