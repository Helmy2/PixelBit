package com.example.pixelbit.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pixelbit.presentation.features.auth.signup.SignUpScreen
import com.example.pixelbit.presentation.features.auth.verification.VerificationScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.SignUp.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = { email ->
                    navController.navigate(Screen.Verification.createRoute(email)) {
                        popUpTo(Screen.SignUp.route)
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignUp.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Verification.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationScreen(
                email = email,
                onVerificationSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onBack = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.SignIn.route) {
            // TODO: Implement Sign In Screen
        }

        composable(Screen.Home.route) {
            // TODO: Implement Home Screen
        }
    }
}

