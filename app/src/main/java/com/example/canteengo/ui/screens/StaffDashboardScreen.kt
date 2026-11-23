package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.Order
import com.example.canteengo.data.OrderStatus
import com.example.canteengo.data.php

@Composable
fun StaffDashboardScreen(orders: List<Order>, onAdvance: (String) -> Unit) {
    var showHistory by rememberSaveable { mutableStateOf(false) }
    val displayed = orders.filter { (it.status == OrderStatus.COMPLETED) == showHistory }
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Canteen dashboard", style = MaterialTheme.typography.headlineMedium)
                Text("${orders.count { it.status != OrderStatus.COMPLETED }} active orders • ${orders.count { it.status == OrderStatus.COMPLETED }} collected")
                Text("Update an order to keep students informed.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = !showHistory, onClick = { showHistory = false }, label = { Text("Active") })
                    FilterChip(selected = showHistory, onClick = { showHistory = true }, label = { Text("History") })
                }
            }
        }
        if (displayed.isEmpty()) item { Text(if (showHistory) "No collected orders yet." else "No active orders yet. Refresh after a student places an order.") }
        items(displayed, key = { it.number }) { order ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(order.number, style = MaterialTheme.typography.titleLarge)
                    Text("Pickup for ${order.customer}")
                    Text(order.status.label, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                    order.items.forEach { item -> Text("${item.quantity} × ${item.food.name}") }
                    Text("${php(order.totalCentavos)} • Cash on pickup")
                    order.status.next()?.let { next ->
                        Button(onClick = { onAdvance(order.number) }, modifier = Modifier.fillMaxWidth()) {
                            Text(when (next) {
                                OrderStatus.PREPARING -> "Start preparing"
                                OrderStatus.READY -> "Mark ready for pickup"
                                OrderStatus.COMPLETED -> "Confirm cash received & collected"
                                OrderStatus.PLACED -> "Order placed"
                            })
                        }
                    }
                }
            }
        }
    }
}
