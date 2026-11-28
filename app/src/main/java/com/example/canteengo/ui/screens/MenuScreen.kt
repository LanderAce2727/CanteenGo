
package com.example.canteengo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.canteengo.data.DemoMenu
import com.example.canteengo.data.FoodItem
import com.example.canteengo.ui.components.FoodCard
import com.example.canteengo.ui.theme.CanteenGoTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

// SAME COLOR PALETTE AS HOMESCREEN
private val MenuPeachBackground = Color(0xFFFFF0DC)
private val MenuOrange = Color(0xFFE87524)
private val MenuDarkOrange = Color(0xFFB95319)
private val MenuBrownText = Color(0xFF60391F)
private val MenuMutedBrown = Color(0xFF8A674F)
private val MenuWhiteCream = Color(0xFFFFFAF4)
private val MenuLightPeach = Color(0xFFFFE2C2)

@Composable
fun MenuScreen(
    category: String,
    onCategory: (String) -> Unit,
    onAdd: (String) -> Unit,
    menu: List<FoodItem> = DemoMenu.foods,
    onHomeClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }

    // CATEGORY ORDER
    val categories = listOf(
        "All",
        "Meals",
        "Drinks"
    )



    val foods = menu.filter { food ->
        food.imageRes != null &&
                (category == "All" || food.category == category) &&
                food.name.contains(query, ignoreCase = true)
    }

    Scaffold(
        containerColor = MenuPeachBackground,

        // FIXED BOTTOM NAVIGATION - SAME AS HOMESCREEN
        bottomBar = {
            NavigationBar(
                containerColor = MenuWhiteCream,
                tonalElevation = 4.dp
            ) {

                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = {
                        Icon(
                            Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MenuOrange,
                        selectedTextColor = MenuOrange,
                        indicatorColor = MenuLightPeach,
                        unselectedIconColor = MenuMutedBrown,
                        unselectedTextColor = MenuMutedBrown
                    )
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Outlined.Restaurant,
                            contentDescription = "Menu"
                        )
                    },
                    label = { Text("Menu") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MenuOrange,
                        selectedTextColor = MenuOrange,
                        indicatorColor = MenuLightPeach,
                        unselectedIconColor = MenuMutedBrown,
                        unselectedTextColor = MenuMutedBrown
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onCartClick,
                    icon = {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart"
                        )
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MenuOrange,
                        selectedTextColor = MenuOrange,
                        indicatorColor = MenuLightPeach,
                        unselectedIconColor = MenuMutedBrown,
                        unselectedTextColor = MenuMutedBrown
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onOrdersClick,
                    icon = {
                        Icon(
                            Icons.Outlined.ReceiptLong,
                            contentDescription = "Orders"
                        )
                    },
                    label = { Text("Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MenuOrange,
                        selectedTextColor = MenuOrange,
                        indicatorColor = MenuLightPeach,
                        unselectedIconColor = MenuMutedBrown,
                        unselectedTextColor = MenuMutedBrown
                    )
                )
            }
        }
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 22.dp,
                end = 22.dp,
                top = 25.dp,
                bottom = 24.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // HEADER
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {

                    Text(
                        text = "Our Menu",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MenuBrownText
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Find something delicious for your break.",
                        fontSize = 13.sp,
                        color = MenuMutedBrown
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // SEARCH BAR
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Search food...",
                                color = MenuMutedBrown
                            )
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MenuOrange,
                            unfocusedBorderColor = MenuLightPeach,
                            focusedLabelColor = MenuOrange,
                            cursorColor = MenuOrange,
                            focusedContainerColor = MenuWhiteCream,
                            unfocusedContainerColor = MenuWhiteCream
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // CATEGORIES - ORDER FOLLOWING HOMESCREEN
                    Text(
                        text = "Categories",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MenuBrownText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { item ->
                            FilterChip(
                                selected = category == item,
                                onClick = { onCategory(item) },
                                label = { Text(item) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MenuOrange,
                                    selectedLabelColor = Color.White,
                                    containerColor = MenuWhiteCream,
                                    labelColor = MenuMutedBrown
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = category == item,
                                    borderColor = MenuLightPeach,
                                    selectedBorderColor = MenuOrange
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

            // EMPTY RESULTS
            if (foods.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 35.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No food found 😔",
                            color = MenuBrownText,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            "Try searching another food.",
                            color = MenuMutedBrown,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // FOOD CARDS - WITH PICTURES
            items(
                items = foods,
                key = { it.id }
            ) { food ->

                FoodCard(food) {
                    onAdd(food.id)
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun MenuScreenPreview() {
    CanteenGoTheme {
        MenuScreen(
            category = "All",
            onCategory = {},
            onAdd = {}
        )
    }
}
