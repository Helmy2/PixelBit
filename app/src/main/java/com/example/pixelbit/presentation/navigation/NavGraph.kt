package com.example.pixelbit.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.pixelbit.presentation.features.auth.signup.SignUpScreen
import com.example.pixelbit.presentation.features.auth.verification.VerificationScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: Screen = Screen.SignUp
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.SignUp> {
            SignUpScreen(
                onSignUpSuccess = { email ->
                    navController.navigate(Screen.Verification(email)) {
                        popUpTo(Screen.SignUp)
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn) {
                        popUpTo(Screen.SignUp) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Screen.Verification> { backStackEntry ->
            val email = backStackEntry.toRoute<Screen.Verification>().email
            VerificationScreen(
                email = email,
                onVerificationSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onBack = {
                    navController.navigate(Screen.SignUp) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Screen.SignIn> {
            // TODO: Implement Sign In Screen
        }

        composable<Screen.Home> {
            // TODO: Implement Home Screen
        }
    }
}

