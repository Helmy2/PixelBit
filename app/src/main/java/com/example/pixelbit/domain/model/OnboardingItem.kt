package com.example.pixelbit.domain.model

data class OnboardingItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageRes: Int,
    val isLastItem: Boolean = false
)