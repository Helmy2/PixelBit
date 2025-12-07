package com.example.pixelbit.presentation.features.auth.forgotpassword

import app.cash.turbine.test
import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.util.EmailValidator
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ForgotPasswordViewModelTest {

    private lateinit var viewModel: ForgotPasswordViewModel
    private val authRepository: AuthRepository = mock()
    private val emailValidator: EmailValidator = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel(authRepository, emailValidator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() {
        assertThat(viewModel.email.value).isEmpty()
        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.errorMessage.value).isNull()
        assertThat(viewModel.isSuccess.value).isFalse()
    }

    @Test
    fun `test onEmailChange updates email and clears error`() {
        viewModel.sendResetLink()
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.errorMessage.value).isNotNull()

        viewModel.onEmailChange("test@example.com")

        assertThat(viewModel.email.value).isEqualTo("test@example.com")
        assertThat(viewModel.errorMessage.value).isNull()
    }

    @Test
    fun `test sendResetLink with blank email shows error`() {
        viewModel.onEmailChange("")
        viewModel.sendResetLink()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.errorMessage.value).isEqualTo("Please enter your email address")
        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.isSuccess.value).isFalse()
    }

    @Test
    fun `test sendResetLink with invalid email shows error`() {
        val invalidEmail = "invalid-email"
        viewModel.onEmailChange(invalidEmail)
        whenever(emailValidator.isValid(invalidEmail)).thenReturn(false)

        viewModel.sendResetLink()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.errorMessage.value).isEqualTo("Please enter a valid email address")
        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.isSuccess.value).isFalse()
    }

    @Test
    fun `test sendResetLink success flow`() = runTest {
        val validEmail = "test@example.com"
        viewModel.onEmailChange(validEmail)
        whenever(emailValidator.isValid(validEmail)).thenReturn(true)

        val flow = flowOf(
            AuthResult.Loading,
            AuthResult.Success(Unit)
        )
        whenever(authRepository.sendPasswordResetEmail(validEmail)).thenReturn(flow)

        viewModel.sendResetLink()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.isSuccess.value).isTrue()
        assertThat(viewModel.errorMessage.value).isNull()
        verify(authRepository).sendPasswordResetEmail(validEmail)
    }

    @Test
    fun `test sendResetLink shows loading state`() = runTest {
        val validEmail = "test@example.com"
        viewModel.onEmailChange(validEmail)
        whenever(emailValidator.isValid(validEmail)).thenReturn(true)

        val flow = flowOf(AuthResult.Loading)
        whenever(authRepository.sendPasswordResetEmail(validEmail)).thenReturn(flow)

        viewModel.isLoading.test {
            assertThat(awaitItem()).isFalse()

            viewModel.sendResetLink()
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun `test sendResetLink failure shows error`() = runTest {
        val validEmail = "test@example.com"
        val errorMsg = "Network error occurred"
        viewModel.onEmailChange(validEmail)
        whenever(emailValidator.isValid(validEmail)).thenReturn(true)

        val flow = flowOf(
            AuthResult.Loading,
            AuthResult.Error(errorMsg)
        )
        whenever(authRepository.sendPasswordResetEmail(validEmail)).thenReturn(flow)

        viewModel.sendResetLink()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.isSuccess.value).isFalse()
        assertThat(viewModel.errorMessage.value).isEqualTo(errorMsg)
        verify(authRepository).sendPasswordResetEmail(validEmail)
    }

    @Test
    fun `test resetState clears success and error`() {
        val validEmail = "test@example.com"
        viewModel.onEmailChange(validEmail)
        whenever(emailValidator.isValid(validEmail)).thenReturn(true)

        val flow = flowOf(AuthResult.Success(Unit))
        whenever(authRepository.sendPasswordResetEmail(validEmail)).thenReturn(flow)

        viewModel.sendResetLink()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.isSuccess.value).isTrue()

        viewModel.resetState()

        assertThat(viewModel.isSuccess.value).isFalse()
        assertThat(viewModel.errorMessage.value).isNull()
    }
}
