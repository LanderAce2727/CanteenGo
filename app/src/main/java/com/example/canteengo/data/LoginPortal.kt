package com.example.canteengo.data

/** Portal choice guides navigation; privileges always come from the authenticated server role. */
enum class LoginPortal(val title: String) {
    CUSTOMER("Customer Login"),
    ADMIN("Admin Login");

    fun acceptsRole(role: String): Boolean = when (this) {
        CUSTOMER -> role == "student"
        ADMIN -> role == "admin" || role == "staff"
    }
}
