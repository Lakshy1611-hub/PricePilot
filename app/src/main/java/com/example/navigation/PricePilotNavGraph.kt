package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AiShoppingScreen
import com.example.ui.screens.AlertsScreen
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
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }) }
        composable(Screen.Onboarding.route) { OnboardingScreen(onGetStarted = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } }) }
        composable(Screen.Home.route) { HomeScreen(viewModel, { navController.navigate(Screen.ComparisonResults.route) }, { navController.navigate(Screen.Search.route) }, { navController.navigate(Screen.Wishlist.route) }, { navController.navigate(Screen.Settings.route) }, { navController.navigate(Screen.RecentComparisons.route) }) }
        composable(Screen.ComparisonResults.route) { ComparisonResultsScreen(viewModel, { navController.popBackStack() }, { navController.navigate(Screen.ProductDetails.route) }, { navController.navigate(Screen.PriceHistory.route) }) }
        composable(Screen.ProductDetails.route) { ProductDetailsScreen(viewModel) { navController.popBackStack() } }
        composable(Screen.PriceHistory.route) { PriceHistoryScreen(viewModel) { navController.popBackStack() } }
        composable(Screen.Search.route) { SearchScreen(viewModel, { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }, { navController.navigate(Screen.Wishlist.route) }, { navController.navigate(Screen.Settings.route) }, { navController.navigate(Screen.ComparisonResults.route) }) }
        composable(Screen.Wishlist.route) { WishlistScreen(viewModel, { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }, { navController.navigate(Screen.Search.route) }, { navController.navigate(Screen.Settings.route) }) }
        composable(Screen.RecentComparisons.route) { RecentComparisonsScreen(viewModel, { navController.popBackStack() }, { navController.navigate(Screen.ComparisonResults.route) }) }
        composable(Screen.Alerts.route) { AlertsScreen { navController.popBackStack() } }
        composable(Screen.AiShopping.route) { AiShoppingScreen(viewModel) { navController.popBackStack() } }
        composable(Screen.Settings.route) { SettingsScreen(viewModel, { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }, { navController.navigate(Screen.Search.route) }, { navController.navigate(Screen.Wishlist.route) }, { navController.navigate(Screen.About.route) }) }
        composable(Screen.About.route) { AboutScreen { navController.popBackStack() } }
    }
}
