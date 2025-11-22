package com.example.pixelbit.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.pixelbit.data.repository.AuthRepositoryImpl
import com.example.pixelbit.data.repository.OnboardingRepositoryImpl
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.OnboardingRepository
import com.example.pixelbit.presentation.features.auth.signup.SignUpViewModel
import com.example.pixelbit.presentation.features.auth.verification.VerificationViewModel
import com.example.pixelbit.presentation.features.onboarding.OnboardingViewModel
import com.example.pixelbit.presentation.features.splash.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Main Koin module list for the application.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pixelbit_preferences")
val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    viewModelOf(::SignUpViewModel)
    viewModelOf(::VerificationViewModel)

    single<DataStore<Preferences>> { androidContext().dataStore }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::SplashViewModel)
}