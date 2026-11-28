
package com.example.canteengo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canteengo.R
import com.example.canteengo.data.FoodItem
import com.example.canteengo.data.php
import com.example.canteengo.ui.theme.CanteenGoTheme

private val MenuPeach = Color(0xFFFFF0DC)
private val MenuLightPeach = Color(0xFFFFE2C2)
private val MenuOrange = Color(0xFFE87524)
private val MenuDarkOrange = Color(0xFFB95319)
private val MenuBrown = Color(0xFF60391F)
private val MenuMutedBrown = Color(0xFF8A674F)
private val MenuCream = Color(0xFFFFFAF4)

@Composable
fun MenuManageScreen(
    onNavigate: (String) -> Unit = {}
) {

    // SAMPLE FOOD ITEMS
    val foods = remember {
        mutableStateListOf(
            FoodItem(
                id = "chicken_meal",
                name = "Chicken Meal",
                description = "A delicious chicken meal served with rice.",
                category = "Meals",
                priceCentavos = 8500,
                emoji = "🍗",
                imageRes = R.drawable.chicken_meal
            ),
            FoodItem(
                id = "special_pancit",
                name = "Special Pancit",
                description = "Filipino pancit noodles.",
                category = "Meals",
                priceCentavos = 6000,
                emoji = "🍜",
                imageRes = R.drawable.special_pancit
            ),
            FoodItem(
                id = "iced_coffee",
                name = "Iced Coffee",
                description = "A refreshing iced coffee drink.",
                category = "Drinks",
                priceCentavos = 4500,
                emoji = "🥤",
                imageRes = R.drawable.iced_coffee
            ),
            FoodItem(
                id = "siomai_rice",
                name = "Siomai Rice",
                description = "Steamed siomai served with rice.",
                category = "Meals",
                priceCentavos = 4500,
                emoji = "🥟",
                imageRes = R.drawable.siomai
            )
        )
    }

    val stockAmounts = remember {
        mutableStateMapOf<String, Int>().apply {
            foods.forEach {
                this[it.id] = 20
            }
        }
    }

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    var selectedCategory by rememberSaveable {
        mutableStateOf("All")
    }

    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var editingFoodId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var deleteFoodId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var foodName by rememberSaveable {
        mutableStateOf("")
    }

    var foodDescription by rememberSaveable {
        mutableStateOf("")
    }

    var foodCategory by rememberSaveable {
        mutableStateOf("Meals")
    }

    var foodPrice by rememberSaveable {
        mutableStateOf("")
    }

    var foodEmoji by rememberSaveable {
        mutableStateOf("🍽️")
    }

    val categories = listOf(
        "All",
        "Meals",
        "Drinks",
        "Snacks"
    )

    val filteredFoods = foods.filter { food ->
        val matchesSearch =
            food.name.contains(searchText, ignoreCase = true) ||
                    food.description.contains(searchText, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All" ||
                    food.category.equals(selectedCategory, ignoreCase = true)

        matchesSearch && matchesCategory
    }

    fun openAddDialog() {
        editingFoodId = null
        foodName = ""
        foodDescription = ""
        foodCategory = "Meals"
        foodPrice = ""
        foodEmoji = "🍽️"
        showDialog = true
    }

    fun openEditDialog(food: FoodItem) {
        editingFoodId = food.id
        foodName = food.name
        foodDescription = food.description
        foodCategory = food.category
        foodPrice = "%.2f".format(
            java.util.Locale.US,
            food.priceCentavos / 100.0
        )
        foodEmoji = food.emoji
        showDialog = true
    }

    // MAIN SCREEN WITH FIXED BOTTOM NAVIGATION
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MenuPeach)
    ) {

        // SCROLLABLE MENU CONTENT
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HEADER
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    "CanteenGo Management",
                    color = MenuMutedBrown,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    "Manage Menu",
                    color = MenuBrown,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Add, edit, and manage your food items.",
                    color = MenuMutedBrown
                )
            }

            // SUMMARY
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuSummaryCard(
                    title = "Total Foods",
                    value = foods.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                MenuSummaryCard(
                    title = "Available",
                    value = foods.count {
                        (stockAmounts[it.id] ?: 0) > 0
                    }.toString(),
                    modifier = Modifier.weight(1f)
                )

                MenuSummaryCard(
                    title = "Out of Stock",
                    value = foods.count {
                        (stockAmounts[it.id] ?: 0) <= 0
                    }.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // ADD FOOD BUTTON
            Button(
                onClick = { openAddDialog() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MenuOrange
                )
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text("Add New Food")
            }

            // SEARCH
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search food...")
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MenuOrange,
                    unfocusedBorderColor = MenuLightPeach
                )
            )

            // CATEGORY FILTER
            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = category
                        },
                        label = {
                            Text(category)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MenuLightPeach,
                            selectedLabelColor = MenuDarkOrange
                        )
                    )
                }
            }

            // FOOD LIST HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Food Items",
                    color = MenuBrown,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "${filteredFoods.size} items",
                    color = MenuMutedBrown,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // FOOD CARDS
            if (filteredFoods.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MenuCream
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No food items found.",
                            color = MenuMutedBrown
                        )
                    }
                }
            } else {
                filteredFoods.forEach { food ->

                    val currentStock = stockAmounts[food.id] ?: 0

                    MenuFoodCard(
                        food = food,
                        stock = currentStock,
                        onEdit = {
                            openEditDialog(food)
                        },
                        onDelete = {
                            deleteFoodId = food.id
                        },
                        onStockChange = { newStock ->
                            stockAmounts[food.id] = newStock
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        // FIXED MENU BOTTOM NAVIGATION
        MenuManageBottomNavigation(
            onNavigate = onNavigate
        )
    }

    // ADD / EDIT DIALOG
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(
                    if (editingFoodId == null) "Add New Food" else "Edit Food",
                    color = MenuBrown,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = foodDescription,
                        onValueChange = { foodDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = foodCategory,
                        onValueChange = { foodCategory = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = foodPrice,
                        onValueChange = {
                            foodPrice = it.filter { char ->
                                char.isDigit() || char == '.'
                            }.take(8)
                        },
                        label = { Text("Price (₱)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = foodEmoji,
                        onValueChange = { foodEmoji = it },
                        label = { Text("Food Icon / Emoji") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Food picture selection can be added in a later step.",
                        color = MenuMutedBrown,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = foodPrice.toDoubleOrNull()

                        if (
                            foodName.isNotBlank() &&
                            price != null &&
                            price >= 0 &&
                            foodCategory.isNotBlank()
                        ) {
                            val oldFood = foods.find {
                                it.id == editingFoodId
                            }

                            val newFood = FoodItem(
                                id = editingFoodId
                                    ?: "custom_${System.currentTimeMillis()}",
                                name = foodName.trim(),
                                description = foodDescription.trim(),
                                category = foodCategory.trim(),
                                priceCentavos = (price * 100).toInt(),
                                emoji = foodEmoji.ifBlank { "🍽️" },
                                imageRes = oldFood?.imageRes
                            )

                            if (editingFoodId == null) {
                                foods.add(newFood)
                                stockAmounts[newFood.id] = 20
                            } else {
                                val index = foods.indexOfFirst {
                                    it.id == editingFoodId
                                }

                                if (index >= 0) {
                                    foods[index] = newFood
                                }
                            }

                            showDialog = false
                        }
                    },
                    enabled = foodName.isNotBlank() &&
                            foodPrice.toDoubleOrNull() != null &&
                            (foodPrice.toDoubleOrNull() ?: -1.0) >= 0 &&
                            foodCategory.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MenuOrange
                    )
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(
                        "Cancel",
                        color = MenuMutedBrown
                    )
                }
            },
            containerColor = MenuCream
        )
    }

    // DELETE CONFIRMATION
    val foodToDelete = foods.find {
        it.id == deleteFoodId
    }

    if (foodToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                deleteFoodId = null
            },
            title = {
                Text("Delete Food?")
            },
            text = {
                Text("Are you sure you want to delete ${foodToDelete.name}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        foods.removeAll {
                            it.id == deleteFoodId
                        }
                        stockAmounts.remove(deleteFoodId)
                        deleteFoodId = null
                    }
                ) {
                    Text(
                        "Delete",
                        color = MenuDarkOrange
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deleteFoodId = null
                    }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MenuCream
        )
    }
}

