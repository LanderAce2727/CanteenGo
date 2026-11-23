"""SQLite storage. All request-supplied values use bound parameters."""
from contextlib import contextmanager
from pathlib import Path
import json
import sqlite3

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

@contextmanager
def connect(path):
    db = sqlite3.connect(path, timeout=10)
    db.row_factory = sqlite3.Row
    db.execute("PRAGMA foreign_keys=ON")
    try:
        with db:
            yield db
    finally:
        db.close()

def initialize(path):
    Path(path).parent.mkdir(parents=True, exist_ok=True)
    with connect(path) as db:
        db.execute("PRAGMA journal_mode=WAL")
        db.executescript(SCHEMA)
        # Synthetic starter menu, explicitly seeded only for a new database.
        if not db.execute("SELECT 1 FROM foods LIMIT 1").fetchone():
            menu = json.loads((Path(__file__).parent / "sample_menu.json").read_text(encoding="utf-8"))
            db.executemany(
                "INSERT INTO foods VALUES (:id,:name,:description,:category,:price_centavos,:emoji,:stock)",
                menu,
            )
