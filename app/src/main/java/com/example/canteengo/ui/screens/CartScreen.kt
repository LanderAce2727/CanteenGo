
package com.example.canteengo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.canteengo.data.CartItem
import com.example.canteengo.data.DemoMenu
import com.example.canteengo.data.php
import com.example.canteengo.ui.components.EmptyState
import com.example.canteengo.ui.components.QuantitySelector
import com.example.canteengo.ui.theme.CanteenGoTheme

// SAME COLOR PALETTE AS HOME AND MENU
private val CartPeachBackground = Color(0xFFFFF0DC)
private val CartOrange = Color(0xFFE87524)
private val CartBrownText = Color(0xFF60391F)
private val CartMutedBrown = Color(0xFF8A674F)
private val CartWhiteCream = Color(0xFFFFFAF4)
private val CartLightPeach = Color(0xFFFFE2C2)

@Composable
fun CartScreen(
    cart: List<CartItem>,
    total: Int,
    onQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit,
    onBrowse: () -> Unit,
    onCheckout: () -> Unit,
    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = CartPeachBackground,

        // FIXED BOTTOM NAVIGATION
        bottomBar = {
            NavigationBar(
                containerColor = CartWhiteCream,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = {
                        Icon(Icons.Outlined.Home, contentDescription = "Home")
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CartOrange,
                        selectedTextColor = CartOrange,
                        indicatorColor = CartLightPeach,
                        unselectedIconColor = CartMutedBrown,
                        unselectedTextColor = CartMutedBrown
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onMenuClick,
                    icon = {
                        Icon(Icons.Outlined.Restaurant, contentDescription = "Menu")
                    },
                    label = { Text("Menu") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CartOrange,
                        selectedTextColor = CartOrange,
                        indicatorColor = CartLightPeach,
                        unselectedIconColor = CartMutedBrown,
                        unselectedTextColor = CartMutedBrown
                    )
                )

                NavigationBarItem(
                    selected = true,
                    onClick = onCartClick,
                    icon = {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = "Cart")
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CartOrange,
                        selectedTextColor = CartOrange,
                        indicatorColor = CartLightPeach,
                        unselectedIconColor = CartMutedBrown,
                        unselectedTextColor = CartMutedBrown
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onOrdersClick,
                    icon = {
                        Icon(Icons.Outlined.ReceiptLong, contentDescription = "Orders")
                    },
                    label = { Text("Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CartOrange,
                        selectedTextColor = CartOrange,
                        indicatorColor = CartLightPeach,
                        unselectedIconColor = CartMutedBrown,
                        unselectedTextColor = CartMutedBrown
                    )
                )
            }
        }
    ) { innerPadding ->

        // SCROLLABLE CART CONTENT ONLY
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 25.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HEADER
            item {
                Column {
                    Text(
                        text = "My Cart",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = CartBrownText
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Your tasty picks are waiting for you.",
                        fontSize = 13.sp,
                        color = CartMutedBrown
                    )
                }
            }

            // EMPTY CART
            if (cart.isEmpty()) {
                item {
                    EmptyState(
                        title = "Your cart is empty",
                        message = "Find something delicious from the canteen.",
                        action = "Browse menu",
                        onAction = onBrowse
                    )
                }
            }

            // CART FOOD ITEMS
            items(cart, key = { it.food.id }) { item ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CartWhiteCream
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // ACTUAL FOOD IMAGE WHEN AVAILABLE
                            if (item.food.imageRes != null) {
                                Image(
                                    painter = painterResource(
                                        id = item.food.imageRes
                                    ),
                                    contentDescription = item.food.name,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.size(90.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = CartLightPeach
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            item.food.emoji,
                                            fontSize = 38.sp
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    item.food.category,
                                    color = CartOrange,
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    item.food.name,
                                    color = CartBrownText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "${php(item.food.priceCentavos)} each",
                                    color = CartMutedBrown,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        HorizontalDivider(color = CartLightPeach)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Subtotal",
                                    color = CartMutedBrown,
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    php(item.totalCentavos),
                                    color = CartOrange,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            QuantitySelector(
                                name = item.food.name,
                                quantity = item.quantity
                            ) { change ->
                                onQuantity(item.food.id, change)
                            }
                        }

                        TextButton(
                            onClick = {
                                onRemove(item.food.id)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                "Remove item",
                                color = CartMutedBrown
                            )
                        }
                    }
                }
            }

            // ORDER SUMMARY
            if (cart.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CartWhiteCream
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Order Summary",
                                color = CartBrownText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Items (${cart.sumOf { it.quantity }})",
                                    color = CartMutedBrown
                                )

                                Text(
                                    php(total),
                                    color = CartBrownText
                                )
                            }

                            HorizontalDivider(color = CartLightPeach)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Total",
                                    color = CartBrownText,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    php(total),
                                    color = CartOrange,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }

                            Text(
                                "No delivery fee. Collect at the DNSC canteen.",
                                color = CartMutedBrown,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Button(
                                onClick = onCheckout,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CartOrange
                                )
                            ) {
                                Text(
                                    "Continue to checkout",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Cart screen",
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun CartScreenPreview() {
    CanteenGoTheme {
        CartScreen(
            cart = listOf(
                CartItem(DemoMenu.foods[8], 2),
                CartItem(DemoMenu.foods[10], 1)
            ),
            total = 13500,
            onQuantity = { _, _ -> },
            onRemove = {},
            onBrowse = {},
            onCheckout = {}
        )
    }
}
