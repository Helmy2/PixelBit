package com.example.pixelbit.di

import com.example.pixelbit.data.repository.AuthRepositoryImpl
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.presentation.features.auth.login.LoginViewModel
import com.example.pixelbit.presentation.features.auth.signup.SignUpViewModel
import com.example.pixelbit.presentation.features.auth.verification.VerificationViewModel
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Main Koin module list for the application.
 */
val appModule = module {
    single {
        // Todo make the start destination base if the user is logged in or not
        AppNavigator(Screen.Home)
    }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    viewModelOf(::SignUpViewModel)
    viewModelOf(::VerificationViewModel)
    viewModelOf(::LoginViewModel)
}