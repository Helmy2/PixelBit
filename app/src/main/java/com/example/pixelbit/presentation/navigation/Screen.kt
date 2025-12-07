package com.example.pixelbit.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object SignUp : Screen()

    @Serializable
    data class Verification(val email: String) : Screen()

    @Serializable
    data object SignIn : Screen()

    @Serializable
    data object ForgotPassword : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data class ProductDetails(val productId: String) : Screen()

    @Serializable
    data object Onboarding : Screen()

    @Serializable
    data object MyOrders : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Favorites : Screen()

    @Serializable
    data object Cart : Screen()

    @Serializable
    data object Checkout : Screen()

    @Serializable
    data object Address : Screen()

    @Serializable
    data class CategoryDetails(val id: String) : Screen()
}

data class TopLevelDestination(
    val route: Screen,
    val selectedIcon: ImageVector,
)

val TOP_LEVEL_ROUTES: List<TopLevelDestination> =
    listOf(
        TopLevelDestination(
            Screen.Home,
            Icons.Default.Home,
        ),
        TopLevelDestination(
            Screen.Cart,
            Icons.Default.ShoppingCart,
        ),
        TopLevelDestination(
            Screen.Favorites,
            Icons.Default.Favorite,
        ),
        TopLevelDestination(
            Screen.Profile,
            Icons.Default.Person,
        ),
    )
