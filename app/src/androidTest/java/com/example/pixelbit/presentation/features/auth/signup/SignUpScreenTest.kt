package com.example.pixelbit.presentation.features.auth.signup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.model.User
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        authRepository = mock()
        viewModel = SignUpViewModel(authRepository)
    }

    private fun getString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }

    @Test
    fun givenSignUpScreen_whenDisplayed_thenAllFieldsAreVisible() {
        // Given
        composeTestRule.setContent {
            PixelbitTheme {
                SignUpScreen(
                    onSignUpSuccess = {},
                    onNavigateToSignIn = {},
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(getString(R.string.create_account)).assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.sign_up_to_get_started)).assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.full_name)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.email)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.phone_number)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.password)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.confirm_password)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.i_agree_to_terms)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.sign_up)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun givenValidInput_whenSignUpClicked_thenSignUpIsTriggered() {
        // Given
        whenever(authRepository.signUp(any(), any(), any(), any())).thenReturn(flowOf(AuthResult.Success(User())))

        composeTestRule.setContent {
            PixelbitTheme {
                SignUpScreen(
                    onSignUpSuccess = {},
                    onNavigateToSignIn = {},
                    viewModel = viewModel
                )
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.enter_your_full_name)).performScrollTo().performTextInput("Ibrahim Mohamed")
        composeTestRule.onNodeWithText(getString(R.string.enter_your_email)).performScrollTo().performTextInput("Ibrahim@gmail.com")
        composeTestRule.onNodeWithText(getString(R.string.enter_your_phone_number)).performScrollTo().performTextInput("01102251486")
        composeTestRule.onNodeWithText(getString(R.string.enter_your_password)).performScrollTo().performTextInput("116633a")
        composeTestRule.onNodeWithText(getString(R.string.re_enter_your_password)).performScrollTo().performTextInput("116633a")

        composeTestRule.onNode(isToggleable()).performScrollTo().performClick()
        
        composeTestRule.onNodeWithText(getString(R.string.sign_up)).performScrollTo().performClick()

        // Then
        composeTestRule.onNodeWithText(getString(R.string.enter_valid_email)).assertDoesNotExist()
    }

    @Test
    fun givenInvalidEmail_whenSignUpClicked_thenErrorIsDisplayed() {
        // Given
        composeTestRule.setContent {
            PixelbitTheme {
                SignUpScreen(
                    onSignUpSuccess = {},
                    onNavigateToSignIn = {},
                    viewModel = viewModel
                )
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.enter_your_full_name)).performScrollTo().performTextInput("Ibrahim Mohamed")
        composeTestRule.onNodeWithText(getString(R.string.enter_your_email)).performScrollTo().performTextInput("invalid-email")
        composeTestRule.onNodeWithText(getString(R.string.enter_your_password)).performScrollTo().performTextInput("116633a")
        composeTestRule.onNodeWithText(getString(R.string.re_enter_your_password)).performScrollTo().performTextInput("116633a")

        composeTestRule.onNode(isToggleable()).performScrollTo().performClick()
        
        composeTestRule.onNodeWithText(getString(R.string.sign_up)).performScrollTo().performClick()

        // Then
        composeTestRule.onNodeWithText(getString(R.string.enter_valid_email)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun givenPasswordsDoNotMatch_whenSignUpClicked_thenErrorIsDisplayed() {
        // Given
        composeTestRule.setContent {
            PixelbitTheme {
                SignUpScreen(
                    onSignUpSuccess = {},
                    onNavigateToSignIn = {},
                    viewModel = viewModel
                )
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.enter_your_full_name)).performScrollTo().performTextInput("Ibrahim Mohamed")
        composeTestRule.onNodeWithText(getString(R.string.enter_your_email)).performScrollTo().performTextInput("Ibrahim@gmail.com")
        composeTestRule.onNodeWithText(getString(R.string.enter_your_password)).performScrollTo().performTextInput("116633a")
        composeTestRule.onNodeWithText(getString(R.string.re_enter_your_password)).performScrollTo().performTextInput("112233x")
        

        composeTestRule.onNode(isToggleable()).performScrollTo().performClick()
        
        composeTestRule.onNodeWithText(getString(R.string.sign_up)).performScrollTo().performClick()


        composeTestRule.onAllNodesWithText(getString(R.string.passwords_do_not_match)).onFirst().performScrollTo().assertIsDisplayed()
    }
}
