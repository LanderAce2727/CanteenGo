package com.example.canteengo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.FoodItem
import com.example.canteengo.data.php

/** Uses server callbacks; menu creation/editing remain available in the local demo only. */
@Composable
fun CloudInventoryScreen(
    foods: List<FoodItem>,
    onStock: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    var foodId by rememberSaveable { mutableStateOf("") }
    var servings by rememberSaveable { mutableStateOf("") }
    val peach = Color(0xFFFFF0DC)
    val cream = Color(0xFFFFFAF4)
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(peach),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextButton(onClick = onBack) { Text("Back to dashboard") }
            Text("Manage inventory", style = MaterialTheme.typography.headlineMedium)
            Text("Select a food or enter its ID to set available servings. Sold-out foods can be restocked by ID.")
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = cream), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        foodId, { foodId = it }, label = { Text("Food ID") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        servings, { servings = it.filter(Char::isDigit).take(5) },
                        label = { Text("Available servings (0–10000)") },
                        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { servings.toIntOrNull()?.let { onStock(foodId.trim(), it) } },
                        enabled = foodId.trim().matches(Regex("[a-z0-9_-]{1,40}")) && servings.toIntOrNull() in 0..10000,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Save stock to server") }
                }
            }
        }
        item { Text("Available menu", style = MaterialTheme.typography.titleLarge) }
        if (foods.isEmpty()) item { Text("No food is currently available. Enter a food ID above to restock it.") }
        items(foods, key = { it.id }) { food ->
            OutlinedCard(
                onClick = { foodId = food.id; servings = "" },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = cream)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${food.emoji} ${food.name}", style = MaterialTheme.typography.titleMedium)
                    Text("${food.category} • ${php(food.priceCentavos)}")
                    Text("ID: ${food.id}")
                    Text("Select to update servings")
                }
            }
        }
    }
}
