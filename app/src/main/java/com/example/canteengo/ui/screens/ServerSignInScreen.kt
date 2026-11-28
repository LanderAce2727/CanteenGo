package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canteengo.BuildConfig
import com.example.canteengo.ui.theme.CanteenGoTheme

@Composable
fun ServerSignInScreen(busy: Boolean, onConnect: (String, String, String, Boolean) -> Unit, onBack: () -> Unit) {
    var address by rememberSaveable { mutableStateOf(if (BuildConfig.DEBUG) "http://10.0.2.2:8000" else "") }
    var username by rememberSaveable { mutableStateOf("") }
    // Password is deliberately not saved to the activity state.
    var password by remember { mutableStateOf("") }
    var registering by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Connect to CanteenGo", style = MaterialTheme.typography.headlineMedium)
        Text("Sign in to save orders and share updates with the canteen.")
        OutlinedTextField(address, { address = it }, label = { Text("Server address") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth())
        if (BuildConfig.DEBUG) Text("Emulator: http://10.0.2.2:8000. Phone: use your computer’s Wi-Fi IP address.",
            style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(username, { username = it }, label = { Text("Username") },
            supportingText = { Text("3–40 lowercase letters, numbers, dots, underscores or hyphens") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(password, { password = it }, label = { Text("Password") },
            supportingText = { Text("12–128 characters") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth())
        Text(if (registering) "New accounts are students. Staff and administrator accounts are created by the server administrator."
            else "Your account determines which features you can access.")
        Button(
            onClick = { onConnect(address, username, password, registering) },
            enabled = !busy && address.isNotBlank() && username.isNotBlank() && password.length in 12..128,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (registering) "Create student account & sign in" else "Sign in") }
        TextButton(onClick = { registering = !registering }, enabled = !busy) {
            Text(if (registering) "Already registered? Sign in" else "Create a student account")
        }
        TextButton(onClick = onBack, enabled = !busy) { Text("Back") }
    }
}

@Preview(name = "Server Sign In", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ServerSignInScreenPreview() {
    CanteenGoTheme {
        Surface {
            ServerSignInScreen(
                busy = false,
                onConnect = { _, _, _, _ -> },
                onBack = {}
            )
        }
    }
}
