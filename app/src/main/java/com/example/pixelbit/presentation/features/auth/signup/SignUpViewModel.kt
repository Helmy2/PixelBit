package com.example.pixelbit.presentation.features.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val agreeToTerms: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        val filtered = name.filter { it.isLetter() || it.isWhitespace() }.take(50)
        _state.value = _state.value.copy(name = filtered, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        val trimmed = email.trim().lowercase()
        _state.value = _state.value.copy(email = trimmed, errorMessage = null)
    }

    fun onPhoneChange(phone: String) {
        val filtered = phone.take(20)
        _state.value = _state.value.copy(phone = filtered, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun onAgreeToTermsChange(agree: Boolean) {
        _state.value = _state.value.copy(agreeToTerms = agree, errorMessage = null)
    }

    fun signUp() {
        val currentState = _state.value

        val validationError = validateInput(currentState)
        if (validationError != null) {
            _state.value = currentState.copy(errorMessage = validationError)
            return
        }

        viewModelScope.launch {
            authRepository.signUp(
                name = currentState.name.trim(),
                email = currentState.email.trim(),
                phone = currentState.phone.trim(),
                password = currentState.password
            ).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.value = currentState.copy(isLoading = true, errorMessage = null)
                    }
                    is AuthResult.Success -> {
                        _state.value = currentState.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    }
                    is AuthResult.Error -> {
                        _state.value = currentState.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    private fun validateInput(state: SignUpState): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
        return when {
            state.name.isBlank() || state.name.length < 2 ->
                "Please enter your full name (at least 2 characters)"

            !state.name.contains(" ") ->
                "Please enter your full name (first and last name)"

            state.email.isBlank() ->
                "Please enter your email address"

            !emailRegex.matches(state.email) ->
                "Please enter a valid email address"


            state.password.isEmpty() ->
                "Please enter a password"

            state.password.length < 6 ->
                "Password must be at least 6 characters long"

            state.password.length > 50 ->
                "Password is too long (max 50 characters)"

            !state.password.any { it.isDigit() } ->
                "Password must contain at least one number"

            !state.password.any { it.isLetter() } ->
                "Password must contain at least one letter"

            state.confirmPassword.isEmpty() ->
                "Please confirm your password"

            state.password != state.confirmPassword ->
                "Passwords do not match"

            !state.agreeToTerms ->
                "Please agree to the Terms and Conditions"

            else -> null
        }
    }

    fun resetState() {
        _state.value = SignUpState()
    }
}
