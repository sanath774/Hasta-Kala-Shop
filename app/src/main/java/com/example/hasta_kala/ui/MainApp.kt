package com.example.hasta_kala.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hasta_kala.ui.screens.*

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomBarScreen("dashboard", "Home", Icons.Default.Home)
    object Products : BottomBarScreen("products", "Products", Icons.Default.ShoppingBag)
    object QuickBilling : BottomBarScreen("quick_billing", "Billing", Icons.Default.Receipt)
    object Analytics : BottomBarScreen("analytics", "Analytics", Icons.Default.Analytics)
    object Profile : BottomBarScreen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        composable(Screen.MainContent.route) { MainContent() }
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    val screens = listOf(
        BottomBarScreen.Dashboard,
        BottomBarScreen.Products,
        BottomBarScreen.QuickBilling,
        BottomBarScreen.Analytics,
        BottomBarScreen.Profile
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF673AB7),
                            selectedTextColor = Color(0xFF673AB7),
                            indicatorColor = Color(0xFFD1C4E9)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomBarScreen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomBarScreen.Dashboard.route) { DashboardScreen(navController) }
            composable(BottomBarScreen.Products.route) { ProductsScreen() }
            composable(BottomBarScreen.QuickBilling.route) { QuickBillingScreen() }
            composable(BottomBarScreen.Analytics.route) { AnalyticsScreen(navController) }
            composable(BottomBarScreen.Profile.route) { ProfileScreen(navController) }
            
            // Nested screens
            composable(Screen.Inventory.route) { InventoryScreen(navController) }
            composable(Screen.IncomeLog.route) { IncomeLogScreen(navController) }
            composable(Screen.AIInsights.route) { AIInsightsScreen(navController) }
        }
    }
}
