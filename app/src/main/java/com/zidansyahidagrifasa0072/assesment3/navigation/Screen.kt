package com.zidansyahidagrifasa0072.assesment3.navigation

sealed class Screen(
    val route: String
) {
    data object Home : Screen("home")

    data object Favorite : Screen("favorite")

    data object Profile : Screen("profile")

    data object AddReview : Screen("add_review")

    data object EditReview : Screen("edit_review")

    data object Detail : Screen("detail")

}
