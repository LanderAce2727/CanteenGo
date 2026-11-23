package com.example.canteengo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.canteengo.ui.navigation.AppDestination
import com.example.canteengo.ui.navigation.AppDestination.*
import com.example.canteengo.ui.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenGoApp(model: CanteenViewModel) {
    var route by rememberSaveable { mutableStateOf(WELCOME.name) }
    var connecting by rememberSaveable { mutableStateOf(false) }
    var hadServerSession by rememberSaveable { mutableStateOf(false) }
    var category by rememberSaveable { mutableStateOf("All") }
    var selectedOrder by rememberSaveable { mutableStateOf<String?>(null) }
    val destination = AppDestination.valueOf(route)
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val studentTabs = listOf(HOME, MENU, CART, ORDERS)
    fun accountHome(): String = when (model.role) {
        "admin" -> ADMIN.name
        "staff" -> STAFF.name
        else -> HOME.name
    }
    val back: () -> Unit = {
        if (!model.busy) {
            if (connecting) connecting = false
            else route = when (destination) {
                CHECKOUT -> CART.name
                MENU, CART, ORDERS -> HOME.name
                STAFF -> if (model.isOnline && model.role == "admin") ADMIN.name else WELCOME.name
                else -> WELCOME.name
            }
        }
    }
    BackHandler(enabled = connecting || destination != WELCOME || model.busy, onBack = back)
    // Do not restore a server screen with local demo data after process death.
    LaunchedEffect(model.isOnline) {
        if (hadServerSession && !model.isOnline) {
            route = WELCOME.name
            selectedOrder = null
        }
        hadServerSession = model.isOnline
    }
    LaunchedEffect(model.message) {
        model.message?.let {
            snackbar.showSnackbar(it, duration = SnackbarDuration.Long)
            model.clearMessage()
        }
    }
    val browse: () -> Unit = { category = "All"; route = MENU.name }
    val add: (String) -> Unit = { foodId ->
        model.add(foodId)
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar("Added to cart")
        }
    }
    Scaffold(
        topBar = {
            if (destination != WELCOME && !connecting) TopAppBar(
                title = { Text(when (destination) {
                    STAFF -> "CanteenGo · Staff"
                    ADMIN -> "CanteenGo · Admin"
                    else -> "CanteenGo"
                }) },
                navigationIcon = {
                    if (destination == CHECKOUT || (destination == STAFF && model.role == "admin"))
                        TextButton(onClick = back, enabled = !model.busy) { Text("Back") }
                },
                actions = {
                    if (model.isOnline) TextButton(onClick = model::refresh, enabled = !model.busy) { Text("Refresh") }
                    TextButton(enabled = !model.busy, onClick = {
                        if (model.isOnline) model.disconnect {
                            selectedOrder = null
                            route = WELCOME.name
                        } else route = WELCOME.name
                    }) { Text(if (model.isOnline) "Sign out" else "Switch role") }
                }
            )
        },
        bottomBar = {
            if (!connecting && destination in studentTabs && (!model.isOnline || model.role == "student")) NavigationBar {
                studentTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = destination == tab,
                        onClick = { route = tab.name },
                        icon = { Text(tab.symbol, style = MaterialTheme.typography.titleLarge) },
                        label = { Text(if (tab == CART && model.itemCount > 0) "Cart (${model.itemCount})" else tab.title) }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding), horizontalAlignment = Alignment.CenterHorizontally) {
            if (destination != WELCOME && !connecting) Surface(
                color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (model.isOnline) "Signed in as ${model.username} · ${model.role} · Refresh for updates"
                    else "Local demo · Orders are not sent to the canteen",
                    style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp)
                )
            }
            Box(Modifier.widthIn(max = 1100.dp).weight(1f).fillMaxWidth()) {
                if (connecting) ServerSignInScreen(model.busy, onConnect = { address, name, password, register ->
                    model.connect(address, name, password, register) {
                        selectedOrder = null
                        connecting = false
                        route = accountHome()
                    }
                }, onBack = { connecting = false })
                else when (destination) {
                    WELCOME -> WelcomeScreen(
                        onStudent = { route = if (model.isOnline) accountHome() else HOME.name },
                        onStaff = { route = if (model.isOnline) accountHome() else STAFF.name },
                        onConnect = { if (model.isOnline) route = accountHome() else connecting = true }
                    )
                    HOME -> HomeScreen(onCategory = { category = it; route = MENU.name }, onAdd = add, foods = model.foods)
                    MENU -> MenuScreen(category, onCategory = { category = it }, onAdd = add, menu = model.foods)
                    CART -> CartScreen(model.cart, model.totalCentavos, model::changeQuantity, model::remove,
                        browse, onCheckout = { route = CHECKOUT.name })
                    CHECKOUT -> CheckoutScreen(model.cart, model.totalCentavos, onPlace = { name ->
                        model.submitOrder(name) { number -> selectedOrder = number; route = ORDERS.name }
                    }, onBrowse = browse, isDemo = !model.isOnline)
                    ORDERS -> OrdersScreen(model.orders, selectedOrder ?: model.orders.firstOrNull()?.number,
                        onSelect = { selectedOrder = it }, onBrowse = browse)
                    STAFF -> StaffDashboardScreen(model.orders, model::advanceOrder)
                    ADMIN -> AdminScreen(model.orders, model.report, model::loadReport, model::updateStock,
                        onStaff = { route = STAFF.name })
                }
            }
        }
    }
    if (model.busy) Dialog(onDismissRequest = {}) {
        Surface(shape = MaterialTheme.shapes.large) {
            Row(Modifier.padding(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(24.dp))
                Text("Please wait…")
            }
        }
    }
}
