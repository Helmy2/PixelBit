package com.example.pixelbit.presentation.features.auth.verification

import app.cash.turbine.test
import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class VerificationViewModelTest {

    private lateinit var viewModel: VerificationViewModel
    private lateinit var repository: AuthRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `givenViewModelInitialized, whenSendVerificationEmail, thenStateIsSuccess`() = runTest {
        // Given
        whenever(repository.sendEmailVerification()).thenReturn(flowOf(AuthResult.Success(true)))
        viewModel = VerificationViewModel(repository)

        // When
        viewModel.sendVerificationEmail()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.verificationEmailSent).isTrue()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `givenEmailIsVerified, whenCheckVerification, thenStateIsVerified`() = runTest {
        // Given
        whenever(repository.checkEmailVerification()).thenReturn(flowOf(AuthResult.Success(true)))
        viewModel = VerificationViewModel(repository)

        // When
        viewModel.checkVerification()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isVerified).isTrue()
        }
    }

    @Test
    fun `givenEmailNotVerified, whenCheckVerification, thenStateIsNotVerifiedWithMessage`() =
        runTest {
            // Given
            whenever(repository.checkEmailVerification()).thenReturn(flowOf(AuthResult.Success(false)))
            viewModel = VerificationViewModel(repository)

            // When
            viewModel.checkVerification()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.isVerified).isFalse()
                assertThat(state.errorMessage).isNotNull()
            }
        }

    @Test
    fun `givenRepositoryError, whenCheckVerification, thenErrorStateIsSet`() = runTest {
        // Given
        whenever(repository.checkEmailVerification()).thenReturn(flowOf(AuthResult.Error("Error")))
        viewModel = VerificationViewModel(repository)

        // When
        viewModel.checkVerification()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isNotNull()
        }
    }

    @Test
    fun `givenUserAction, whenDeleteUser, thenRepositoryIsCalled`() = runTest {
        // Given
        whenever(repository.deleteCurrentUser()).thenReturn(flowOf(AuthResult.Success(true)))
        viewModel = VerificationViewModel(repository)

        // When
        viewModel.deleteUserAndSignOut()

        // Then
        verify(repository).deleteCurrentUser()
    }
}
