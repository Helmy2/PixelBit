package com.example.pixelbit.presentation.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(false)


    init {
        loadOnboardingData()
        checkOnboardingStatus()
    }

    private fun loadOnboardingData() {
        viewModelScope.launch {
            val items = onboardingRepository.getOnboardingItems()
            _uiState.update { it.copy(onboardingItems = items, isLoading = false) }
        }
    }

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.PageChanged -> {
                _uiState.update { it.copy(currentPage = event.page) }
            }
            OnboardingEvent.Skip -> {
                completeOnboarding()
            }
            OnboardingEvent.Next -> {
                val nextPage = _uiState.value.currentPage + 1
                if (nextPage < _uiState.value.onboardingItems.size) {
                    _uiState.update { it.copy(currentPage = nextPage) }
                } else {
                    completeOnboarding()
                }
            }
            OnboardingEvent.GetStarted -> {
                completeOnboarding()
            }
        }
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            val completed = onboardingRepository.isOnboardingCompleted()
            _isOnboardingCompleted.value = completed
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            onboardingRepository.setOnboardingCompleted()
            _isOnboardingCompleted.value = true
        }
    }
}