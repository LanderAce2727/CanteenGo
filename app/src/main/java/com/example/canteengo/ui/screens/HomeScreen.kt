package com.example.canteengo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canteengo.R
import com.example.canteengo.ui.theme.CanteenGoTheme


private val PeachBackground = Color(0xFFFFF0DC)
private val Orange = Color(0xFFE87524)
private val DarkOrange = Color(0xFFB95319)
private val BrownText = Color(0xFF60391F)
private val MutedBrown = Color(0xFF8A674F)
private val WhiteCream = Color(0xFFFFFAF4)
private val LightPeach = Color(0xFFFFE2C2)

@Composable
fun HomeScreen(
    studentName: String = "Student",
    walletBalance: String = "₱250.00",
    onMenuClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFoodClick: (String) -> Unit = {}
) {

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
                .padding(horizontal = 22.dp)
        ) {

            Spacer(modifier = Modifier.height(35.dp))

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good day ☀️",
                        fontSize = 13.sp,
                        color = MutedBrown
                    )

                    Text(
                        text = "Hello, $studentName!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownText
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(LightPeach, CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // WALLET CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Orange
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "MY E-WALLET BALANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = walletBalance,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "CanteenGo Wallet",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            // CATEGORIES
            Text(
                text = "Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrownText
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                CategoryItem(
                    icon = Icons.Outlined.Restaurant,
                    label = "Meals",
                    onClick = onMenuClick
                )

                CategoryItem(
                    icon = Icons.Outlined.LocalDrink,
                    label = "Drinks",
                    onClick = onMenuClick
                )

                CategoryItem(
                    icon = Icons.Outlined.Fastfood,
                    label = "Snacks",
                    onClick = onMenuClick
                )

//                CategoryItem(
//                    icon = Icons.Outlined.Cake,
//                    label = "Desserts",
//                    onClick = onMenuClick
//                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // POPULAR FOOD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular Today",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrownText
                )

                TextButton(onClick = onMenuClick) {
                    Text(
                        "See all",
                        color = DarkOrange,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FIRST ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FoodCard(
                    imageRes = R.drawable.chicken_meal,
                    name = "Chicken Meal",
                    price = "₱45.00",
                    modifier = Modifier.weight(1f),
                    onClick = { onFoodClick("Chicken Meal") }
                )

                FoodCard(
                    imageRes = R.drawable.special_pancit,
                    name = "Special Pancit",
                    price = "₱40.00",
                    modifier = Modifier.weight(1f),
                    onClick = { onFoodClick("Special Pancit") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECOND ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FoodCard(
                    imageRes = R.drawable.iced_coffee,
                    name = "Iced Coffee",
                    price = "₱45.00",
                    modifier = Modifier.weight(1f),
                    onClick = { onFoodClick("Iced Coffee") }
                )

                FoodCard(
                    imageRes = R.drawable.siomai,
                    name = "Siomai Rice",
                    price = "₱45.00",
                    modifier = Modifier.weight(1f),
                    onClick = { onFoodClick("Siomai Rice") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // BOTTOM NAVIGATION
        NavigationBar(
            containerColor = WhiteCream,
            tonalElevation = 4.dp
        ) {
            NavigationBarItem(
                selected = true,
                onClick = {},
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
                selected = false,
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

// CATEGORY ITEM
@Composable
private fun RowScope.CategoryItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(
                WhiteCream,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Orange,
            modifier = Modifier.size(27.dp)
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = BrownText
        )
    }
}

// FOOD CARD
@Composable
private fun FoodCard(
    imageRes: Int,
    name: String,
    price: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = WhiteCream
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(13.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrownText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = price,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkOrange
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    CanteenGoTheme {
        HomeScreen(
            studentName = "Alex",
            walletBalance = "₱250.00"
        )
    }
}