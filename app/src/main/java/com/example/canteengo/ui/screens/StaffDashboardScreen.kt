
package com.example.canteengo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canteengo.data.CartItem
import com.example.canteengo.data.DemoMenu
import com.example.canteengo.data.Order
import com.example.canteengo.data.OrderStatus
import com.example.canteengo.data.php
import com.example.canteengo.ui.theme.CanteenGoTheme

private val StaffPeach = Color(0xFFFFF0DC)
private val StaffOrange = Color(0xFFE87524)
private val StaffDarkOrange = Color(0xFFB95319)
private val StaffBrown = Color(0xFF60391F)
private val StaffMutedBrown = Color(0xFF8A674F)
private val StaffCream = Color(0xFFFFFAF4)
private val StaffLightPeach = Color(0xFFFFE2C2)

@Composable
fun StaffDashboardScreen(
    orders: List<Order>,
    onAdvance: (String) -> Unit
) {
    var showHistory by rememberSaveable {
        mutableStateOf(false)
    }

    val activeOrders = orders.filter {
        it.status != OrderStatus.COMPLETED
    }

    val completedOrders = orders.filter {
        it.status == OrderStatus.COMPLETED
    }

    val displayed = orders.filter {
        (it.status == OrderStatus.COMPLETED) == showHistory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(StaffPeach),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // HEADER
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Staff Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = StaffBrown
                )

                Text(
                    text = "Manage student orders and pickup status.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StaffMutedBrown
                )
            }
        }

        // OVERVIEW
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StaffBrown
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StaffSummaryCard(
                        title = "Active",
                        count = activeOrders.size.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    StaffSummaryCard(
                        title = "Preparing",
                        count = orders.count {
                            it.status == OrderStatus.PREPARING
                        }.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    StaffSummaryCard(
                        title = "Ready",
                        count = orders.count {
                            it.status == OrderStatus.READY
                        }.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StaffOrange
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Collected Orders",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                completedOrders.size.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            "✓",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }
            }
        }

        // ORDER SECTION
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    if (showHistory) "Order History" else "Incoming Orders",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StaffBrown
                )

                Text(
                    if (showHistory)
                        "Previously completed orders"
                    else
                        "Review and update current orders",
                    color = StaffMutedBrown,
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !showHistory,
                        onClick = {
                            showHistory = false
                        },
                        label = {
                            Text("Active Orders")
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StaffLightPeach,
                            selectedLabelColor = StaffDarkOrange
                        )
                    )

                    FilterChip(
                        selected = showHistory,
                        onClick = {
                            showHistory = true
                        },
                        label = {
                            Text("History")
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StaffLightPeach,
                            selectedLabelColor = StaffDarkOrange
                        )
                    )
                }
            }
        }

        // EMPTY STATE
        if (displayed.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StaffCream
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            if (showHistory) "📋" else "🍽️",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Text(
                            if (showHistory)
                                "No completed orders yet"
                            else
                                "No active orders yet",
                            fontWeight = FontWeight.Bold,
                            color = StaffBrown
                        )

                        Text(
                            if (showHistory)
                                "Completed orders will appear here."
                            else
                                "Orders from students will appear here.",
                            color = StaffMutedBrown,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // ORDER CARDS
        items(
            displayed,
            key = { order -> order.number }
        ) { order ->

            StaffOrderCard(
                order = order,
                onAdvance = onAdvance
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// OVERVIEW CARD
@Composable
private fun StaffSummaryCard(
    title: String,
    count: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = StaffCream
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = StaffOrange
            )

            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = StaffMutedBrown
            )
        }
    }
}

// ORDER CARD
@Composable
private fun StaffOrderCard(
    order: Order,
    onAdvance: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = StaffCream
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ORDER HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "ORDER #${order.number}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StaffBrown
                    )

                    Text(
                        "Pickup for ${order.customer}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = StaffMutedBrown
                    )
                }

                StaffStatusBadge(order.status)
            }

            HorizontalDivider(
                color = StaffLightPeach
            )

            // ORDER ITEMS
            Text(
                "Order Items",
                fontWeight = FontWeight.SemiBold,
                color = StaffBrown
            )

            order.items.forEach { item ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "${item.quantity} × ${item.food.name}",
                            color = StaffBrown,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        php(item.totalCentavos),
                        color = StaffMutedBrown,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(
                color = StaffLightPeach
            )

            // TOTAL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Order Total",
                    fontWeight = FontWeight.Bold,
                    color = StaffBrown
                )

                Text(
                    php(order.totalCentavos),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StaffOrange
                )
            }

            // ACTION BUTTON
            val nextStatus = order.status.next()

            if (nextStatus != null) {
                Button(
                    onClick = {
                        onAdvance(order.number)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StaffOrange
                    )
                ) {
                    Text(
                        text = when (nextStatus) {
                            OrderStatus.PREPARING ->
                                "Start Preparing"

                            OrderStatus.READY ->
                                "Mark Ready for Pickup"

                            OrderStatus.COMPLETED ->
                                "Confirm Collected"

                            OrderStatus.PLACED ->
                                "Order Placed"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    "✓ Order completed",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            StaffLightPeach,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    color = StaffDarkOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// STATUS BADGE
@Composable
private fun StaffStatusBadge(
    status: OrderStatus
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = StaffLightPeach
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            color = StaffDarkOrange,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// PREVIEW
@Preview(
    name = "Staff Dashboard",
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun StaffDashboardScreenPreview() {
    val sampleOrders = listOf(
        Order(
            "1001",
            "Maria",
            listOf(
                CartItem(DemoMenu.foods[0], 2),
                CartItem(DemoMenu.foods[6], 1)
            ),
            OrderStatus.PLACED
        ),
        Order(
            "1002",
            "Juan",
            listOf(
                CartItem(DemoMenu.foods[1], 1)
            ),
            OrderStatus.PREPARING
        ),
        Order(
            "1003",
            "Alex",
            listOf(
                CartItem(DemoMenu.foods[8], 2)
            ),
            OrderStatus.READY
        ),
        Order(
            "1004",
            "Sam",
            listOf(
                CartItem(DemoMenu.foods[10], 1)
            ),
            OrderStatus.COMPLETED
        )
    )

    CanteenGoTheme {
        Surface {
            StaffDashboardScreen(
                orders = sampleOrders,
                onAdvance = {}
            )
        }
    }
}
