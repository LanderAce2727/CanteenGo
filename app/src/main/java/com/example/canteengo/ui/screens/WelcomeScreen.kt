package com.example.canteengo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canteengo.ui.theme.CanteenGoTheme

@Composable
fun WelcomeScreen(onStudent: () -> Unit, onStaff: () -> Unit, onConnect: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClickLabel = "Continue to menu", role = Role.Button, onClick = onStudent)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🍊", fontSize = 72.sp)
        Text("CanteenGo", style = MaterialTheme.typography.titleLarge)
        Text(
            "Craving something?\nGood food is a tap away.",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        Text(
            "Pick your favorites. Skip the queue.\nOrder ahead and pay cash at pickup.",
            textAlign = TextAlign.Center
        )
        Text(
            "Tap anywhere to continue",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        TextButton(onClick = onConnect) { Text("Sign in to server") }
        TextButton(onClick = onStaff) { Text("Staff demo") }
        Text("Tap to explore the local demo. Sign in to save real orders.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}

@Preview(name = "Welcome", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WelcomeScreenPreview() {
    CanteenGoTheme {
        Surface {
            WelcomeScreen(onStudent = {}, onStaff = {})
        }
    }
}
