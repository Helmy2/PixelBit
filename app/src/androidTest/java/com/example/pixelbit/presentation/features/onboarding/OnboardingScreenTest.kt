package com.example.pixelbit.presentation.features.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.OnboardingItem
import com.example.pixelbit.domain.repository.OnboardingRepository
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var onboardingRepository: OnboardingRepository
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        onboardingRepository = mock()
    }

    private fun getString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun givenOnboardingScreen_whenDisplayed_thenFirstItemIsVisible() {
        // Given
        val items = listOf(
            OnboardingItem(id = 1, title = "Title 1", description = "Description 1", imageRes = R.drawable.ic_launcher_foreground),
            OnboardingItem(id = 2, title = "Title 2", description = "Description 2", imageRes = R.drawable.ic_launcher_foreground)
        )
        runBlocking {
            whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)
            whenever(onboardingRepository.isOnboardingCompleted()).thenReturn(false)
        }
        viewModel = OnboardingViewModel(onboardingRepository)

        // When
        composeTestRule.setContent {
            PixelbitTheme {

                val uiState by viewModel.uiState.collectAsState()
                OnboardingContent(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToSignUp = {},
                    onNavigateToSignIn = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Title 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description 1").assertIsDisplayed()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun givenMultipleItems_whenNextClicked_thenSecondItemIsDisplayed() {
        // Given
        val items = listOf(
            OnboardingItem(id = 1, title = "Title 1", description = "Description 1", imageRes = R.drawable.ic_launcher_foreground),
            OnboardingItem(id = 2, title = "Title 2", description = "Description 2", imageRes = R.drawable.ic_launcher_foreground)
        )
        runBlocking {
            whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)
            whenever(onboardingRepository.isOnboardingCompleted()).thenReturn(false)
        }
        viewModel = OnboardingViewModel(onboardingRepository)

        composeTestRule.setContent {
            PixelbitTheme {
                val uiState by viewModel.uiState.collectAsState()
                OnboardingContent(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToSignUp = {},
                    onNavigateToSignIn = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Next").performClick()

        // Then
        composeTestRule.onNodeWithText("Title 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description 2").assertIsDisplayed()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun givenLastItem_whenGetStartedClicked_thenCompleteOnboarding() {
        // Given
        val items = listOf(
            OnboardingItem(id = 1, title = "Title 1", description = "Description 1", imageRes = R.drawable.ic_launcher_foreground)
        )
        runBlocking {
            whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)
            whenever(onboardingRepository.isOnboardingCompleted()).thenReturn(false)
        }
        viewModel = OnboardingViewModel(onboardingRepository)
        
        var navigateToSignUpCalled = false

        composeTestRule.setContent {
            PixelbitTheme {
                val uiState by viewModel.uiState.collectAsState()
                OnboardingContent(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToSignUp = { navigateToSignUpCalled = true },
                    onNavigateToSignIn = {}
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Create Account").performClick()

        // Then
        runBlocking {
            verify(onboardingRepository).setOnboardingCompleted()
        }
        assert(navigateToSignUpCalled)
    }
}
