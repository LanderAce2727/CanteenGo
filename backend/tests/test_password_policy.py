"""Password rules and API regression checks using an isolated database."""
import pytest
from pydantic import ValidationError
from fastapi.testclient import TestClient
from canteengo.app import create_app
from canteengo.database import connect
from canteengo.schemas import Credentials, RegistrationCredentials
from canteengo.security import password_hash


@pytest.mark.parametrize("password", ["abc12345", "ABCDEFG1", "abc1234!", "abc 1234", "a1" * 64])
def test_registration_accepts_letters_numbers_without_special_requirements(password):
    assert RegistrationCredentials(username="student", password=password).password == password


@pytest.mark.parametrize("password", ["abc1234", "abcdefgh", "12345678", "!!!!!!!!", "a1" * 65])
def test_registration_rejects_short_missing_character_types_and_overlong_passwords(password):
    with pytest.raises(ValidationError):
        RegistrationCredentials(username="student", password=password)


def test_cli_validation_does_not_print_the_password(monkeypatch, capsys):
    from canteengo import cli
    secret = "lettersOnlySecret"
    monkeypatch.setattr("sys.argv", ["canteengo", "create-user", "--username", "student", "--role", "staff"])
    monkeypatch.setattr(cli, "getpass", lambda _prompt: secret)
    with pytest.raises(SystemExit) as error:
        cli.main()
    assert error.value.code == 2
    output = capsys.readouterr()
    assert secret not in output.err + output.out
    assert "letters and numbers" in output.err


def test_login_validation_keeps_legacy_passwords_usable():
    assert Credentials(username="student", password="legacyletters").password == "legacyletters"


def test_eight_character_registration_and_legacy_login_end_to_end(tmp_path):
    path = tmp_path / "password-policy.sqlite3"
    with TestClient(create_app(path)) as client:
        for password in ["abc1234", "abcdefgh", "12345678"]:
            assert client.post("/api/auth/register", json={
                "username": "newstudent", "password": password,
            }).status_code == 422
        body = {"username": "newstudent", "password": "abc12345"}
        assert client.post("/api/auth/register", json=body).status_code == 201
        assert client.post("/api/auth/login", json=body).status_code == 200
        assert client.post("/api/auth/login", json={
            "username": "newstudent", "password": "wrongpass1",
        }).status_code == 401
        with connect(path) as db:
            db.execute("INSERT INTO users(username,password_hash,role) VALUES (?,?,'student')",
                       ("legacy", password_hash("legacyletters")))
        assert client.post("/api/auth/login", json={
            "username": "legacy", "password": "legacyletters",
        }).status_code == 200
