package com.example.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object UrlCompare : Screen("url_compare")
    object Search : Screen("search")
    object ComparisonResults : Screen("comparison_results")
    object ProductDetails : Screen("product_details")
    object PriceHistory : Screen("price_history")
    object Wishlist : Screen("wishlist")
    object RecentComparisons : Screen("recent_comparisons")
    object Alerts : Screen("alerts")
    object AiShopping : Screen("ai_shopping")
    object Settings : Screen("settings")
    object About : Screen("about")
}
