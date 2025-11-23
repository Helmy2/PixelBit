package com.example.pixelbit.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.pixelbit.presentation.features.auth.login.LoginScreen
import com.example.pixelbit.presentation.features.auth.signup.SignUpScreen
import com.example.pixelbit.presentation.features.auth.verification.VerificationScreen
import com.example.pixelbit.presentation.features.onboarding.OnboardingScreen
import com.example.pixelbit.presentation.features.splash.SplashScreen

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
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
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
                entry<Screen.Splash> {
                    SplashScreen(
                        onNavigateToOnboarding = {
                            navController.add(Screen.Onboarding)
                        },
                        onNavigateToSignIn = {
                            navController.add(Screen.SignIn)
                        }
                    )
                }

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
                // KEEP THE EXISTING ENTRIES BUT UPDATE NAVIGATION CALLS:
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
                            navController.add(Screen.Home)
                        },
                        onBack = {
                            navController.back()
                        }
                    )
                }

                entry<Screen.SignIn> {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.add(Screen.Home)
                        },
                        onForgotPassword = { },
                        onSignUpClick = {
                            navController.add(Screen.SignUp)
                        },
                    )
                }

                entry<Screen.Home> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Button(onClick = {
                            navController.add(Screen.SignUp)
                        }) {
                            Text(text = "Sign Up")
                        }
                    }
                }

                entry<Screen.Profile> {
                }

                entry<Screen.MyOrders> {
                }

                entry<Screen.Favorites> {
                }
            }
        )
    }
}

@Composable
fun BottomAppBar(navigator: AppNavigator) {
    NavigationBar {
        AppNavigator.TOP_LEVEL_ROUTES.forEach { destination ->
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