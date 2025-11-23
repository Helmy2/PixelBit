package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.OnboardingItem

interface OnboardingRepository {
    suspend fun getOnboardingItems(): List<OnboardingItem>
    suspend fun setOnboardingCompleted()
    suspend fun isOnboardingCompleted(): Boolean
}