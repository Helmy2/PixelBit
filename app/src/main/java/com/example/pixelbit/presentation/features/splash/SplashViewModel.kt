package com.example.pixelbit.presentation.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            try {
                val isCompleted = onboardingRepository.isOnboardingCompleted()
                _uiState.value = SplashUiState(
                    isOnboardingCompleted = isCompleted,
                    isLoading = false
                )
            } catch (e: Exception) {
                // If there's an error reading preferences, treat as not completed
                _uiState.value = SplashUiState(
                    isOnboardingCompleted = false,
                    isLoading = false
                )
            }
        }
    }
}

data class SplashUiState(
    val isOnboardingCompleted: Boolean = false,
    val isLoading: Boolean = true
)