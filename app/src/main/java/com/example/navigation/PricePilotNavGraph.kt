package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AboutScreen
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

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToResults = { navController.navigate(Screen.ComparisonResults.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToWishlist = { navController.navigate(Screen.Wishlist.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToRecents = { navController.navigate(Screen.RecentComparisons.route) }
            )
        }
        composable(Screen.ComparisonResults.route) {
            ComparisonResultsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { navController.navigate(Screen.ProductDetails.route) },
                onNavigateToHistory = { navController.navigate(Screen.PriceHistory.route) }
            )
        }
        composable(Screen.ProductDetails.route) {
            ProductDetailsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PriceHistory.route) {
            PriceHistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = viewModel,
                onNavigateHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateWishlist = { navController.navigate(Screen.Wishlist.route) },
                onNavigateSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateResults = { navController.navigate(Screen.ComparisonResults.route) }
            )
        }
        composable(Screen.Wishlist.route) {
            WishlistScreen(
                viewModel = viewModel,
                onNavigateHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateSearch = { navController.navigate(Screen.Search.route) },
                onNavigateSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.RecentComparisons.route) {
            RecentComparisonsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateResults = { navController.navigate(Screen.ComparisonResults.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateSearch = { navController.navigate(Screen.Search.route) },
                onNavigateWishlist = { navController.navigate(Screen.Wishlist.route) },
                onNavigateAbout = { navController.navigate(Screen.About.route) }
            )
        }
        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
