
package com.example.canteengo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.Order
import com.example.canteengo.data.OrderStatus
import com.example.canteengo.data.CartItem
import com.example.canteengo.data.DemoMenu
import com.example.canteengo.data.php
import com.example.canteengo.ui.components.EmptyState
import com.example.canteengo.ui.theme.CanteenGoTheme

private val PeachBackground = Color(0xFFFFF0DC)
private val Orange = Color(0xFFE87524)
private val BrownText = Color(0xFF60391F)
private val MutedBrown = Color(0xFF8A674F)
private val WhiteCream = Color(0xFFFFFAF4)
private val LightPeach = Color(0xFFFFE2C2)

@Composable
fun OrdersScreen(
    orders: List<Order>,
    selectedNumber: String? = null,
    onSelect: (String) -> Unit = {},
    onBrowse: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    val selectedOrder = orders.find { it.number == selectedNumber }
        ?: orders.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeachBackground)
    ) {

        // SCROLLABLE CONTENT
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Spacer(modifier = Modifier.height(25.dp))

            // HEADER
            Text(
                text = "My Orders",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BrownText
            )

            Text(
                text = "Track your canteen orders here.",
                color = MutedBrown,
                style = MaterialTheme.typography.bodyMedium
            )

            if (orders.isEmpty()) {

                EmptyState(
                    "No orders yet",
                    "Your placed orders will appear here.",
                    "Browse menu",
                    onBrowse
                )

            } else {

                // ORDER LIST
                Text(
                    text = "Order History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrownText
                )

                orders.forEach { order ->

                    val isSelected = selectedOrder?.number == order.number

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(order.number)
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = WhiteCream
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.number}",
                                    fontWeight = FontWeight.Bold,
                                    color = BrownText
                                )

                                OrderStatusLabel(order.status)
                            }

                            Text(
                                text = order.customer,
                                color = MutedBrown,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "${order.items.sumOf { it.quantity }} item(s)",
                                color = MutedBrown,
                                style = MaterialTheme.typography.bodySmall
                            )

                            HorizontalDivider(color = LightPeach)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Total",
                                    color = BrownText,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    php(order.totalCentavos),
                                    color = Orange,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (isSelected) {
                                Text(
                                    "Viewing order details",
                                    color = Orange,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }

                // SELECTED ORDER DETAILS
                if (selectedOrder != null) {

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Order Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BrownText
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = WhiteCream
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {

                            Text(
                                "Order #${selectedOrder.number}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrownText
                            )

                            Text(
                                "Pickup name: ${selectedOrder.customer}",
                                color = MutedBrown
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Order Status",
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrownText
                                )

                                OrderStatusLabel(selectedOrder.status)
                            }

                            HorizontalDivider(color = LightPeach)

                            Text(
                                "Items Ordered",
                                fontWeight = FontWeight.Bold,
                                color = BrownText
                            )

                            selectedOrder.items.forEach { item ->

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {

                                    if (item.food.imageRes != null) {
                                        Image(
                                            painter = painterResource(
                                                item.food.imageRes
                                            ),
                                            contentDescription = item.food.name,
                                            modifier = Modifier
                                                .size(55.dp)
                                                .clip(
                                                    RoundedCornerShape(12.dp)
                                                ),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(55.dp)
                                                .clip(
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .background(LightPeach),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                item.food.emoji,
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            item.food.name,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BrownText
                                        )

                                        Text(
                                            "Quantity: ${item.quantity}",
                                            color = MutedBrown,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Text(
                                        php(item.totalCentavos),
                                        color = BrownText,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            HorizontalDivider(color = LightPeach)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total Amount",
                                    fontWeight = FontWeight.Bold,
                                    color = BrownText
                                )

                                Text(
                                    php(selectedOrder.totalCentavos),
                                    fontWeight = FontWeight.Bold,
                                    color = Orange,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // BOTTOM NAVIGATION - SAME AS HOMESCREEN
        NavigationBar(
            containerColor = WhiteCream,
            tonalElevation = 4.dp
        ) {

            NavigationBarItem(
                selected = false,
                onClick = onHomeClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Home"
                    )
                },
                label = { Text("Home") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Orange,
                    selectedTextColor = Orange,
                    indicatorColor = LightPeach,
                    unselectedIconColor = MutedBrown,
                    unselectedTextColor = MutedBrown
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = onMenuClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Restaurant,
                        contentDescription = "Menu"
                    )
                },
                label = { Text("Menu") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Orange,
                    selectedTextColor = Orange,
                    indicatorColor = LightPeach,
                    unselectedIconColor = MutedBrown,
                    unselectedTextColor = MutedBrown
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = onCartClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                },
                label = { Text("Cart") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Orange,
                    selectedTextColor = Orange,
                    indicatorColor = LightPeach,
                    unselectedIconColor = MutedBrown,
                    unselectedTextColor = MutedBrown
                )
            )

            NavigationBarItem(
                selected = true,
                onClick = onOrdersClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = "Orders"
                    )
                },
                label = { Text("Orders") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Orange,
                    selectedTextColor = Orange,
                    indicatorColor = LightPeach,
                    unselectedIconColor = MutedBrown,
                    unselectedTextColor = MutedBrown
                )
            )
        }
    }
}

// ORDER STATUS LABEL
@Composable
private fun OrderStatusLabel(status: OrderStatus) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = LightPeach
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            color = BrownText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// PREVIEW
@Preview(
    name = "Orders screen",
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun OrdersScreenPreview() {
    CanteenGoTheme {
        Surface {
            OrdersScreen(
                orders = listOf(
                    Order(
                        number = "1001",
                        customer = "Alex",
                        items = listOf(
                            CartItem(DemoMenu.foods[8], 2),
                            CartItem(DemoMenu.foods[10], 1)
                        ),
                        status = OrderStatus.PREPARING
                    ),
                    Order(
                        number = "1002",
                        customer = "Alex",
                        items = listOf(
                            CartItem(DemoMenu.foods[9], 1)
                        ),
                        status = OrderStatus.READY
                    )
                ),
                selectedNumber = "1001",
                onSelect = {},
                onBrowse = {}
            )
        }
    }
}
