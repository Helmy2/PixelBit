package com.example.pixelbit.ui.login

import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.presentation.features.auth.login.LoginViewModel
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
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        assertThat(viewModel.email.value).isEmpty()
        assertThat(viewModel.password.value).isEmpty()
        assertThat(viewModel.isPasswordVisible.value).isFalse()
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun `onEmailChange updates email state`() {
        viewModel.onEmailChange("test@mail.com")
        assertThat(viewModel.email.value).isEqualTo("test@mail.com")
    }

    @Test
    fun `onPasswordChange updates password state`() {
        viewModel.onPasswordChange("secret123")
        assertThat(viewModel.password.value).isEqualTo("secret123")
    }

    @Test
    fun `togglePasswordVisibility toggles state`() {
        assertThat(viewModel.isPasswordVisible.value).isFalse()

        viewModel.togglePasswordVisibility()
        assertThat(viewModel.isPasswordVisible.value).isTrue()

        viewModel.togglePasswordVisibility()
        assertThat(viewModel.isPasswordVisible.value).isFalse()
    }

    @Test
    fun `login failure with empty fields sets error message`() = runTest {
        var callbackCalled = false

        viewModel.login(onSuccess = { callbackCalled = true })
        advanceUntilIdle()

        assertThat(callbackCalled).isFalse()
        assertThat(viewModel.errorMessage.value).isEqualTo("Please fill in all fields")
    }

    @Test
    fun `login success calls onSuccess callback`() = runTest {
        val email = "valid@mail.com"
        val pass = "validPass"
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(pass)

        whenever(authRepository.login(email, pass)).thenReturn(Result.success(Unit))

        var callbackCalled = false
        viewModel.login(onSuccess = { callbackCalled = true })

        advanceUntilIdle()

        assertThat(callbackCalled).isTrue()
        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.errorMessage.value).isNull()
    }

    @Test
    fun `login failure sets error message from repository`() = runTest {
        val email = "valid@mail.com"
        val pass = "wrongPass"
        val errorMsg = "Invalid credentials"

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(pass)

        whenever(authRepository.login(email, pass))
            .thenReturn(Result.failure(Exception(errorMsg)))

        var callbackCalled = false
        viewModel.login(onSuccess = { callbackCalled = true })
        advanceUntilIdle()

        assertThat(callbackCalled).isFalse()
        assertThat(viewModel.errorMessage.value).isEqualTo(errorMsg)
        assertThat(viewModel.isLoading.value).isFalse()
    }
}
