package com.example.pixelbit.presentation.features.favorites

import app.cash.turbine.test
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.FavoritesRepository
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
class FavoritesViewModelTest {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var repository: FavoritesRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testProduct = Product("1", "Test Product", "Electronics",
        "Brand", "$99", "img.jpg", "Test", true)

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
    fun givenEmptyFavorites_whenLoadFavorites_thenEmptyListIsReturned() = runTest {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.success(emptyList())))

        // When
        viewModel = FavoritesViewModel(repository)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.favoriteProducts).isEmpty()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun givenFavoriteProducts_whenLoadFavorites_thenProductsAreLoaded() = runTest {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.success(listOf(testProduct))))

        // When
        viewModel = FavoritesViewModel(repository)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.favoriteProducts).hasSize(1)
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun givenRepositoryError_whenLoadFavorites_thenErrorMessageIsSet() = runTest {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.failure(Exception("Error"))))

        // When
        viewModel = FavoritesViewModel(repository)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isNotNull()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun givenProduct_whenRemoveFromFavorites_thenRepositoryIsCalled() = runTest {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.success(listOf(testProduct))))
        whenever(repository.removeFromFavorites("1")).thenReturn(Result.success(Unit))
        viewModel = FavoritesViewModel(repository)

        // When
        viewModel.removeFromFavorites("1")

        // Then
        verify(repository).removeFromFavorites("1")
    }

    @Test
    fun givenProduct_whenRemoveFromFavoritesFails_thenErrorMessageIsSet() = runTest {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.success(listOf(testProduct))))
        whenever(repository.removeFromFavorites("1")).thenReturn(Result.failure(Exception("Failed")))
        viewModel = FavoritesViewModel(repository)

        // When
        viewModel.removeFromFavorites("1")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isNotNull()
            assertThat(state.isRemoving).isFalse()
        }
    }
}