// BOTTOM NAVIGATION
@Composable
fun MenuManageBottomNavigation(
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MenuCream,
        tonalElevation = 3.dp
    ) {

        NavigationBarItem(
            selected = false,
            onClick = { onNavigate("Dashboard") },
            icon = {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Dashboard"
                )
            },
            label = { Text("Dashboard") },
            colors = menuNavigationColors()
        )

        NavigationBarItem(
            selected = true,
            onClick = { onNavigate("Menu") },
            icon = {
                Icon(
                    Icons.Outlined.Restaurant,
                    contentDescription = "Menu"
                )
            },
            label = { Text("Menu") },
            colors = menuNavigationColors()
        )

        NavigationBarItem(
            selected = false,
            onClick = { onNavigate("Orders") },
            icon = {
                Icon(
                    Icons.Outlined.ReceiptLong,
                    contentDescription = "Orders"
                )
            },
            label = { Text("Orders") },
            colors = menuNavigationColors()
        )

        NavigationBarItem(
            selected = false,
            onClick = { onNavigate("Staff") },
            icon = {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = "Staff"
                )
            },
            label = { Text("Staff") },
            colors = menuNavigationColors()
        )

        NavigationBarItem(
            selected = false,
            onClick = { onNavigate("Reports") },
            icon = {
                Icon(
                    Icons.Outlined.BarChart,
                    contentDescription = "Reports"
                )
            },
            label = { Text("Reports") },
            colors = menuNavigationColors()
        )
    }
}

