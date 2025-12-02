package com.example.pixelbit.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.mutableStateListOf
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class AppNavigator(
    private val onboardingRepository: OnboardingRepository,
    private val authRepository: AuthRepository
) : SavedStateRegistry.SavedStateProvider {
    fun shouldKeepSplashScreenFlow(): Flow<Boolean> {
        return flow {
            if (backStack.isEmpty()) {
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
            }
            emit(false)
        }
    }

    val backStack = mutableStateListOf<Screen>()
    fun add(command: Screen) {
        if (backStack.lastOrNull() != command) {
            backStack.add(command)
        }
    }

    fun addAsStart(command: Screen) {
        backStack.clear()
        backStack.add(command)
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
            backStack.add(Screen.Home)
            backStack.add(command)
        }
    }

    fun isSelected(command: Screen): Boolean {
        return backStack.lastOrNull() == command
    }

    fun shouldShowAppBar(): Boolean {
        return TOP_LEVEL_ROUTES.any { it.route == backStack.lastOrNull() }
    }

    private val backStackStateKey = "key_nav_back_stack"

    fun attachToRegistry(owner: SavedStateRegistryOwner) {
        val registry = owner.savedStateRegistry
        registry.registerSavedStateProvider(backStackStateKey, this)

        val savedBundle = registry.consumeRestoredStateForKey(backStackStateKey)
        if (savedBundle != null) {
            val jsonList = savedBundle.getStringArrayList(backStackStateKey)
            if (!jsonList.isNullOrEmpty()) {
                backStack.clear()
                val restoredStack = jsonList.map { Json.decodeFromString<Screen>(it) }
                backStack.addAll(restoredStack)
            }
        }
    }

    override fun saveState(): Bundle =
        Bundle().apply {
            val jsonList = ArrayList(backStack.map { Json.encodeToString(it) })
            putStringArrayList(backStackStateKey, jsonList)
        }

}
