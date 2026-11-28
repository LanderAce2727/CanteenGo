"""Configuration and adapter tests; no cloud credentials or network required."""
from unittest.mock import MagicMock
import pytest
from canteengo.database import (
    database_target, validate_postgres_url, PostgresConnection, begin_order_write, connect,
)


def test_explicit_sqlite_target_ignores_cloud_environment(monkeypatch, tmp_path):
    monkeypatch.setenv("RENDER", "true")
    monkeypatch.setenv("DATABASE_URL", "not-a-url")
    assert database_target(tmp_path / "test.db") == str(tmp_path / "test.db")


def test_render_cannot_fall_back_to_ephemeral_sqlite(monkeypatch):
    monkeypatch.delenv("DATABASE_URL", raising=False)
    monkeypatch.setenv("RENDER", "true")
    with pytest.raises(ValueError, match="Set DATABASE_URL"):
        database_target()


@pytest.mark.parametrize("target", ["", "sqlite:///local.db", "postgresql:///missing-host", "not-a-url"])
def test_bad_database_configuration_fails_without_echoing_value(monkeypatch, target):
    monkeypatch.setenv("DATABASE_URL", target)
    with pytest.raises(ValueError) as error:
        database_target()
    if target:
        assert target not in str(error.value)


def test_cloud_url_overrides_sqlite(monkeypatch):
    target = "postgresql://user:placeholder@db.example/test?sslmode=require"
    monkeypatch.setenv("DATABASE_URL", target)
    monkeypatch.setenv("CANTEENGO_DB", "ignored.db")
    assert database_target() == target


def test_adapter_binds_values_without_interpolating_them():
    connection = MagicMock()
    db = PostgresConnection(connection)
    values = ("name'; DROP TABLE users; --",)
    db.execute("SELECT * FROM users WHERE username=?", values)
    connection.execute.assert_called_once_with("SELECT * FROM users WHERE username=%s", values)


def test_order_lock_is_database_specific():
    postgres = MagicMock()
    begin_order_write(PostgresConnection(postgres))
    postgres.execute.assert_called_once_with("LOCK TABLE orders, foods IN SHARE ROW EXCLUSIVE MODE", ())
    sqlite = MagicMock()
    begin_order_write(sqlite)
    sqlite.execute.assert_called_once_with("BEGIN IMMEDIATE")


def test_postgres_connections_require_tls(monkeypatch):
    factory = MagicMock()
    monkeypatch.setattr("canteengo.database.psycopg.connect", factory)
    with connect("postgresql://user:placeholder@db.example/test?sslmode=disable"):
        pass
    assert factory.call_args.kwargs["sslmode"] == "require"
    assert factory.call_args.kwargs["connect_timeout"] == 10


def test_verified_tls_is_preserved(monkeypatch):
    factory = MagicMock()
    monkeypatch.setattr("canteengo.database.psycopg.connect", factory)
    with connect("postgresql://user:placeholder@db.example/test?sslmode=verify-full"):
        pass
    assert factory.call_args.kwargs["sslmode"] == "verify-full"
