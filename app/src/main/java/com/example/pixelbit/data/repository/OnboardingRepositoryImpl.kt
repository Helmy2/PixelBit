package com.example.pixelbit.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.pixelbit.domain.model.OnboardingItem
import com.example.pixelbit.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.util.Log

class OnboardingRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : OnboardingRepository {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private const val TAG = "OnboardingRepository"
    }

    override suspend fun getOnboardingItems(): List<OnboardingItem> {
        return listOf(
            OnboardingItem(
                id = 1,
                title = "Discover Amazing Products",
                description = "Browse through thousands of high-quality products",
                imageRes = com.example.pixelbit.R.drawable.onboarding_1
            ),
            OnboardingItem(
                id = 2,
                title = "Easy & Secure Payment",
                description = "Multiple payment options with complete security",
                imageRes = com.example.pixelbit.R.drawable.onboarding_2
            ),
            OnboardingItem(
                id = 3,
                title = "Fast Delivery",
                description = "Get your orders delivered quickly to your doorstep",
                imageRes = com.example.pixelbit.R.drawable.onboarding_3,
                isLastItem = true
            )
        )
    }

    override suspend fun setOnboardingCompleted() {
        Log.d(TAG, "Setting onboarding completed to TRUE")
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }

    override suspend fun isOnboardingCompleted(): Boolean {
        val completed = dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }.first()
        Log.d(TAG, "Checking onboarding completed: $completed")
        return completed
    }
}