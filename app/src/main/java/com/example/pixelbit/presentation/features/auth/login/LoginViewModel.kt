package com.example.pixelbit.presentation.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun onEmailChange(newValue: String) {
        _email.value = newValue
        _errorMessage.value = null
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
        _errorMessage.value = null
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun login(onSuccess: () -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(_email.value, _password.value)
            _isLoading.value = false

            result.onSuccess {
                
                onSuccess()
            }.onFailure { error ->
                _errorMessage.value = "Wrong e-mail or password"
            }
        }
    }
}