package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.CartItem
import com.example.canteengo.data.php
import com.example.canteengo.ui.components.EmptyState
import com.example.canteengo.ui.components.QuantitySelector

@Composable
fun CartScreen(cart: List<CartItem>, total: Int, onQuantity: (String, Int) -> Unit, onRemove: (String) -> Unit, onBrowse: () -> Unit, onCheckout: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Your tasty picks", style = MaterialTheme.typography.headlineMedium) }
        if (cart.isEmpty()) item { EmptyState("Your cart is empty", "Find something delicious from the canteen.", "Browse menu", onBrowse) }
        items(cart, key = { it.food.id }) { item ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${item.food.emoji} ${item.food.name}", style = MaterialTheme.typography.titleLarge)
                    Text("${php(item.food.priceCentavos)} each • ${php(item.totalCentavos)}")
                    QuantitySelector(item.food.name, item.quantity) { onQuantity(item.food.id, it) }
                    TextButton(onClick = { onRemove(item.food.id) }) { Text("Remove ${item.food.name}") }
                }
            }
        }
        if (cart.isNotEmpty()) item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Total ${php(total)}", style = MaterialTheme.typography.headlineSmall)
                Text("No delivery fee. Collect at the DNSC canteen.")
                Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) { Text("Continue to checkout") }
            }
        }
    }
}
