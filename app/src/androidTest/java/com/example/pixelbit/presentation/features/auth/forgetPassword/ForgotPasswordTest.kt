package com.example.pixelbit.presentation.features.auth.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ForgotPasswordTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun forgotPassword_linkIsDisplayed() {
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

        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
    }

    @Test
    fun forgotPassword_clickTriggersCallback() {
        var isClicked = false

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
                onForgotPasswordClick = { isClicked = true }, // Action to test
                onSignUpClick = {}
            )
        }

        composeTestRule.onNodeWithText("Forgot Password?").performClick()

        assert(isClicked)
    }
}
