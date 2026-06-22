package com.zidansyahidagrifasa0072.assesment3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zidansyahidagrifasa0072.assesment3.ui.screen.add_review.AddReviewScreen
import com.zidansyahidagrifasa0072.assesment3.ui.screen.detail_review.DetailReviewScreen
import com.zidansyahidagrifasa0072.assesment3.ui.screen.edit_review.EditReviewScreen
import com.zidansyahidagrifasa0072.assesment3.ui.screen.home.HomeScreen
import com.zidansyahidagrifasa0072.assesment3.ui.screen.login.LoginScreen
import com.zidansyahidagrifasa0072.assesment3.ui.screen.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object AddReview : Screen("add_review")
    object DetailReview : Screen("detail_review/{reviewId}") {
        fun createRoute(reviewId: String) = "detail_review/$reviewId"
    }
    object EditReview : Screen("edit_review/{reviewId}") {
        fun createRoute(reviewId: String) = "edit_review/$reviewId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddReview = { navController.navigate(Screen.AddReview.route) },
                onNavigateToDetailReview = { reviewId ->
                    navController.navigate(Screen.DetailReview.createRoute(reviewId))
                }
            )
        }

        composable(Screen.AddReview.route) {
            AddReviewScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DetailReview.route,
            arguments = listOf(navArgument("reviewId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId") ?: ""
            DetailReviewScreen(
                reviewId = reviewId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditReview = { id ->
                    navController.navigate(Screen.EditReview.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.EditReview.route,
            arguments = listOf(navArgument("reviewId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId") ?: ""
            EditReviewScreen(
                reviewId = reviewId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}