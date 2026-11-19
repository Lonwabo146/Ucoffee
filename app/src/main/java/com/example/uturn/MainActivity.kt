package com.example.uturn

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uturn.data.AuthViewModel
import com.example.uturn.data.CartViewModel
import com.example.uturn.data.CatalogViewModel
import com.example.uturn.data.OrdersViewModel
import com.example.uturn.data.PaymentMethod
import com.example.uturn.data.ProductRepository
import com.example.uturn.data.SessionManager
import com.example.uturn.data.TokenStore
import com.example.uturn.screens.CartScreen
import com.example.uturn.screens.CheckoutScreen
import com.example.uturn.screens.ForgotPasswordScreen
import com.example.uturn.screens.HomeScreen
import com.example.uturn.screens.LoginScreen
import com.example.uturn.screens.OnboardingScreen
import com.example.uturn.screens.OrderConfirmationScreen
import com.example.uturn.screens.OrdersScreen
import com.example.uturn.screens.ProductDetailScreen
import com.example.uturn.screens.ProfileScreen
import com.example.uturn.screens.RegisterScreen
import com.example.uturn.screens.SplashScreen
import com.example.uturn.screens.StoryScreen
import com.example.uturn.ui.theme.UturnCoffeeTheme
import com.example.uturn.ui.theme.UturnTextGrey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    data object Story : BottomNavItem("story", "Story", Icons.Filled.Favorite)
    data object Cart : BottomNavItem("cart", "Cart", Icons.Filled.ShoppingCart)
    data object Orders : BottomNavItem("orders", "Orders", Icons.Filled.List)
    data object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person)
}

private val topLevelRoutes = setOf("home", "story", "cart", "orders", "profile")

class MainActivity : ComponentActivity() {
    // Bumped every time the app comes back to the foreground (e.g. after paying on PayFast)
    private var resumeTick by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenStore.init(this)   // loads the encrypted login token before any screen needs it
        enableEdgeToEdge()
        setContent {
            UturnCoffeeTheme {
                UturnApp(resumeTick)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeTick++
    }
}

@Composable
fun UturnApp(resumeTick: Int) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // One cart, one order store, one menu and one auth state shared by every screen
    val cartViewModel: CartViewModel = viewModel()
    val ordersViewModel: OrdersViewModel = viewModel()
    val catalogViewModel: CatalogViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val cartCount by cartViewModel.itemCount.collectAsStateWithLifecycle()
    val catalogUi by catalogViewModel.ui.collectAsStateWithLifecycle()
    val placing by ordersViewModel.placing.collectAsStateWithLifecycle()
    val displayName by sessionManager.displayName.collectAsStateWithLifecycle(initialValue = null)
    val isLoggedIn by sessionManager.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val savedPhone by sessionManager.phone.collectAsStateWithLifecycle(initialValue = null)
    val savedAddress by sessionManager.address.collectAsStateWithLifecycle(initialValue = null)
    val savedPayment by sessionManager.paymentMethod.collectAsStateWithLifecycle(initialValue = null)
    val preferredPayment = PaymentMethod.entries.firstOrNull { it.name == savedPayment }
        ?: PaymentMethod.PAYFAST

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in topLevelRoutes

    // Reload the menu every time Home is shown (picks up products the admin added or changed)
    LaunchedEffect(currentRoute) {
        if (currentRoute == BottomNavItem.Home.route) catalogViewModel.load()
    }

    // Poll the server while the Orders tab or an order confirmation is open, so payment and admin status changes appear
    val watchingOrders = currentRoute == BottomNavItem.Orders.route ||
            currentRoute?.startsWith("confirmation") == true
    LaunchedEffect(watchingOrders, isLoggedIn) {
        if (isLoggedIn && watchingOrders) {
            while (true) {
                ordersViewModel.refresh()
                delay(5_000)
            }
        }
    }

    // Coming back from the PayFast page: refresh straight away
    LaunchedEffect(resumeTick) {
        if (resumeTick > 0) ordersViewModel.refresh()
    }

