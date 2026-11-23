package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.Order
import com.example.canteengo.data.php

@Composable
fun AdminScreen(orders: List<Order>, report: String, onReport: (String) -> Unit,
                onStock: (String, Int) -> Unit, onStaff: () -> Unit) {
    var foodId by rememberSaveable { mutableStateOf("") }
    var stock by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Administration", style = MaterialTheme.typography.headlineMedium)
        Text("${orders.size} orders • ${php(orders.sumOf { it.totalCentavos })} ordered")
        Text("Order totals include unpaid orders. Cash is confirmed at collection.")
        Button(onClick = onStaff) { Text("Manage orders") }
        Text("Set available servings", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(foodId, { foodId = it }, label = { Text("Food ID, e.g. chicken") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(stock, { stock = it.filter(Char::isDigit).take(5) },
            label = { Text("New stock quantity") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Button(onClick = { stock.toIntOrNull()?.let { onStock(foodId.trim(), it) } },
            enabled = foodId.isNotBlank() && stock.toIntOrNull() in 0..10000) { Text("Update stock") }
        Text("Masked order reports", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("json", "csv", "xml").forEach { format ->
                OutlinedButton(onClick = { onReport(format) }) { Text(format.uppercase()) }
            }
        }
        if (report.isNotBlank()) SelectionContainer {
            Text(report, style = MaterialTheme.typography.bodySmall)
        }
    }
}
