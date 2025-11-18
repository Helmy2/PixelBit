package com.example.pixelbit.presentation.navigation

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
}