    // Switch between top-level tabs without stacking copies of them
    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(BottomNavItem.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Story,
                        BottomNavItem.Cart,
                        BottomNavItem.Orders,
                        BottomNavItem.Profile
                    )
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                if (item == BottomNavItem.Cart && cartCount > 0) {
                                    BadgedBox(badge = { Badge { Text("$cartCount") } }) {
                                        Icon(item.icon, contentDescription = item.label)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = { navigateTopLevel(item.route) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(
                    sessionManager = sessionManager,
                    onFinished = { onboardingDone ->
                        navController.navigate(if (onboardingDone) "login" else "onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            composable("onboarding") {
                OnboardingScreen(
                    sessionManager = sessionManager,
                    onFinished = {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            composable("login") {
                val auth by authViewModel.ui.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) { authViewModel.clearError() }
                LoginScreen(
                    isLoading = auth.loading,
                    serverError = auth.error,
                    onLogin = { email, password ->
                        authViewModel.login(email, password) { user ->
                            scope.launch {
                                sessionManager.setLoggedIn(user.name)
                                user.phone?.takeIf { it.isNotBlank() }?.let {
                                    sessionManager.saveDeliveryDetails(phone = it, address = null)
                                }
                            }
                            ordersViewModel.refresh()
                            navController.navigate("home") { popUpTo("login") { inclusive = true } }
                        }
                    },
                    onSignUp = { navController.navigate("register") },
                    onGuest = {
                        // Guest = not logged in: no saved name, no token, no previous account's orders
                        scope.launch { sessionManager.logout() }
                        TokenStore.clear()
                        ordersViewModel.clearLocal()
                        navController.navigate("home") { popUpTo("login") { inclusive = true } }
                    },
                    onForgotPassword = { navController.navigate("forgot") }
                )
            }

            composable("forgot") {
                ForgotPasswordScreen(
                    onSubmit = { /* TODO: POST /auth/forgot-password with the email */ },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("register") {
                val auth by authViewModel.ui.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) { authViewModel.clearError() }
                RegisterScreen(
                    isLoading = auth.loading,
                    serverError = auth.error,
                    onRegister = { fullName, email, phone, password ->
                        authViewModel.register(fullName, email, password, phone) { user ->
                            scope.launch {
                                sessionManager.setLoggedIn(user.name)
                                sessionManager.saveDeliveryDetails(phone = phone, address = null)
                            }
                            ordersViewModel.refresh()
                            navController.navigate("home") { popUpTo("login") { inclusive = true } }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    displayName = displayName,
                    catalog = catalogUi,
                    onRetry = { catalogViewModel.load() },
                    onProductClick = { product -> navController.navigate("product/${product.id}") }
                )
            }

            composable(
                route = "product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { entry ->
                val product = ProductRepository.byId(entry.arguments?.getInt("productId") ?: -1)
                if (product == null) {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    ProductDetailScreen(
                        product = product,
                        onBack = { navController.popBackStack() },
                        onAddToCart = { line ->
                            cartViewModel.add(line)
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable(BottomNavItem.Cart.route) {
                CartScreen(
                    vm = cartViewModel,
                    onCheckout = { navController.navigate("checkout") },
                    onBrowse = { navigateTopLevel(BottomNavItem.Home.route) }
                )
            }

            composable("checkout") {
                val lines by cartViewModel.lines.collectAsStateWithLifecycle()
                val summary by cartViewModel.summary.collectAsStateWithLifecycle()
                CheckoutScreen(
                    lines = lines,
                    summary = summary,
                    initialName = displayName.orEmpty(),
                    initialPhone = savedPhone.orEmpty(),
                    initialAddress = savedAddress.orEmpty(),
                    placing = placing,
                    onBack = { navController.popBackStack() },
                    onPay = { details, method, fulfilment ->
                        if (!TokenStore.hasToken) {
                            Toast.makeText(context, "Please log in or register to place an order.", Toast.LENGTH_LONG).show()
                            navController.navigate("login")
                        } else {
                            ordersViewModel.placeOrder(
                                lines = lines,
                                method = method,
                                delivery = details,
                                fulfilment = fulfilment,
                                onPlaced = { order, paymentUrl ->
                                    // Navigate first, then clear, so Checkout never flashes an empty cart
                                    navController.navigate("confirmation/${order.number}") {
                                        popUpTo(BottomNavItem.Home.route)
                                    }
                                    cartViewModel.clear()
                                    if (!paymentUrl.isNullOrBlank()) {
                                        runCatching {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)))
                                        }.onFailure {
                                            Toast.makeText(context, "Couldn't open the payment page.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onError = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    }
                )
            }

            composable(
                route = "confirmation/{orderNumber}",
                arguments = listOf(navArgument("orderNumber") { type = NavType.StringType })
            ) { entry ->
                val number = entry.arguments?.getString("orderNumber")
                val orders by ordersViewModel.orders.collectAsStateWithLifecycle()
                OrderConfirmationScreen(
                    order = orders.firstOrNull { it.number == number },
                    onTrackOrder = { navigateTopLevel(BottomNavItem.Orders.route) },
                    onContinueShopping = { navigateTopLevel(BottomNavItem.Home.route) }
                )
            }

            composable(BottomNavItem.Orders.route) {
                val orders by ordersViewModel.orders.collectAsStateWithLifecycle()
                OrdersScreen(
                    orders = orders,
                    onBrowse = { navigateTopLevel(BottomNavItem.Home.route) }
                )
            }

            composable(BottomNavItem.Story.route) {
                StoryScreen(onBrowse = { navigateTopLevel(BottomNavItem.Home.route) })
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    displayName = displayName,
                    isLoggedIn = isLoggedIn,
                    phone = savedPhone,
                    address = savedAddress,
                    paymentMethod = preferredPayment,
                    onSaveDelivery = { phone, address ->
                        scope.launch { sessionManager.saveDeliveryDetails(phone, address) }
                    },
                    onSelectPayment = { method ->
                        scope.launch { sessionManager.savePaymentMethod(method.name) }
                    },
                    onOrderHistory = { navigateTopLevel(BottomNavItem.Orders.route) },
                    onLogin = {
                        navController.navigate("login") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onLogout = {
                        scope.launch { sessionManager.logout() }
                        TokenStore.clear()
                        ordersViewModel.clearLocal()
                        cartViewModel.clear()
                        navController.navigate("login") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

// Temporary — replaced one by one as we build the real screens
@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, color = UturnTextGrey)
    }
}