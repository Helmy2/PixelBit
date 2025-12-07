package com.example.pixelbit.presentation.features.auth.signup

import app.cash.turbine.test
import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.model.User
import com.example.pixelbit.domain.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var authRepository: AuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        viewModel = SignUpViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given valid name, when onNameChange, then state is updated`() = runTest {
        // Given
        val name = "Ibrahim Mohamed"

        // When
        viewModel.onNameChange(name)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.name).isEqualTo(name)
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `given invalid email, when signUp, then error message is shown`() = runTest {
        // Given
        viewModel.onNameChange("Ibrahim Mohamed")
        viewModel.onEmailChange("invalid-email")
        viewModel.onPasswordChange("116633a")
        viewModel.onConfirmPasswordChange("116633a")
        viewModel.onAgreeToTermsChange(true)

        // When
        viewModel.signUp()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isEqualTo("Please enter a valid email address")
        }
    }

    @Test
    fun `given passwords do not match, when signUp, then error message is shown`() = runTest {
        // Given
        viewModel.onNameChange("Ibrahim Mohamed")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("116633a")
        viewModel.onConfirmPasswordChange("112233x")
        viewModel.onAgreeToTermsChange(true)

        // When
        viewModel.signUp()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isEqualTo("Passwords do not match")
        }
    }

    @Test
    fun `given terms not agreed, when signUp, then error message is shown`() = runTest {
        // Given
        viewModel.onNameChange("Ibrahim Mohamed")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")
        viewModel.onAgreeToTermsChange(false)

        // When
        viewModel.signUp()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isEqualTo("Please agree to the Terms and Conditions")
        }
    }

    @Test
    fun `given valid inputs, when signUp, then emits loading and success`() = runTest {
        // Given
        val name = "Ibrahim Mohamed"
        val email = "test@example.com"
        val password = "password123"
        viewModel.onNameChange(name)
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.onConfirmPasswordChange(password)
        viewModel.onAgreeToTermsChange(true)

        whenever(authRepository.signUp(name, email, "", password)).thenReturn(flowOf(AuthResult.Loading, AuthResult.Success(User())))

        // When
        viewModel.signUp()

        // Then
        viewModel.state.test {
            var state = awaitItem()
            if (!state.isLoading) state = awaitItem() // Handle initial state
            assertThat(state.isLoading).isTrue()

            state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isSuccess).isTrue()
        }
    }
}
