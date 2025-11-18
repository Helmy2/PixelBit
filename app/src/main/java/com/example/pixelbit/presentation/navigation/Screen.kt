package com.example.pixelbit.presentation.navigation

sealed class Screen(val route: String) {
    data object SignUp : Screen("sign_up")
    data object Verification : Screen("verification/{email}") {
        fun createRoute(email: String) = "verification/$email"
    }
    data object SignIn : Screen("sign_in")
    data object Home : Screen("home")
}