@Composable
private fun menuNavigationColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = MenuDarkOrange,
        selectedTextColor = MenuDarkOrange,
        indicatorColor = MenuLightPeach,
        unselectedIconColor = MenuMutedBrown,
        unselectedTextColor = MenuMutedBrown
    )

// SUMMARY CARD
@Composable
private fun MenuSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MenuCream
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                title,
                color = MenuMutedBrown,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                value,
                color = MenuDarkOrange,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// FOOD CARD
@Composable
private fun MenuFoodCard(
    food: FoodItem,
    stock: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStockChange: (Int) -> Unit
) {
    val isAvailable = stock > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MenuCream
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MenuLightPeach),
                    contentAlignment = Alignment.Center
                ) {
                    if (food.imageRes != null) {
                        Image(
                            painter = painterResource(food.imageRes),
                            contentDescription = food.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            food.emoji,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        food.name,
                        color = MenuBrown,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        food.category,
                        color = MenuMutedBrown,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        food.description,
                        color = MenuMutedBrown,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )

                    Text(
                        php(food.priceCentavos),
                        color = MenuDarkOrange,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            HorizontalDivider(color = MenuLightPeach)

            // STOCK AND AVAILABILITY
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        "Available Servings",
                        color = MenuMutedBrown,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        if (isAvailable) "$stock servings" else "Out of Stock",
                        color = if (isAvailable) MenuDarkOrange else Color(0xFFB84235),
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (isAvailable) {
                        Color(0xFFE3F1DF)
                    } else {
                        Color(0xFFFFE0DC)
                    }
                ) {
                    Text(
                        if (isAvailable) "Available" else "Unavailable",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = if (isAvailable) {
                            Color(0xFF477849)
                        } else {
                            Color(0xFFB84235)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // STOCK CONTROLS
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (stock > 0) onStockChange(stock - 1)
                    },
                    enabled = stock > 0,
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Outlined.Remove,
                        contentDescription = "Decrease stock"
                    )
                }

                Text(
                    stock.toString(),
                    modifier = Modifier.padding(horizontal = 14.dp),
                    color = MenuBrown,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = {
                        if (stock < 10000) onStockChange(stock + 1)
                    },
                    enabled = stock < 10000,
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = "Increase stock"
                    )
                }

                Spacer(Modifier.weight(1f))

                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit food",
                        tint = MenuOrange
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete food",
                        tint = Color(0xFFB84235)
                    )
                }
            }
        }
    }
}

// PREVIEW
@Preview(
    name = "Admin Menu Management",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun MenuManagePreview() {
    CanteenGoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MenuPeach
        ) {
            MenuManageScreen()
        }
    }
}
