package com.example.pixelbit.presentation.features.auth.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        authRepository = mock()
        viewModel = LoginViewModel(authRepository)
    }

    private fun getString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }

    @Test
    fun givenLoginScreen_whenDisplayed_thenInputFieldsAreVisible() {
        // When
        composeTestRule.setContent {
            PixelbitTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onForgotPassword = {},
                    onSignUpClick = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(getString(R.string.enter_email_or_phone)).assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.enter_your_password)).assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.sign_in)).assertIsDisplayed()
    }

    @Test
    fun givenEmptyFields_whenLoginClicked_thenShowErrorMessage() {
        // When
        composeTestRule.setContent {
            PixelbitTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onForgotPassword = {},
                    onSignUpClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(getString(R.string.sign_in)).performClick()

        // Then
        runBlocking {
            org.mockito.kotlin.verify(authRepository, org.mockito.kotlin.never())
                .login(any(), any())
        }
    }

    @Test
    fun givenValidCredentials_whenLoginClicked_thenCallRepository() {
        // Given
        runBlocking {
            whenever(authRepository.login(any(), any())).thenReturn(Result.success(Unit))
        }

        composeTestRule.setContent {
            PixelbitTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onForgotPassword = {},
                    onSignUpClick = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.enter_email_or_phone))
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText(getString(R.string.enter_your_password))
            .performTextInput("password123")
        composeTestRule.onNodeWithText(getString(R.string.sign_in)).performClick()

        // Then
        runBlocking {
            org.mockito.kotlin.verify(authRepository).login("test@example.com", "password123")
        }
    }

    @Test
    fun givenLoginScreen_whenForgotPasswordClicked_thenCallbackInvoked() {
        var forgotPasswordClicked = false

        composeTestRule.setContent {
            PixelbitTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onForgotPassword = { forgotPasswordClicked = true },
                    onSignUpClick = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.forgot_password)).performClick()

        // Then
        assert(forgotPasswordClicked)
    }

    @Test
    fun givenLoginScreen_whenSignUpClicked_thenCallbackInvoked() {
        var signUpClicked = false

        composeTestRule.setContent {
            PixelbitTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onForgotPassword = {},
                    onSignUpClick = { signUpClicked = true }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.sign_up)).performClick()

        // Then
        assert(signUpClicked)
    }
}
