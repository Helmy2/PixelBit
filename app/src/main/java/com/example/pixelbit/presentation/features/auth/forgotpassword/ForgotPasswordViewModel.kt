package com.example.pixelbit.presentation.features.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun onEmailChange(newValue: String) {
        _email.value = newValue
        _errorMessage.value = null
    }

    fun sendResetLink() {
        if (_email.value.isBlank()) {
            _errorMessage.value = "Please enter your email address"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(_email.value).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _isLoading.value = true
                        _errorMessage.value = null
                    }
                    is AuthResult.Success -> {
                        _isLoading.value = false
                        _isSuccess.value = true
                    }
                    is AuthResult.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = result.message
                    }
                }
            }
        }
    }

    fun resetState() {
        _isSuccess.value = false
        _errorMessage.value = null
    }
}
