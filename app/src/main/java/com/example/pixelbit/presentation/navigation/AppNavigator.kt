package com.example.pixelbit.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.mutableStateListOf

class AppNavigator(startDestination: Screen) {
    companion object {
        val TOP_LEVEL_ROUTES: List<TopLevelDestination> =
            listOf(
                TopLevelDestination(
                    Screen.Home,
                    Icons.Default.Home,
                ),
                TopLevelDestination(
                    Screen.MyOrders,
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
    }

    val backStack = mutableStateListOf(startDestination)

    fun add(command: Screen) {
        if (backStack.lastOrNull() != command) {
            backStack.add(command)
        }
    }

    fun back() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    fun addTopLevel(command: Screen) {
        if (backStack.firstOrNull() == command) {
            if (backStack.size > 1) {
                backStack.removeRange(1, backStack.size)
            }
        } else {
            backStack.clear()
            backStack.add(command)
        }
    }

    fun isSelected(command: Screen): Boolean {
        return backStack.firstOrNull() == command
    }

    fun shouldShowAppBar(): Boolean {
        return TOP_LEVEL_ROUTES.any { it.route == backStack.lastOrNull() }
    }
}
