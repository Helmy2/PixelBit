package com.example.pixelbit.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.pixelbit.presentation.features.auth.login.LoginScreen
import com.example.pixelbit.presentation.features.auth.signup.SignUpScreen
import com.example.pixelbit.presentation.features.auth.verification.VerificationScreen
import com.example.pixelbit.presentation.features.favorites.FavoritesScreen
import com.example.pixelbit.presentation.features.home.HomeScreen
import com.example.pixelbit.presentation.features.onboarding.OnboardingScreen

@Composable
fun NavGraph(
    navController: AppNavigator,
) {
    Scaffold(
        bottomBar = {
            AnimatedVisibility(navController.shouldShowAppBar()) {
                BottomAppBar(navController)
            }
        },
        contentWindowInsets = WindowInsets()
    ) { paddingValues ->
        NavDisplay(
            backStack = navController.backStack,
            onBack = { navController.back() },
            modifier = Modifier.padding(paddingValues),
            transitionSpec = {
                val isTopLevelTransition = navController.shouldShowAppBar()

                if (isTopLevelTransition) {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                } else {
                    slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                }
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            entryProvider = entryProvider {
                entry<Screen.Onboarding> {
                    OnboardingScreen(
                        onNavigateToSignUp = {
                            navController.add(Screen.SignUp)
                        },
                        onNavigateToSignIn = {
                            navController.add(Screen.SignIn)
                        }
                    )
                }
                entry<Screen.SignUp> {
                    SignUpScreen(
                        onSignUpSuccess = { email ->
                            navController.add(Screen.Verification(email))
                        },
                        onNavigateToSignIn = {
                            navController.add(Screen.SignIn)
                        }
                    )
                }

                entry<Screen.Verification> {
                    VerificationScreen(
                        email = it.email,
                        onVerificationSuccess = {
                            navController.addAsStart(Screen.Home)
                        },
                        onBack = {
                            navController.back()
                        }
                    )
                }

                entry<Screen.SignIn> {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.addAsStart(Screen.Home)
                        },
                        onForgotPassword = { },
                        onSignUpClick = {
                            navController.add(Screen.SignUp)
                        },
                    )
                }

                entry<Screen.Home> {
                    HomeScreen()
                }

                entry<Screen.Profile> {
                }

                entry<Screen.MyOrders> {
                }

                entry<Screen.Favorites> {
                    FavoritesScreen()
                }
            }
        )
    }
}

@Composable
fun BottomAppBar(navigator: AppNavigator) {
    NavigationBar {
        TOP_LEVEL_ROUTES.forEach { destination ->
            NavigationBarItem(
                selected = navigator.isSelected(destination.route),
                onClick = { navigator.addTopLevel(destination.route) },
                icon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null
                    )
                }
            )
        }
    }
}