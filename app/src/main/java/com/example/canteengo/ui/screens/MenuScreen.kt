package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.DemoMenu
import com.example.canteengo.data.FoodItem
import com.example.canteengo.ui.components.FoodCard

@Composable
fun MenuScreen(category: String, onCategory: (String) -> Unit, onAdd: (String) -> Unit, menu: List<FoodItem> = DemoMenu.foods) {
    var query by rememberSaveable { mutableStateOf("") }
    val foods = menu.filter { (category == "All" || it.category == category) && it.name.contains(query, ignoreCase = true) }
    LazyVerticalGrid(columns = GridCells.Adaptive(240.dp), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Made for your break", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(query, { query = it }, label = { Text("Search food") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(DemoMenu.categories) { item -> FilterChip(selected = category == item, onClick = { onCategory(item) }, label = { Text(item) }) }
                }
                Text("${foods.size} delicious options", style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (foods.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) { Text("No matches. Try another food or category.") }
        items(foods, key = { it.id }) { food -> FoodCard(food) { onAdd(food.id) } }
    }
}
