package com.example.hasta_kala.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Dashboard : Screen("dashboard")
    object Products : Screen("products")
    object QuickBilling : Screen("quick_billing")
    object Analytics : Screen("analytics")
    object Inventory : Screen("inventory")
    object IncomeLog : Screen("income_log")
    object AIInsights : Screen("ai_insights")
    object Profile : Screen("profile")
    object MainContent : Screen("main_content")
}
