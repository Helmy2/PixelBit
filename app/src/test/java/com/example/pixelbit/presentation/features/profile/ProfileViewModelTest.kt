package com.example.pixelbit.presentation.features.profile

import com.example.pixelbit.domain.model.User
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.navigation.Screen
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val authRepository: AuthRepository = mock()
    private val appNavigator: AppNavigator = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val mockUser = User(
        uid = "user123",
        name = "Test User",
        email = "test@example.com",
        phone = "+1234567890",
        isEmailVerified = true,
        createdAt = 1704067200000L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Set default mock behavior to prevent NullPointerException
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(mockUser)))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() {
        val initialState = ProfileUiState()
        assertThat(initialState.user).isNull()
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.errorMessage).isNull()
    }

    @Test
    fun `test loadProfile success loads user data`() = runTest {
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(mockUser)))

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.user).isEqualTo(mockUser)
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `test loadProfile loads user with all fields correctly`() = runTest {
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(mockUser)))

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val user = viewModel.uiState.value.user
        assertThat(user?.uid).isEqualTo("user123")
        assertThat(user?.name).isEqualTo("Test User")
        assertThat(user?.email).isEqualTo("test@example.com")
        assertThat(user?.phone).isEqualTo("+1234567890")
        assertThat(user?.isEmailVerified).isTrue()
        assertThat(user?.createdAt).isEqualTo(1704067200000L)
    }

    @Test
    fun `test loadProfile with unverified email`() = runTest {
        val unverifiedUser = mockUser.copy(isEmailVerified = false)
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(unverifiedUser)))

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val user = viewModel.uiState.value.user
        assertThat(user?.isEmailVerified).isFalse()
    }

    @Test
    fun `test loadProfile failure shows error message`() = runTest {
        val errorMessage = "Network error"
        whenever(authRepository.getProfileFlow()).thenReturn(
            flowOf(Result.failure(Exception(errorMessage)))
        )

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.user).isNull()
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `test loadProfile failure with null message shows default error`() = runTest {
        whenever(authRepository.getProfileFlow()).thenReturn(
            flowOf(Result.failure(Exception()))
        )

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isEqualTo("Failed to load profile")
    }

    @Test
    fun `test signOut calls repository and navigates to onboarding`() = runTest {
        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(authRepository).signOut()
        verify(appNavigator).addAsStart(Screen.Onboarding)
    }

    @Test
    fun `test onMyOrdersClick navigates to my orders screen`() = runTest {
        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onMyOrdersClick()

        verify(appNavigator).add(Screen.MyOrders)
    }

    @Test
    fun `test onManageAddressClick navigates to address screen`() = runTest {
        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onManageAddressClick()

        verify(appNavigator).add(Screen.Address)
    }

    @Test
    fun `test clearError clears error message`() = runTest {
        whenever(authRepository.getProfileFlow()).thenReturn(
            flowOf(Result.failure(Exception("Error")))
        )

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.errorMessage).isNotNull()

        viewModel.clearError()

        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test
    fun `test clearError does not affect user or loading state`() = runTest {
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(mockUser)))

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val state = viewModel.uiState.value
        assertThat(state.user).isEqualTo(mockUser)
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `test ViewModel initialization triggers loadProfile`() = runTest {
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(mockUser)))

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(authRepository).getProfileFlow()
    }

    @Test
    fun `test user with empty phone is loaded correctly`() = runTest {
        val userWithoutPhone = mockUser.copy(phone = "")
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(userWithoutPhone)))

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val user = viewModel.uiState.value.user
        assertThat(user?.phone).isEmpty()
    }

    @Test
    fun `test multiple navigation actions work independently`() = runTest {
        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onMyOrdersClick()
        viewModel.onManageAddressClick()

        verify(appNavigator).add(Screen.MyOrders)
        verify(appNavigator).add(Screen.Address)
    }

    @Test
    fun `test signOut works with null user`() = runTest {
        whenever(authRepository.getProfileFlow()).thenReturn(
            flowOf(Result.failure(Exception("No user")))
        )

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.user).isNull()

        viewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(authRepository).signOut()
        verify(appNavigator).addAsStart(Screen.Onboarding)
    }

    @Test
    fun `test user createdAt timestamp is preserved`() = runTest {
        val timestamp = 1704067200000L
        val user = mockUser.copy(createdAt = timestamp)
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(user)))

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.user?.createdAt).isEqualTo(timestamp)
    }

    @Test
    fun `test user with default values is loaded correctly`() = runTest {
        val defaultUser = User()
        whenever(authRepository.getProfileFlow()).thenReturn(flowOf(Result.success(defaultUser)))

        viewModel = ProfileViewModel(authRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val user = viewModel.uiState.value.user
        assertThat(user?.uid).isEmpty()
        assertThat(user?.name).isEmpty()
        assertThat(user?.email).isEmpty()
        assertThat(user?.phone).isEmpty()
        assertThat(user?.isEmailVerified).isFalse()
        assertThat(user?.createdAt).isNotNull()
    }
}
