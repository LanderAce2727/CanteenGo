
package com.example.canteengo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

private val AdminPeach = Color(0xFFFFF0DC)
private val AdminLightPeach = Color(0xFFFFE2C2)
private val AdminOrange = Color(0xFFE87524)
private val AdminDarkOrange = Color(0xFFB95319)
private val AdminBrown = Color(0xFF60391F)
private val AdminMutedBrown = Color(0xFF8A674F)
private val AdminCream = Color(0xFFFFFAF4)

@Composable
fun AdminScreen(
    orders: List<Order>,
    report: String,
    onReport: (String) -> Unit,
    onStock: (String, Int) -> Unit,
    onStaff: () -> Unit,
    foods: List<com.example.canteengo.data.FoodItem> = DemoMenu.foods,
    isDemo: Boolean = true
) {
    var selectedTab by rememberSaveable {
        mutableStateOf("Dashboard")
    }

    val totalValue = orders.sumOf {
        it.totalCentavos
    }

    val completedOrders = orders.count {
        it.status == OrderStatus.COMPLETED
    }

    val activeOrders = orders.count {
        it.status != OrderStatus.COMPLETED
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminPeach)
    ) {

        // MENU SCREEN
        // MenuManageScreen handles its own scrolling and bottom navigation.

        if (selectedTab == "Menu") {

            if (isDemo) {
                MenuManageScreen(onNavigate = { selectedTab = it })
            } else {
                CloudInventoryScreen(
                    foods = foods,
                    onStock = onStock,
                    onBack = { selectedTab = "Dashboard" }
                )
            }

        } else {

            // MAIN CONTENT

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                when (selectedTab) {

                    // DASHBOARD

                    "Dashboard" -> {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                "CanteenGo Management",
                                color = AdminMutedBrown,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                "Admin Dashboard",
                                color = AdminBrown,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Welcome back, Admin!",
                                color = AdminMutedBrown
                            )
                        }

                        AdminSectionTitle("Overview")

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AdminStatCard(
                                    title = "Total Orders",
                                    value = orders.size.toString(),
                                    subtitle = "All recorded orders",
                                    modifier = Modifier.weight(1f)
                                )

                                AdminStatCard(
                                    title = "Order Value",
                                    value = php(totalValue),
                                    subtitle = "Includes unpaid orders",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AdminStatCard(
                                    title = "Active Orders",
                                    value = activeOrders.toString(),
                                    subtitle = "Still in progress",
                                    modifier = Modifier.weight(1f)
                                )

                                AdminStatCard(
                                    title = "Completed",
                                    value = completedOrders.toString(),
                                    subtitle = "Collected orders",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        AdminSectionTitle("Recent Orders")

                        if (orders.isEmpty()) {
                            AdminEmptyCard("No orders yet.")
                        } else {
                            orders.takeLast(3).reversed().forEach {
                                AdminOrderCard(it)
                            }
                        }
                    }

                    // ORDERS

                    "Orders" -> {

                        AdminPageHeader(
                            title = "All Orders",
                            subtitle = "Monitor customer orders."
                        )

                        AdminStatCard(
                            title = "Total Orders",
                            value = orders.size.toString(),
                            subtitle = "All recorded orders",
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (orders.isEmpty()) {
                            AdminEmptyCard("No orders yet.")
                        } else {
                            orders.reversed().forEach {
                                AdminOrderCard(it)
                            }
                        }

                        Button(
                            onClick = onStaff,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AdminOrange
                            )
                        ) {
                            Text("Open Order Management")
                        }
                    }

                    // STAFF

                    "Staff" -> {

                        AdminPageHeader(
                            title = "Manage Staff",
                            subtitle = "Staff accounts and management."
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AdminCream
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    "Staff Management",
                                    color = AdminBrown,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    "This section will allow the admin to manage " +
                                            "staff accounts and staff information.",
                                    color = AdminMutedBrown
                                )

                                Text(
                                    "Staff management UI will be added next.",
                                    color = AdminDarkOrange,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // REPORTS

                    "Reports" -> {

                        AdminPageHeader(
                            title = "Sales & Reports",
                            subtitle = "Generate masked order reports."
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AdminCream
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "Order Reports",
                                    color = AdminBrown,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    "Choose a format to generate a report.",
                                    color = AdminMutedBrown
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("json", "csv", "xml").forEach { format ->

                                        OutlinedButton(
                                            onClick = {
                                                onReport(format)
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = AdminDarkOrange
                                            )
                                        ) {
                                            Text(format.uppercase())
                                        }
                                    }
                                }

                                if (report.isNotBlank()) {

                                    HorizontalDivider(
                                        color = AdminLightPeach
                                    )

                                    SelectionContainer {
                                        Text(
                                            report,
                                            color = AdminBrown,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        AdminSectionTitle("Order Summary")

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminStatCard(
                                title = "Orders",
                                value = orders.size.toString(),
                                subtitle = "Total orders",
                                modifier = Modifier.weight(1f)
                            )

                            AdminStatCard(
                                title = "Order Value",
                                value = php(totalValue),
                                subtitle = "Order totals",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminStatCard(
                                title = "Completed",
                                value = completedOrders.toString(),
                                subtitle = "Collected",
                                modifier = Modifier.weight(1f)
                            )

                            AdminStatCard(
                                title = "Active",
                                value = activeOrders.toString(),
                                subtitle = "In progress",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // FIXED ADMIN BOTTOM NAVIGATION

            NavigationBar(
                containerColor = AdminCream,
                tonalElevation = 3.dp
            ) {

                NavigationBarItem(
                    selected = selectedTab == "Dashboard",
                    onClick = {
                        selectedTab = "Dashboard"
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.Home,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = {
                        Text("Dashboard")
                    },
                    colors = adminNavigationColors()
                )

                NavigationBarItem(
                    selected = selectedTab == "Menu",
                    onClick = {
                        selectedTab = "Menu"
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.Restaurant,
                            contentDescription = "Menu"
                        )
                    },
                    label = {
                        Text("Menu")
                    },
                    colors = adminNavigationColors()
                )

                NavigationBarItem(
                    selected = selectedTab == "Orders",
                    onClick = {
                        selectedTab = "Orders"
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.ReceiptLong,
                            contentDescription = "Orders"
                        )
                    },
                    label = {
                        Text("Orders")
                    },
                    colors = adminNavigationColors()
                )

                NavigationBarItem(
                    selected = selectedTab == "Staff",
                    onClick = {
                        selectedTab = "Staff"
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = "Staff"
                        )
                    },
                    label = {
                        Text("Staff")
                    },
                    colors = adminNavigationColors()
                )

                NavigationBarItem(
                    selected = selectedTab == "Reports",
                    onClick = {
                        selectedTab = "Reports"
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.BarChart,
                            contentDescription = "Reports"
                        )
                    },
                    label = {
                        Text("Reports")
                    },
                    colors = adminNavigationColors()
                )
            }
        }
    }
}

// NAVIGATION COLORS

@Composable
private fun adminNavigationColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = AdminDarkOrange,
        selectedTextColor = AdminDarkOrange,
        indicatorColor = AdminLightPeach,
        unselectedIconColor = AdminMutedBrown,
        unselectedTextColor = AdminMutedBrown
    )

// REUSABLE COMPONENTS

@Composable
private fun AdminPageHeader(
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            "CanteenGo Management",
            color = AdminMutedBrown,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            title,
            color = AdminBrown,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            subtitle,
            color = AdminMutedBrown
        )
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = AdminCream
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                title,
                color = AdminMutedBrown,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                value,
                color = AdminDarkOrange,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                subtitle,
                color = AdminMutedBrown,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun AdminSectionTitle(title: String) {
    Text(
        title,
        color = AdminBrown,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun AdminEmptyCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = AdminCream
        )
    ) {
        Text(
            message,
            modifier = Modifier.padding(18.dp),
            color = AdminMutedBrown
        )
    }
}

@Composable
private fun AdminOrderCard(order: Order) {

    val statusColor = when (order.status) {
        OrderStatus.PLACED -> Color(0xFFE89B36)
        OrderStatus.PREPARING -> AdminOrange
        OrderStatus.READY -> Color(0xFF548B58)
        OrderStatus.COMPLETED -> Color(0xFF548B58)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = AdminCream
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Order #${order.number}",
                        color = AdminBrown,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        order.customer,
                        color = AdminMutedBrown,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = statusColor.copy(alpha = 0.13f)
                ) {
                    Text(
                        order.status.label,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            order.items.forEach { item ->
                Text(
                    "${item.quantity} × ${item.food.name}",
                    color = AdminBrown,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider(
                color = AdminLightPeach
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Order Total",
                    color = AdminMutedBrown
                )

                Text(
                    php(order.totalCentavos),
                    color = AdminDarkOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// PREVIEW

@Preview(
    name = "Admin Dashboard",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun AdminScreenPreview() {
    CanteenGoTheme {
        Surface {
            AdminScreen(
                orders = listOf(
                    Order(
                        "1001",
                        "student1",
                        listOf(CartItem(DemoMenu.foods[0], 2)),
                        OrderStatus.PLACED
                    ),
                    Order(
                        "1002",
                        "student2",
                        listOf(CartItem(DemoMenu.foods[1], 1)),
                        OrderStatus.PREPARING
                    ),
                    Order(
                        "1003",
                        "student3",
                        listOf(CartItem(DemoMenu.foods[2], 2)),
                        OrderStatus.READY
                    ),
                    Order(
                        "1004",
                        "student4",
                        listOf(CartItem(DemoMenu.foods[3], 1)),
                        OrderStatus.COMPLETED
                    )
                ),
                report = "",
                onReport = {},
                onStock = { _, _ -> },
                onStaff = {}
            )
        }
    }
}
