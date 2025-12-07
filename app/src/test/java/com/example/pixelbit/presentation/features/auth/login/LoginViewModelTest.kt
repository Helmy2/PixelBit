package com.example.pixelbit.presentation.features.auth.login

import app.cash.turbine.test
import com.example.pixelbit.domain.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var authRepository: AuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login failure with empty fields sets error message`() = runTest {

        viewModel.login(onSuccess = {})

        viewModel.errorMessage.test {
            val error = awaitItem()
            assertThat(error).isEqualTo("Please fill in all fields")
        }
    }

    @Test
    fun `login success calls onSuccess callback`() = runTest {
        val email = "valid@mail.com"
        val pass = "password123"
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(pass)

        whenever(authRepository.login(email, pass)).thenReturn(Result.success(Unit))

        var isSuccessCalled = false
        viewModel.login(onSuccess = { isSuccessCalled = true })

        advanceUntilIdle()

        assertThat(isSuccessCalled).isTrue()

        viewModel.isLoading.test {
            assertThat(awaitItem()).isFalse()
        }
        viewModel.errorMessage.test {
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `login failure sets error message while pass or email are wrong`() = runTest {
        val email = "valid@mail.com"
        val pass = "wrongpass"
        val repoError = "Invalid credentials"

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(pass)

        whenever(authRepository.login(email, pass))
            .thenReturn(Result.failure(Exception(repoError)))

        viewModel.login(onSuccess = {})
        advanceUntilIdle()

        viewModel.errorMessage.test {
            val error = awaitItem()
            assertThat(error).isEqualTo(repoError)
        }
    }
}
