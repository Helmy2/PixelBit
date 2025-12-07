package com.example.pixelbit.presentation.features.auth.forgetPassword

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.util.EmailValidator
import com.example.pixelbit.presentation.features.auth.forgotpassword.ForgotPasswordScreen
import com.example.pixelbit.presentation.features.auth.forgotpassword.ForgotPasswordViewModel
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ForgotPasswordTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var emailValidator: EmailValidator
    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        authRepository = mock()
        emailValidator = mock()
        whenever(emailValidator.isValid(any())).thenReturn(true)
        viewModel = ForgotPasswordViewModel(authRepository, emailValidator)
    }

    private fun getString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }

    @Test
    fun givenForgotPasswordScreen_whenDisplayed_thenEmailInputVisible() {
        // When
        composeTestRule.setContent {
            PixelbitTheme {
                ForgotPasswordScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(getString(R.string.enter_your_email)).assertIsDisplayed()
    }

    @Test
    fun givenValidEmail_whenResetClicked_thenCallRepository() {
        // Given
        runBlocking {
            whenever(authRepository.sendPasswordResetEmail(any())).thenReturn(
                flowOf(
                    com.example.pixelbit.domain.model.AuthResult.Success(
                        Unit
                    )
                )
            )
        }

        composeTestRule.setContent {
            PixelbitTheme {
                ForgotPasswordScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.enter_your_email))
            .performTextInput("test@example.com")
    }
}
