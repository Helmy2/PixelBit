package com.example.pixelbit.presentation.features.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.User
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.OnboardingRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var onboardingRepository: OnboardingRepository
    private lateinit var appNavigator: AppNavigator
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        authRepository = mock()
        onboardingRepository = mock()
        appNavigator = AppNavigator(onboardingRepository, authRepository)
    }

    private fun getString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }

    @Test
    fun givenUserProfile_whenScreenDisplayed_thenUserInfoIsVisible() {
        // Given
        val user = User(
            uid = "1",
            name = "Ibrahim Mohamed",
            email = "Ibrahim@gmail.com",
            phone = "01102251486",
            isEmailVerified = true
        )
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(user)))
        viewModel = ProfileViewModel(authRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                ProfileScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Ibrahim Mohamed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ibrahim@gmail.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("01102251486").assertIsDisplayed()
    }

    @Test
    fun givenErrorLoadingProfile_whenScreenDisplayed_thenErrorMessageIsVisible() {
        // Given
        val errorMessage = "Network Error"
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.failure(Exception(errorMessage))))
        viewModel = ProfileViewModel(authRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                ProfileScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun givenProfileScreen_whenLogoutClicked_thenSignOutTriggered() {
         // Given
        val user = User(uid = "1", name = "Ibrahim Mohamed", email = "Ibrahim@gmail.com")
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(user)))
        viewModel = ProfileViewModel(authRepository, appNavigator)

        composeTestRule.setContent {
            PixelbitTheme {
                ProfileScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.logout)).performClick()
    }
}
