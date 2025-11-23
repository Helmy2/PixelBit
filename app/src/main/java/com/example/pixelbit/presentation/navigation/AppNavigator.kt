package com.example.pixelbit.presentation.navigation

import androidx.compose.runtime.mutableStateListOf
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppNavigator(
    private val onboardingRepository: OnboardingRepository,
    private val authRepository: AuthRepository
) {
    fun shouldKeepSplashScreenFlow(): Flow<Boolean> {
        return flow {
            val firstScreen = try {
                val isCompleted = onboardingRepository.isOnboardingCompleted()
                val isLoggedIn = authRepository.isUserLoggedIn()
                when {
                    !isCompleted -> Screen.Onboarding
                    isLoggedIn -> Screen.Home
                    else -> Screen.SignIn
                }
            } catch (_: Exception) {
                Screen.Onboarding
            }
            backStack.add(firstScreen)
            emit(false)
        }
    }

    val backStack = mutableStateListOf<Screen>()
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
