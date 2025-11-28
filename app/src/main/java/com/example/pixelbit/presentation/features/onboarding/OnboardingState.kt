package com.example.pixelbit.presentation.features.onboarding
import com.example.pixelbit.domain.model.OnboardingItem

data class OnboardingUiState(
    val onboardingItems: List<OnboardingItem> = emptyList(),
    val currentPage: Int = 0,
    val isLoading: Boolean = true
)

sealed class OnboardingEvent {
    data class PageChanged(val page: Int) : OnboardingEvent()
    object Skip : OnboardingEvent()
    object Next : OnboardingEvent()
    object GetStarted : OnboardingEvent()
}