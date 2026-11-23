"""Password and session helpers; no plaintext password or token is stored."""
import hashlib
import hmac
import secrets

ITERATIONS = 600_000

def password_hash(password: str) -> str:
    salt = secrets.token_hex(16)
    digest = hashlib.pbkdf2_hmac("sha256", password.encode(), bytes.fromhex(salt), ITERATIONS)
    return f"pbkdf2_sha256$" + str(ITERATIONS) + "$" + salt + "$" + digest.hex()

def password_matches(password: str, encoded: str) -> bool:
    try:
        algorithm, iterations, salt, expected = encoded.split("$")
        if algorithm != "pbkdf2_sha256":
            return False
        digest = hashlib.pbkdf2_hmac("sha256", password.encode(), bytes.fromhex(salt), int(iterations))
        return hmac.compare_digest(digest.hex(), expected)
    except (ValueError, TypeError):
        return False

def token_hash(token: str) -> str:
    return hashlib.sha256(token.encode()).hexdigest()
