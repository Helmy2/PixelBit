package com.example.pixelbit.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object SignUp : Screen()

    @Serializable
    data class Verification(val email: String) : Screen()

    @Serializable
    data object SignIn : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Onboarding : Screen()

    @Serializable
    data object Splash : Screen()


    @Serializable
    data object MyOrders : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Favorites : Screen()
}

data class TopLevelDestination(
    val route: Screen,
    val selectedIcon: ImageVector,
)
