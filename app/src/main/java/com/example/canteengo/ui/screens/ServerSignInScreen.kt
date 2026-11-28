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
import androidx.compose.ui.unit.dp

@Composable
fun ServerSignInScreen(busy: Boolean, onConnect: (String, String, Boolean) -> Unit, onBack: () -> Unit) {
    var username by rememberSaveable { mutableStateOf("") }
    // Password is deliberately not saved to the activity state.
    var password by remember { mutableStateOf("") }
    var registering by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(if (registering) "Create your account" else "Sign in to CanteenGo", style = MaterialTheme.typography.headlineMedium)
        Text("Sign in to save orders and share updates with the canteen.")
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
            onClick = { onConnect(username, password, registering) },
            enabled = !busy && username.isNotBlank() && password.length in 12..128,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (registering) "Create student account & sign in" else "Sign in") }
        TextButton(onClick = { registering = !registering }, enabled = !busy) {
            Text(if (registering) "Already registered? Sign in" else "Create a student account")
        }
        TextButton(onClick = onBack, enabled = !busy) { Text("Back") }
    }
}
