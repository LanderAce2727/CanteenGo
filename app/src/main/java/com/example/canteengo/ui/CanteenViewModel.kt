package com.example.canteengo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canteengo.data.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class CanteenViewModel : ViewModel() {
    var cart by mutableStateOf<List<CartItem>>(emptyList())
        private set
    var orders by mutableStateOf<List<Order>>(emptyList())
        private set
    var foods by mutableStateOf(DemoMenu.foods)
        private set
    var isOnline by mutableStateOf(false)
        private set
    var role by mutableStateOf("student")
        private set
    var username by mutableStateOf("")
        private set
    var busy by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set
    var report by mutableStateOf("")
        private set
    private var api: CanteenApi? = null
    private var session: ServerSession? = null
    private var nextOrderNumber = 1
    private var pendingSignature = ""
    private var pendingRequestId = ""
    val totalCentavos: Int get() = cart.sumOf { it.totalCentavos }
    val itemCount: Int get() = cart.sumOf { it.quantity }

    fun clearMessage() { message = null }

    private fun request(block: suspend () -> Unit) {
        if (busy) return
        busy = true
        viewModelScope.launch {
            try {
                block()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                message = when (error) {
                    is ApiException -> error.message
                    is IllegalArgumentException -> error.message
                    is IOException -> "Cannot reach the server. Check its address and your connection, then retry."
                    else -> "Could not read the server response. Please try again."
                }
            } finally {
                busy = false
            }
        }
    }

    fun connect(address: String, name: String, password: String, register: Boolean, onConnected: () -> Unit) {
        request {
            val client = CanteenApi(address)
            if (register) client.register(name.trim(), password)
            val signedIn = client.login(name.trim(), password)
            try {
                val menu = client.menu(signedIn.token)
                val savedOrders = client.orders(signedIn.token)
                api = client
                session = signedIn
                isOnline = true
                role = signedIn.role
                username = signedIn.username
                foods = menu
                orders = savedOrders
                cart = emptyList()
                pendingSignature = ""
                onConnected()
            } catch (error: Exception) {
                try { client.logout(signedIn.token) } catch (_: IOException) { /* session expires server-side */ }
                throw error
            }
        }
    }

    fun disconnect(onDone: () -> Unit) {
        request {
            var revoked = true
            val client = api
            val signedIn = session
            try {
                if (client != null && signedIn != null) client.logout(signedIn.token)
            } catch (_: IOException) {
                revoked = false
            }
            report = ""
            session = null
            api = null
            isOnline = false
            role = "student"
            username = ""
            cart = emptyList()
            orders = emptyList()
            foods = DemoMenu.foods
            pendingSignature = ""
            onDone()
            if (!revoked) message = "Signed out on this phone. The server session could not be revoked and will expire automatically."
        }
    }

    fun refresh() {
        val client = api ?: return
        val token = session?.token ?: return
        request {
            val menu = client.menu(token)
            val savedOrders = client.orders(token)
            foods = menu
            orders = savedOrders
        }
    }

    fun loadReport(format: String) {
        val client = api ?: return
        val token = session?.token ?: return
        if (format !in listOf("json", "csv", "xml")) return
        request { report = client.report(token, format) }
    }

    fun updateStock(foodId: String, stock: Int) {
        val client = api ?: return
        val token = session?.token ?: return
        if (!foodId.matches(Regex("[a-z0-9_-]{1,40}")) || stock !in 0..10000) {
            message = "Enter a valid food ID and stock quantity (0–10000)."
            return
        }
        request {
            client.updateStock(token, foodId, stock)
            foods = client.menu(token)
            message = "Stock updated."
        }
    }

    fun add(foodId: String) {
        if (busy) return
        val food = foods.find { it.id == foodId } ?: return
        if (cart.none { it.food.id == foodId }) cart = cart + CartItem(food, 1)
        else changeQuantity(foodId, 1)
    }

    fun changeQuantity(foodId: String, delta: Int) {
        if (busy) return
        cart = cart.map { item ->
            if (item.food.id == foodId) item.copy(quantity = (item.quantity + delta).coerceIn(0, 99)) else item
        }.filter { it.quantity > 0 }
    }

    fun remove(foodId: String) {
        if (!busy) cart = cart.filterNot { it.food.id == foodId }
    }

    /** Local preview/demo flow only. Real orders go through submitOrder. */
    fun placeOrder(customer: String): String? {
        if (isOnline || cart.isEmpty() || customer.isBlank()) return null
        val number = "DEMO-%04d".format(nextOrderNumber++)
        orders = listOf(Order(number, customer.trim(), cart.toList())) + orders
        cart = emptyList()
        return number
    }

    fun submitOrder(customer: String, onPlaced: (String) -> Unit) {
        if (!isOnline) {
            placeOrder(customer)?.let(onPlaced)
            return
        }
        if (cart.isEmpty() || customer.isBlank()) return
        val client = api ?: return
        val token = session?.token ?: return
        val snapshot = cart.toList()
        // A retry after a timeout reuses the same key: one checkout cannot create two orders.
        val signature = customer.trim() + snapshot.joinToString { "${it.food.id}:${it.quantity}" }
        if (signature != pendingSignature) {
            pendingSignature = signature
            pendingRequestId = UUID.randomUUID().toString()
        }
        request {
            val saved = client.place(token, customer.trim(), snapshot, pendingRequestId)
            orders = listOf(saved) + orders.filterNot { it.number == saved.number }
            cart = emptyList()
            pendingSignature = ""
            onPlaced(saved.number)
        }
    }

    fun advanceOrder(number: String) {
        if (!isOnline) {
            orders = orders.map { order ->
                if (order.number == number) order.copy(status = order.status.next() ?: order.status) else order
            }
            return
        }
        val client = api ?: return
        val token = session?.token ?: return
        val next = orders.find { it.number == number }?.status?.next() ?: return
        request {
            val updated = client.advance(token, number, next)
            orders = orders.map { if (it.number == number) updated else it }
        }
    }
}
