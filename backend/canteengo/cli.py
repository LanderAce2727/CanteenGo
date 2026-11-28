"""Local administrator tool. Passwords are prompted, never passed on the command line."""
import argparse
from getpass import getpass
import os
from pydantic import ValidationError
from .app import app
from .database import connect, initialize, INTEGRITY_ERRORS
from .schemas import RegistrationCredentials
from .security import password_hash


def main():
    parser = argparse.ArgumentParser(description="Create a CanteenGo account on the server")
    parser.add_argument("command", choices=["create-user"])
    parser.add_argument("--username", required=True)
    parser.add_argument("--role", required=True, choices=["student", "staff", "admin"])
    args = parser.parse_args()
    password = getpass("Password (8+ characters, letters and numbers): ")
    if password != getpass("Confirm password: "):
        parser.error("Passwords do not match")
    try:
        credentials = RegistrationCredentials(username=args.username, password=password)
    except ValidationError:
        parser.error("Use a valid username and an 8–128 character password with letters and numbers")
    path = app.state.db_path
    initialize(path)
    try:
        with connect(path) as db:
            db.execute("INSERT INTO users(username,password_hash,role) VALUES (?,?,?)",
                       (credentials.username, password_hash(credentials.password), args.role))
    except INTEGRITY_ERRORS:
        parser.error("Username already exists")
    print(f"Created {args.role} account: {credentials.username}")


if __name__ == "__main__":
    main()
