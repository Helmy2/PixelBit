package com.example.pixelbit.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.pixelbit.presentation.features.address.AddressScreen
import com.example.pixelbit.presentation.features.auth.forgotpassword.ForgotPasswordScreen
import com.example.pixelbit.presentation.features.auth.login.LoginScreen
import com.example.pixelbit.presentation.features.auth.signup.SignUpScreen
import com.example.pixelbit.presentation.features.auth.verification.VerificationScreen
import com.example.pixelbit.presentation.features.cart.CartScreen
import com.example.pixelbit.presentation.features.checkout.CheckoutScreen
import com.example.pixelbit.presentation.features.favorites.FavoritesScreen
import com.example.pixelbit.presentation.features.home.HomeScreen
import com.example.pixelbit.presentation.features.myorders.MyOrdersScreen
import com.example.pixelbit.presentation.features.onboarding.OnboardingScreen
import com.example.pixelbit.presentation.features.products.ProductsScreen
import com.example.pixelbit.presentation.features.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: AppNavigator,
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()

    val isTopLevelTransition = remember(navController.shouldShowAppBar()) {
        navController.shouldShowAppBar()
    }

    val navigationSuiteType = remember(windowAdaptiveInfo, isTopLevelTransition) {
        val calculateFromAdaptiveInfo =
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
        if (isTopLevelTransition) {
            calculateFromAdaptiveInfo
        } else {
            NavigationSuiteType.None
        }
    }

    NavigationSuiteScaffold(
        layoutType = navigationSuiteType,
        navigationSuiteItems = {
            bottomAppBar(navController)
        },
    ) {
        NavDisplay(
            backStack = navController.backStack,
            transitionSpec = {
                if (isTopLevelTransition) {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                } else {
                    slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                }
            },
            popTransitionSpec = {
                if (isTopLevelTransition) {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                } else {
                    slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                }
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            entryDecorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
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
                        onForgotPassword = {
                            navController.add(Screen.ForgotPassword)
                        },
                        onSignUpClick = {
                            navController.add(Screen.SignUp)
                        },
                    )
                }

                entry<Screen.ForgotPassword> {
                    ForgotPasswordScreen(
                        onNavigateBack = {
                            navController.back()
                        }
                    )
                }

                entry<Screen.Home> {
                    HomeScreen(onCategoryClick = { category ->
                        navController.add(Screen.Products(category))
                    })
                }

                entry<Screen.Products> {
                    ProductsScreen(
                        categoryName = it.category,
                        onBackClick = { navController.back() }
                    )
                }

                entry<Screen.Profile> {
                    ProfileScreen()
                }

                entry<Screen.Cart> {
                    CartScreen()
                }

                entry<Screen.Checkout> {
                    CheckoutScreen()
                }

                entry<Screen.MyOrders> {
                    MyOrdersScreen()
                }

                entry<Screen.Favorites> {
                    FavoritesScreen()
                }

                entry<Screen.Address> {
                    AddressScreen()
                }
            }
        )
    }
}

fun NavigationSuiteScope.bottomAppBar(navigator: AppNavigator) {
    TOP_LEVEL_ROUTES.forEach { destination ->
        item(
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