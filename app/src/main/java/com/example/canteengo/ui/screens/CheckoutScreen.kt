package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.CartItem
import com.example.canteengo.data.php
import com.example.canteengo.ui.components.EmptyState

@Composable
fun CheckoutScreen(cart: List<CartItem>, total: Int, onPlace: (String) -> Unit, onBrowse: () -> Unit, isDemo: Boolean = true) {
    var name by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Almost lunchtime", style = MaterialTheme.typography.headlineMedium)
        if (cart.isEmpty()) {
            EmptyState("Nothing to checkout", "Add food before placing an order.", "Browse menu", onBrowse)
        } else {
            OutlinedTextField(value = name, onValueChange = { name = it.take(80) }, label = { Text("Name for pickup") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Order summary", style = MaterialTheme.typography.titleLarge)
                    cart.forEach { item -> Text("${item.quantity} × ${item.food.name} — ${php(item.totalCentavos)}") }
                    HorizontalDivider()
                    Text("Total ${php(total)}", style = MaterialTheme.typography.headlineSmall)
                }
            }
            Text("Cash on pickup", style = MaterialTheme.typography.titleLarge)
            Text("Pay at the canteen counter when your order is ready. Show your order number at pickup.")
            Text("Pickup location: canteen counter")
            Button(onClick = { onPlace(name) }, enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text("${if (isDemo) "Place demo order" else "Place order"} • ${php(total)}") }
        }
    }
}
