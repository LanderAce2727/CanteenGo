package com.example.canteengo.data

import java.util.Locale

data class FoodItem(val id: String, val name: String, val description: String, val category: String, val priceCentavos: Int, val emoji: String)

data class CartItem(val food: FoodItem, val quantity: Int) {
    val totalCentavos: Int get() = food.priceCentavos * quantity
}

enum class OrderStatus(val label: String) {
    PLACED("Order placed"), PREPARING("Preparing"), READY("Ready for pickup"), COMPLETED("Collected");
    fun next(): OrderStatus? = entries.getOrNull(ordinal + 1)
}

data class Order(val number: String, val customer: String, val items: List<CartItem>, val status: OrderStatus = OrderStatus.PLACED) {
    val totalCentavos: Int get() = items.sumOf { it.totalCentavos }
}

fun php(centavos: Int): String = String.format(Locale.US, "₱%,.2f", centavos / 100.0)

object DemoMenu {
    val foods = listOf(
        FoodItem("chicken", "Chicken adobo", "Savory chicken with steamed rice.", "Meals", 6500, "🍗"),
        FoodItem("sisig", "Sisig rice bowl", "Pork sisig over fluffy rice.", "Meals", 7500, "🍲"),
        FoodItem("vegetables", "Vegetable rice bowl", "Seasonal vegetables with rice.", "Meals", 4500, "🥗"),
        FoodItem("pancit", "Pancit canton", "Stir-fried noodles and vegetables.", "Snacks", 3500, "🍜"),
        FoodItem("sandwich", "Egg sandwich", "Fresh egg filling in soft bread.", "Snacks", 3000, "🥪"),
        FoodItem("banana", "Banana cue", "Sweet caramelized banana skewers.", "Snacks", 2000, "🍌"),
        FoodItem("tea", "Iced tea", "Refreshing chilled lemon tea.", "Drinks", 2000, "🥤"),
        FoodItem("water", "Bottled water", "Cool drinking water, 500 ml.", "Drinks", 1500, "💧")
    )
    val categories = listOf("All", "Meals", "Snacks", "Drinks")
}
