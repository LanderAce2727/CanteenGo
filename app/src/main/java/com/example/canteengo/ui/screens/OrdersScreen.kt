package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.Order
import com.example.canteengo.data.OrderStatus
import com.example.canteengo.data.php
import com.example.canteengo.ui.components.EmptyState

@Composable
fun OrdersScreen(orders: List<Order>, selectedNumber: String?, onSelect: (String) -> Unit, onBrowse: () -> Unit) {
    val selected = orders.find { it.number == selectedNumber }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Your orders", style = MaterialTheme.typography.headlineMedium) }
        if (orders.isEmpty()) item { EmptyState("Good food is one order away", "Your orders and pickup status will appear here.", "Explore menu", onBrowse) }
        if (selected != null) item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(selected.number, style = MaterialTheme.typography.headlineSmall)
                    Text("Pickup for ${selected.customer}")
                    OrderStatus.entries.forEach { status ->
                        Text("${if (status.ordinal <= selected.status.ordinal) "●" else "○"} ${status.label}", style = if (status == selected.status) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium)
                    }
                    selected.items.forEach { item -> Text("${item.quantity} × ${item.food.name} — ${php(item.totalCentavos)}") }
                    Text("${php(selected.totalCentavos)} • Cash on pickup")
                    Text(if (selected.status == OrderStatus.READY) "Your food is ready! Show this number at the canteen counter." else if (selected.status == OrderStatus.COMPLETED) "Collected. Thank you for ordering!" else "We’ll show updates here as the staff prepares your order.")
                }
            }
        }
        if (orders.isNotEmpty()) item { Text("Order history", style = MaterialTheme.typography.titleLarge) }
        items(orders, key = { it.number }) { order ->
            OutlinedCard(onClick = { onSelect(order.number) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(order.number, style = MaterialTheme.typography.titleMedium)
                    Text("${order.status.label} • ${php(order.totalCentavos)}")
                    Text("${order.items.sumOf { it.quantity }} items • View tracking")
                }
            }
        }
    }
}
