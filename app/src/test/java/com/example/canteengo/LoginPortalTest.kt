package com.example.canteengo

import com.example.canteengo.data.LoginPortal
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginPortalTest {
    @Test
    fun customerPortalAcceptsOnlyStudentRole() {
        assertTrue(LoginPortal.CUSTOMER.acceptsRole("student"))
        listOf("admin", "staff", "", "unknown").forEach {
            assertFalse(LoginPortal.CUSTOMER.acceptsRole(it))
        }
    }

    @Test
    fun adminPortalAcceptsPrivilegedRolesOnly() {
        assertTrue(LoginPortal.ADMIN.acceptsRole("admin"))
        assertTrue(LoginPortal.ADMIN.acceptsRole("staff"))
        listOf("student", "", "unknown").forEach {
            assertFalse(LoginPortal.ADMIN.acceptsRole(it))
        }
    }
}
