"""Local administrator tool. Passwords are prompted, never passed on the command line."""
import argparse
from getpass import getpass
import os
import sqlite3
from .app import app
from .database import connect, initialize
from .schemas import Credentials
from .security import password_hash


def main():
    parser = argparse.ArgumentParser(description="Create a CanteenGo account on the server")
    parser.add_argument("command", choices=["create-user"])
    parser.add_argument("--username", required=True)
    parser.add_argument("--role", required=True, choices=["student", "staff", "admin"])
    args = parser.parse_args()
    password = getpass("Password (12+ characters): ")
    if password != getpass("Confirm password: "):
        parser.error("Passwords do not match")
    credentials = Credentials(username=args.username, password=password)
    path = app.state.db_path
    initialize(path)
    try:
        with connect(path) as db:
            db.execute("INSERT INTO users(username,password_hash,role) VALUES (?,?,?)",
                       (credentials.username, password_hash(credentials.password), args.role))
    except sqlite3.IntegrityError:
        parser.error("Username already exists")
    print(f"Created {args.role} account: {credentials.username}")


if __name__ == "__main__":
    main()
