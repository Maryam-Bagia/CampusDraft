package com.example.campusdraft.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.campusdraft.presentation.screens.favorites.FavoritesScreen
import com.example.campusdraft.presentation.screens.form.FormScreen
import com.example.campusdraft.presentation.screens.history.HistoryScreen
import com.example.campusdraft.presentation.screens.home.HomeScreen
import com.example.campusdraft.presentation.screens.preview.PreviewScreen

/**
 * Root navigation graph for the entire app.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onApplicationTypeSelected = { appTypeId ->
                    navController.navigate(Screen.Form.createRoute(appTypeId))
                },
                onHistoryClick  = { navController.navigate(Screen.History.route) },
                onFavoritesClick = { navController.navigate(Screen.Favorites.route) }
            )
        }

        composable(
            route = Screen.Form.route,
            arguments = listOf(navArgument("applicationTypeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val appTypeId = backStackEntry.arguments?.getString("applicationTypeId") ?: return@composable
            FormScreen(
                applicationTypeId = appTypeId,
                onBack = { navController.popBackStack() },
                onPreview = { savedId ->
                    navController.navigate(Screen.Preview.createRoute(savedId))
                }
            )
        }

        composable(
            route = Screen.Preview.route,
            arguments = listOf(navArgument("savedId") { type = NavType.LongType })
        ) { backStackEntry ->
            val savedId = backStackEntry.arguments?.getLong("savedId") ?: return@composable
            PreviewScreen(
                savedId = savedId,
                onBack = { navController.popBackStack() },
                onNewApplication = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onItemClick = { savedId ->
                    navController.navigate(Screen.Preview.createRoute(savedId))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBack = { navController.popBackStack() },
                onItemClick = { savedId ->
                    navController.navigate(Screen.Preview.createRoute(savedId))
                }
            )
        }
    }
}