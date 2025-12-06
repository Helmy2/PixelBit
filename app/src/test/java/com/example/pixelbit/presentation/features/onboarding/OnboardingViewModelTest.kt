package com.example.pixelbit.presentation.features.onboarding

import app.cash.turbine.test
import com.example.pixelbit.domain.model.OnboardingItem
import com.example.pixelbit.domain.repository.OnboardingRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel
    private val onboardingRepository: OnboardingRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val mockOnboardingItem1 = OnboardingItem(
        id = 1,
        title = "Welcome",
        description = "Welcome to our app",
        imageRes = 1,
        isLastItem = false
    )
    private val mockOnboardingItem2 = OnboardingItem(
        id = 2,
        title = "Features",
        description = "Explore amazing features",
        imageRes = 2,
        isLastItem = false
    )
    private val mockOnboardingItem3 = OnboardingItem(
        id = 3,
        title = "Get Started",
        description = "Let's begin your journey",
        imageRes = 3,
        isLastItem = true
    )

    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        // Set default mock behaviors
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(emptyList())
        whenever(onboardingRepository.isOnboardingCompleted()).thenReturn(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() {
        val initialState = OnboardingUiState()
        assertThat(initialState.currentPage).isEqualTo(0)
        assertThat(initialState.onboardingItems).isEmpty()
    }

    @Test
    fun `test loadOnboardingData loads items`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.onboardingItems).isEqualTo(items)
        assertThat(viewModel.uiState.value.onboardingItems).hasSize(3)
    }

    @Test
    fun `test loadOnboardingData preserves isLastItem flag`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val loadedItems = viewModel.uiState.value.onboardingItems
        assertThat(loadedItems[0].isLastItem).isFalse()
        assertThat(loadedItems[1].isLastItem).isFalse()
        assertThat(loadedItems[2].isLastItem).isTrue()
    }

    @Test
    fun `test checkOnboardingStatus when not completed`() = runTest {
        whenever(onboardingRepository.isOnboardingCompleted()).thenReturn(false)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).isOnboardingCompleted()
    }

    @Test
    fun `test checkOnboardingStatus when already completed`() = runTest {
        whenever(onboardingRepository.isOnboardingCompleted()).thenReturn(true)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).isOnboardingCompleted()
    }

    @Test
    fun `test PageChanged event updates current page`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(OnboardingEvent.PageChanged(1))

        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)
    }

    @Test
    fun `test PageChanged event with different pages`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(OnboardingEvent.PageChanged(2))
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(2)

        viewModel.onEvent(OnboardingEvent.PageChanged(0))
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)
    }

    @Test
    fun `test Skip event completes onboarding`() = runTest {
        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(OnboardingEvent.Skip)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).setOnboardingCompleted()
    }

    @Test
    fun `test Next event advances to next page`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)

        viewModel.onEvent(OnboardingEvent.Next)
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)

        viewModel.onEvent(OnboardingEvent.Next)
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(2)
    }

    @Test
    fun `test Next event on last page completes onboarding`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2.copy(isLastItem = true))
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Move to last page
        viewModel.onEvent(OnboardingEvent.PageChanged(1))

        // Next on last page should complete onboarding
        viewModel.onEvent(OnboardingEvent.Next)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).setOnboardingCompleted()
    }

    @Test
    fun `test GetStarted event completes onboarding`() = runTest {
        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(OnboardingEvent.GetStarted)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).setOnboardingCompleted()
    }

    @Test
    fun `test multiple Next events navigate through all pages`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertThat(awaitItem().currentPage).isEqualTo(0)

            viewModel.onEvent(OnboardingEvent.Next)
            assertThat(awaitItem().currentPage).isEqualTo(1)

            viewModel.onEvent(OnboardingEvent.Next)
            assertThat(awaitItem().currentPage).isEqualTo(2)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test currentPage does not exceed items size`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2.copy(isLastItem = true))
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Navigate to last page
        viewModel.onEvent(OnboardingEvent.Next)
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)

        // Try to go beyond - should complete onboarding instead
        viewModel.onEvent(OnboardingEvent.Next)
        testDispatcher.scheduler.advanceUntilIdle()

        // Page should still be at last index, not exceeded
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)
        verify(onboardingRepository).setOnboardingCompleted()
    }

    @Test
    fun `test onboarding items are loaded on initialization`() = runTest {
        val items = listOf(mockOnboardingItem1)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).getOnboardingItems()
    }

    @Test
    fun `test onboarding status is checked on initialization`() = runTest {
        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).isOnboardingCompleted()
    }

    @Test
    fun `test state updates are reflected in flow`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.currentPage).isEqualTo(0)

            viewModel.onEvent(OnboardingEvent.PageChanged(1))
            val updatedState = awaitItem()
            assertThat(updatedState.currentPage).isEqualTo(1)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test navigation through all pages sequentially`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)

        viewModel.onEvent(OnboardingEvent.Next)
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(1)

        viewModel.onEvent(OnboardingEvent.Next)
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(2)

        // Next on last page should complete
        viewModel.onEvent(OnboardingEvent.Next)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).setOnboardingCompleted()
    }

    @Test
    fun `test empty onboarding items list`() = runTest {
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(emptyList())

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.onboardingItems).isEmpty()
        assertThat(viewModel.uiState.value.currentPage).isEqualTo(0)
    }

    @Test
    fun `test Skip from any page completes onboarding`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Navigate to middle page
        viewModel.onEvent(OnboardingEvent.PageChanged(1))

        // Skip from middle page
        viewModel.onEvent(OnboardingEvent.Skip)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(onboardingRepository).setOnboardingCompleted()
    }

    @Test
    fun `test onboarding items have correct IDs`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val loadedItems = viewModel.uiState.value.onboardingItems
        assertThat(loadedItems[0].id).isEqualTo(1)
        assertThat(loadedItems[1].id).isEqualTo(2)
        assertThat(loadedItems[2].id).isEqualTo(3)
    }

    @Test
    fun `test onboarding items have correct content`() = runTest {
        val items = listOf(mockOnboardingItem1, mockOnboardingItem2, mockOnboardingItem3)
        whenever(onboardingRepository.getOnboardingItems()).thenReturn(items)

        viewModel = OnboardingViewModel(onboardingRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val loadedItems = viewModel.uiState.value.onboardingItems
        assertThat(loadedItems[0].title).isEqualTo("Welcome")
        assertThat(loadedItems[0].description).isEqualTo("Welcome to our app")
        assertThat(loadedItems[2].title).isEqualTo("Get Started")
    }
}
