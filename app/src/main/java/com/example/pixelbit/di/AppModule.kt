package com.example.pixelbit.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.pixelbit.data.repository.AddressRepositoryImpl
import com.example.pixelbit.data.repository.AuthRepositoryImpl
import com.example.pixelbit.data.repository.CartRepositoryImpl
import com.example.pixelbit.data.repository.CheckoutRepositoryImpl
import com.example.pixelbit.data.repository.FavoritesRepositoryImpl
import com.example.pixelbit.data.repository.OnboardingRepositoryImpl
import com.example.pixelbit.data.repository.OrderRepositoryImpl
import com.example.pixelbit.data.repository.ShopRepositoryImpl
import com.example.pixelbit.domain.repository.AddressRepository
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.CartRepository
import com.example.pixelbit.domain.repository.CheckoutRepository
import com.example.pixelbit.domain.repository.FavoritesRepository
import com.example.pixelbit.domain.repository.OnboardingRepository
import com.example.pixelbit.domain.repository.OrderRepository
import com.example.pixelbit.domain.repository.ShopRepository
import com.example.pixelbit.domain.util.EmailValidator
import com.example.pixelbit.domain.util.EmailValidatorImpl
import com.example.pixelbit.presentation.features.address.AddressViewModel
import com.example.pixelbit.presentation.features.auth.forgotpassword.ForgotPasswordViewModel
import com.example.pixelbit.presentation.features.auth.login.LoginViewModel
import com.example.pixelbit.presentation.features.auth.signup.SignUpViewModel
import com.example.pixelbit.presentation.features.auth.verification.VerificationViewModel
import com.example.pixelbit.presentation.features.cart.CartViewModel
import com.example.pixelbit.presentation.features.category.CategoryDetailsViewModel
import com.example.pixelbit.presentation.features.checkout.CheckoutViewModel
import com.example.pixelbit.presentation.features.favorites.FavoritesViewModel
import com.example.pixelbit.presentation.features.home.HomeViewModel
import com.example.pixelbit.presentation.features.myorders.MyOrdersViewModel
import com.example.pixelbit.presentation.features.onboarding.OnboardingViewModel
import com.example.pixelbit.presentation.features.profile.ProfileViewModel
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Main Koin module list for the application.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pixelbit_preferences")
val appModule = module {
    singleOf(::AppNavigator)
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single<DataStore<Preferences>> { androidContext().dataStore }

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::OnboardingRepositoryImpl).bind<OnboardingRepository>()
    singleOf(::FavoritesRepositoryImpl).bind<FavoritesRepository>()
    singleOf(::ShopRepositoryImpl).bind<ShopRepository>()
    singleOf(::CartRepositoryImpl).bind<CartRepository>()
    singleOf(::CheckoutRepositoryImpl).bind<CheckoutRepository>()
    singleOf(::OrderRepositoryImpl).bind<OrderRepository>()
    singleOf(::AddressRepositoryImpl).bind<AddressRepository>()

    factoryOf(::EmailValidatorImpl).bind<EmailValidator>()

    viewModelOf(::SignUpViewModel)
    viewModelOf(::VerificationViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::MyOrdersViewModel)
    viewModelOf(::AddressViewModel)
    viewModelOf(::CategoryDetailsViewModel)
}
