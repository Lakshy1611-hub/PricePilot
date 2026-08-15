package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AiShoppingScreen
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.BestDealsScreen
import com.example.ui.screens.ComparisonResultsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PriceHistoryScreen
import com.example.ui.screens.ProductDetailsScreen
import com.example.ui.screens.RecentComparisonsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.viewmodel.PricePilotViewModel

@Composable
fun PricePilotNavGraph(viewModel: PricePilotViewModel) {
    val navController = rememberNavController()

    fun navigateTab(route: String) {
        navController.navigate(route) {
            // Keep each primary tab's UI/search state alive instead of recreating it.
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }) }
        composable(Screen.Onboarding.route) { OnboardingScreen(onGetStarted = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } }) }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToResults = { navController.navigate(Screen.ComparisonResults.route) },
                onNavigateToSearch = { navigateTab(Screen.Search.route) },
                onNavigateToWishlist = { navigateTab(Screen.Wishlist.route) },
                onNavigateToSettings = { navigateTab(Screen.Settings.route) },
                onNavigateToRecents = { navController.navigate(Screen.RecentComparisons.route) },
                onNavigateToAi = { navController.navigate(Screen.AiShopping.route) },
                onNavigateToAlerts = { navigateTab(Screen.Alerts.route) },
                onNavigateToDeals = { navigateTab(Screen.BestDeals.route) }
            )
        }
        composable(Screen.BestDeals.route) { BestDealsScreen(viewModel, { navigateTab(Screen.Home.route) }, { navController.navigate(Screen.ComparisonResults.route) }) }
        composable(Screen.ComparisonResults.route) { ComparisonResultsScreen(viewModel, { navController.popBackStack() }, { navController.navigate(Screen.ProductDetails.route) }, { navController.navigate(Screen.PriceHistory.route) }) }
        composable(Screen.ProductDetails.route) { ProductDetailsScreen(viewModel, onBack = { navController.popBackStack() }, onNavigateToHistory = { navController.navigate(Screen.PriceHistory.route) }) }
        composable(Screen.PriceHistory.route) { PriceHistoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) }
        composable(Screen.Search.route) { SearchScreen(viewModel, { navigateTab(Screen.Home.route) }, { navigateTab(Screen.Wishlist.route) }, { navigateTab(Screen.Settings.route) }, { navController.navigate(Screen.ComparisonResults.route) }) }
        composable(Screen.Wishlist.route) { WishlistScreen(viewModel, { navigateTab(Screen.Home.route) }, { navigateTab(Screen.Search.route) }, { navigateTab(Screen.Settings.route) }) }
        composable(Screen.RecentComparisons.route) { RecentComparisonsScreen(viewModel, { navController.popBackStack() }, { navController.navigate(Screen.ComparisonResults.route) }) }
        composable(Screen.Alerts.route) { AlertsScreen { navigateTab(Screen.Home.route) } }
        composable(Screen.AiShopping.route) { AiShoppingScreen(viewModel) { navController.popBackStack() } }
        composable(Screen.Settings.route) { SettingsScreen(viewModel, { navigateTab(Screen.Home.route) }, { navigateTab(Screen.Search.route) }, { navigateTab(Screen.Wishlist.route) }, { navController.navigate(Screen.About.route) }) }
        composable(Screen.About.route) { AboutScreen { navController.popBackStack() } }
    }
}
