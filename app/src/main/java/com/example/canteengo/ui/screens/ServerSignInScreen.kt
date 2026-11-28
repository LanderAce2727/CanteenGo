package com.example.canteengo.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.example.canteengo.ui.theme.CanteenGoTheme
import com.example.canteengo.data.LoginPortal

/** Connect the designed auth screens to the existing server authentication flow. */
@Composable
fun ServerSignInScreen(
    busy: Boolean,
    onConnect: (String, String, Boolean) -> Unit,
    onBack: () -> Unit,
    portal: LoginPortal = LoginPortal.CUSTOMER
) {
    var registering by rememberSaveable { mutableStateOf(false) }
    if (registering && portal == LoginPortal.CUSTOMER) {
        SignUpScreen(
            onSignUp = { username, password -> onConnect(username, password, true) },
            onLogin = { if (!busy) registering = false },
            busy = busy
        )
    } else {
        LoginScreen(
            onLogin = { username, password -> onConnect(username, password, false) },
            onGuest = onBack,
            onSignUp = { if (!busy && portal == LoginPortal.CUSTOMER) registering = true },
            busy = busy,
            portal = portal
        )
    }
}

@Preview(name = "Cloud sign in", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ServerSignInScreenPreview() {
    CanteenGoTheme {
        Surface {
            ServerSignInScreen(false, onConnect = { _, _, _ -> }, onBack = {})
        }
    }
}
