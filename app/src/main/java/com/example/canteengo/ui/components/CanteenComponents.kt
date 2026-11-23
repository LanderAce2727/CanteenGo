package com.example.canteengo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canteengo.data.FoodItem
import com.example.canteengo.data.php

@Composable
fun FoodCard(food: FoodItem, onAdd: () -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(food.emoji, fontSize = 44.sp)
            Text(food.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(food.name, style = MaterialTheme.typography.titleLarge)
            Text(food.description, style = MaterialTheme.typography.bodyMedium)
            Text(php(food.priceCentavos), style = MaterialTheme.typography.titleMedium)
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Add ${food.name} to cart" }) { Text("Add to cart") }
        }
    }
}

@Composable
fun QuantitySelector(name: String, quantity: Int, onChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = { onChange(-1) }, modifier = Modifier.semantics { contentDescription = "Decrease $name quantity" }) { Text("−") }
        Text("$quantity", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.titleMedium)
        OutlinedButton(onClick = { onChange(1) }, enabled = quantity < 99, modifier = Modifier.semantics { contentDescription = "Increase $name quantity" }) { Text("+") }
    }
}

@Composable
fun EmptyState(title: String, message: String, action: String, onAction: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(message)
        Button(onClick = onAction) { Text(action) }
    }
}
