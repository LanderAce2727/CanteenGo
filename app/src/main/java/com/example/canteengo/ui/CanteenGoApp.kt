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
import com.example.canteengo.data.ServerConfig
import com.example.canteengo.data.LoginPortal
import com.example.canteengo.ui.navigation.AppDestination.*
import com.example.canteengo.ui.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenGoApp(model: CanteenViewModel) {
    var route by rememberSaveable { mutableStateOf(WELCOME.name) }
    var connecting by rememberSaveable { mutableStateOf(false) }
    var loginPortalName by rememberSaveable { mutableStateOf(LoginPortal.CUSTOMER.name) }
    val loginPortal = LoginPortal.valueOf(loginPortalName)
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
                if (connecting) ServerSignInScreen(model.busy, onConnect = { name, password, register ->
                    model.connect(ServerConfig.BASE_URL, name, password, register, portal = loginPortal) {
                        selectedOrder = null
                        connecting = false
                        route = accountHome()
                    }
                }, onBack = { connecting = false }, portal = loginPortal)
                else when (destination) {
                    WELCOME -> WelcomeScreen(
                        onStudent = {
                            loginPortalName = LoginPortal.CUSTOMER.name
                            if (model.isOnline) route = accountHome() else connecting = true
                        },
                        onStaff = {
                            loginPortalName = LoginPortal.ADMIN.name
                            if (model.isOnline) route = accountHome() else connecting = true
                        },
                        onConnect = { route = if (model.isOnline) accountHome() else HOME.name }
                    )
                    HOME -> HomeScreen(
                        studentName = if (model.isOnline) model.username else "Student",
                        foods = model.foods,
                        onCategory = { category = it; route = MENU.name },
                        onMenuClick = { route = MENU.name },
                        onCartClick = { route = CART.name },
                        onOrdersClick = { route = ORDERS.name },
                        onFoodClick = { foodId -> add(foodId) }
                    )
                    MENU -> MenuScreen(category, onCategory = { category = it }, onAdd = add, menu = model.foods,
                        onHomeClick = { route = HOME.name }, onCartClick = { route = CART.name },
                        onOrdersClick = { route = ORDERS.name })
                    CART -> CartScreen(model.cart, model.totalCentavos, model::changeQuantity, model::remove,
                        browse, onCheckout = { route = CHECKOUT.name },
                        onHomeClick = { route = HOME.name }, onMenuClick = browse,
                        onCartClick = { route = CART.name }, onOrdersClick = { route = ORDERS.name })
                    CHECKOUT -> CheckoutScreen(
                        cart = model.cart,
                        total = model.totalCentavos,
                        onPlace = { name, _ ->
                            model.submitOrder(customer = name) { number ->
                                selectedOrder = number
                                route = ORDERS.name
                            }
                        },
                        onBrowse = browse,
                        isDemo = !model.isOnline,
                        onHomeClick = { route = HOME.name }, onMenuClick = browse,
                        onCartClick = { route = CART.name }, onOrdersClick = { route = ORDERS.name }
                    )
                    ORDERS -> OrdersScreen(model.orders, selectedOrder ?: model.orders.firstOrNull()?.number,
                        onSelect = { selectedOrder = it }, onBrowse = browse,
                        onHomeClick = { route = HOME.name }, onMenuClick = browse,
                        onCartClick = { route = CART.name }, onOrdersClick = { route = ORDERS.name })
                    STAFF -> StaffDashboardScreen(model.orders, model::advanceOrder)
                    ADMIN -> AdminScreen(model.orders, model.report, model::loadReport, model::updateStock,
                        onStaff = { route = STAFF.name }, foods = model.foods, isDemo = !model.isOnline)
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
