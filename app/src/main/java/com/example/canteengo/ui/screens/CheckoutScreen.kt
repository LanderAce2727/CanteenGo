
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun CheckoutScreen(
    cart: List<CartItem>,
    total: Int,
    onPlace: (String, String) -> Unit,
    onBrowse: () -> Unit,
    isDemo: Boolean = true,
    onHomeClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    var name by rememberSaveable { mutableStateOf("") }
    var paymentMethod by rememberSaveable {
        mutableStateOf("Cash on Pickup")
    }

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
                .imePadding()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Spacer(modifier = Modifier.height(25.dp))

            // HEADER
            Text(
                text = "Checkout",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BrownText
            )

            Text(
                text = "Almost lunchtime! Review your order before placing it.",
                color = MutedBrown,
                style = MaterialTheme.typography.bodyMedium
            )

            if (cart.isEmpty()) {

                EmptyState(
                    "Nothing to checkout",
                    "Add food before placing an order.",
                    "Browse menu",
                    onBrowse
                )

            } else {

                // PICKUP INFORMATION
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = WhiteCream
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Pickup Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrownText
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it.take(80)
                            },
                            label = {
                                Text("Name for pickup")
                            },
                            placeholder = {
                                Text("Enter your name")
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text(
                            "Pickup location: Canteen counter",
                            color = MutedBrown,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // ORDER SUMMARY
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
                            "Order Summary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrownText
                        )

                        cart.forEach { item ->

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
                                            .size(58.dp)
                                            .clip(
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(58.dp)
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
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrownText
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
                                php(total),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Orange
                            )
                        }
                    }
                }

                // PAYMENT METHOD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = WhiteCream
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Payment Method",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrownText
                        )

                        PaymentOption(
                            title = "Cash on Pickup",
                            description = "Pay at the canteen counter when your order is ready.",
                            selected = paymentMethod == "Cash on Pickup",
                            onClick = {
                                paymentMethod = "Cash on Pickup"
                            }
                        )

                        HorizontalDivider(color = LightPeach)

                        PaymentOption(
                            title = "Online Payment",
                            description = "Pay digitally. Online payment processing will be connected later.",
                            selected = paymentMethod == "Online Payment",
                            onClick = {
                                paymentMethod = "Online Payment"
                            }
                        )
                    }
                }

                // PLACE ORDER
                Button(
                    onClick = {
                        onPlace(name.trim(), paymentMethod)
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        "${if (isDemo) "Place Demo Order" else "Place Order"} • ${php(total)}",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
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
                label = {
                    Text("Home")
                },
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
                label = {
                    Text("Menu")
                },
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
                onClick = onCartClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                },
                label = {
                    Text("Cart")
                },
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
                onClick = onOrdersClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = "Orders"
                    )
                },
                label = {
                    Text("Orders")
                },
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

// PAYMENT OPTION
@Composable
private fun PaymentOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Orange
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                color = BrownText
            )

            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MutedBrown
            )
        }
    }
}

// PREVIEW
@Preview(
    name = "Checkout screen",
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun CheckoutScreenPreview() {
    CanteenGoTheme {
        Surface {
            CheckoutScreen(
                cart = listOf(
                    CartItem(DemoMenu.foods[8], 2),
                    CartItem(DemoMenu.foods[10], 1)
                ),
                total = 13500,
                onPlace = { _, _ -> },
                onBrowse = {}
            )
        }
    }
}
