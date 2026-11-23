package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.canteengo.ui.theme.CanteenGoTheme
import com.example.canteengo.data.DemoMenu
import com.example.canteengo.data.FoodItem
import com.example.canteengo.ui.components.FoodCard

@Composable
fun HomeScreen(onCategory: (String) -> Unit, onAdd: (String) -> Unit, foods: List<FoodItem> = DemoMenu.foods) {
    LazyVerticalGrid(columns = GridCells.Adaptive(240.dp), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Hello, hungry student!", style = MaterialTheme.typography.headlineMedium)
                Text("What are you craving today?")
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Your next campus favorite", style = MaterialTheme.typography.titleLarge)
                        Text("Fresh meals. Student-friendly prices. Cash on pickup.")
                        Button(onClick = { onCategory("All") }) { Text("Explore the menu") }
                    }
                }
                Text("Browse categories", style = MaterialTheme.typography.titleLarge)
            }
        }
        items(DemoMenu.categories.drop(1)) { category ->
            Button(onClick = { onCategory(category) }, modifier = Modifier.fillMaxWidth()) { Text(category) }
        }
        item(span = { GridItemSpan(maxLineSpan) }) { Text("Campus favorites", style = MaterialTheme.typography.titleLarge) }
        items(foods.take(3), key = { it.id }) { food -> FoodCard(food) { onAdd(food.id) } }
    }
}

@Preview(name = "Student home", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun HomeScreenPreview() {
    CanteenGoTheme {
        Surface {
            HomeScreen(onCategory = {}, onAdd = {})
        }
    }
}
