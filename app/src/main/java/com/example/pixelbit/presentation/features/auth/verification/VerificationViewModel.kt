package com.example.pixelbit.presentation.features.auth.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VerificationState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVerified: Boolean = false,
    val canResend: Boolean = false,
    val resendCountdown: Int = 60,
    val verificationEmailSent: Boolean = false,
    val isDeleting: Boolean = false,
    val deletionComplete: Boolean = false
)

class VerificationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VerificationState())
    val state: StateFlow<VerificationState> = _state.asStateFlow()

    private var hasInitialized = false

    fun initialize() {
        if (!hasInitialized) {
            hasInitialized = true
            sendVerificationEmail()
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch {
            authRepository.sendEmailVerification().collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
                    }
                    is AuthResult.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            verificationEmailSent = true,
                            canResend = false,
                            errorMessage = null
                        )
                        startResendCountdown()
                    }
                    is AuthResult.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun checkVerification() {
        viewModelScope.launch {
            authRepository.checkEmailVerification().collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
                    }
                    is AuthResult.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isVerified = result.data,
                            errorMessage = if (!result.data) "Email not verified yet. Please check your inbox." else null
                        )
                    }
                    is AuthResult.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun startResendCountdown() {
        viewModelScope.launch {
            for (i in 60 downTo 0) {
                _state.value = _state.value.copy(resendCountdown = i)
                delay(1000)
            }
            _state.value = _state.value.copy(canResend = true)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun deleteUserAndSignOut() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDeleting = true)
            authRepository.deleteCurrentUser().collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                    }
                    is AuthResult.Success -> {
                        _state.value = _state.value.copy(
                            isDeleting = false,
                            deletionComplete = true
                        )
                    }
                    is AuthResult.Error -> {
                        authRepository.signOut()
                        _state.value = _state.value.copy(
                            isDeleting = false,
                            deletionComplete = true
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}

