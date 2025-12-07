package com.example.pixelbit.presentation.features.auth.verification

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class VerificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenVerificationScreen_whenLoaded_thenEmailIsDisplayed() {
        // Given
        val email = "Ibrahim@gmail.com"
        val state = VerificationState()

        // When
        composeTestRule.setContent {
            VerificationScreenContent(
                email = email,
                state = state,
                onCheckVerification = {},
                onResendEmail = {},
                onBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(email).assertIsDisplayed()
        composeTestRule.onNodeWithText("Verify Your Email").assertIsDisplayed()
    }

    @Test
    fun givenVerificationScreen_whenCheckVerifiedClicked_thenCallbackInvoked() {
        // Given
        var checkClicked = false
        val state = VerificationState()

        // When
        composeTestRule.setContent {
            VerificationScreenContent(
                email = "Ibrahim@gmail.com",
                state = state,
                onCheckVerification = { checkClicked = true },
                onResendEmail = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("I've Verified My Email").performClick()

        // Then
        assert(checkClicked)
    }

    @Test
    fun givenVerificationScreen_whenResendClicked_thenCallbackInvoked() {
        // Given
        var resendClicked = false
        val state = VerificationState(canResend = true)

        // When
        composeTestRule.setContent {
            VerificationScreenContent(
                email = "Ibrahim@gmail.com",
                state = state,
                onCheckVerification = {},
                onResendEmail = { resendClicked = true },
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("Resend Verification Email").performClick()

        // Then
        assert(resendClicked)
    }

    @Test
    fun givenErrorMessage_whenDisplayed_thenErrorIsVisible() {
        // Given
        val errorMessage = "Error Occurred"
        val state = VerificationState(errorMessage = errorMessage)

        // When
        composeTestRule.setContent {
            VerificationScreenContent(
                email = "Ibrahim@gmail.com",
                state = state,
                onCheckVerification = {},
                onResendEmail = {},
                onBack = {}
            )
        }

        // Thn
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
