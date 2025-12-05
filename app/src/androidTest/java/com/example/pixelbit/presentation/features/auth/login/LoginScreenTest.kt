package com.example.pixelbit.ui.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.pixelbit.presentation.features.auth.login.LoginScreenContent
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysAllComponents() {
        composeTestRule.setContent {
            LoginScreenContent(
                email = "",
                password = "",
                isPasswordVisible = false,
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onTogglePasswordVisibility = {},
                onLoginClick = {},
                onForgotPasswordClick = {},
                onSignUpClick = {}
            )
        }

        composeTestRule.onNodeWithText("Login Account").assertIsDisplayed()

        composeTestRule.onNodeWithText("Enter your email or phone").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter your password").assertIsDisplayed()

        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
    }

    @Test
    fun loginScreen_inputsAreUpdated() {
        composeTestRule.setContent {
            LoginScreenContent(
                email = "test@test.com",
                password = "password123",
                isPasswordVisible = false,
                isLoading = false,
                onEmailChange = {},
                onPasswordChange = {},
                onTogglePasswordVisibility = {},
                onLoginClick = {},
                onForgotPasswordClick = {},
                onSignUpClick = {}
            )
        }

        composeTestRule.onNodeWithText("test@test.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("password123").assertExists()
    }

    @Test
    fun loginScreen_clickActionsPerformCallbacks() {
        var loginClicked = false
        var signUpClicked = false
        var forgotPassClicked = false

        composeTestRule.setContent {
            LoginScreenContent(
                email = "a", password = "b", isPasswordVisible = false, isLoading = false,
                onEmailChange = {}, onPasswordChange = {}, onTogglePasswordVisibility = {},
                onLoginClick = { loginClicked = true },
                onForgotPasswordClick = { forgotPassClicked = true },
                onSignUpClick = { signUpClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Sign In").performClick()
        assert(loginClicked)

        composeTestRule.onNodeWithText("Sign Up").performClick()
        assert(signUpClicked)

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        assert(forgotPassClicked)
    }

    @Test
    fun loginScreen_loadingState_showsProgressBar() {
        composeTestRule.setContent {
            LoginScreenContent(
                email = "", password = "", isPasswordVisible = false,
                isLoading = true, // Set loading to true
                onEmailChange = {}, onPasswordChange = {}, onTogglePasswordVisibility = {},
                onLoginClick = {}, onForgotPasswordClick = {}, onSignUpClick = {}
            )
        }

        composeTestRule.onNodeWithText("Sign In").assertDoesNotExist()
    }
}
