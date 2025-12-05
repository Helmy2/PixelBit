package com.example.pixelbit.presentation.features.cart

import app.cash.turbine.test
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.repository.CartRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.navigation.Screen
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
class CartViewModelTest {

    private lateinit var viewModel: CartViewModel
    private lateinit var repository: CartRepository
    private lateinit var navigator: AppNavigator
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testCartItem = CartItem("1", "p1", "Test Product", "Brand", "100.0", "img.jpg", 1)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        navigator = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun givenEmptyCart_whenLoadCartItems_thenEmptyListIsReturned() = runTest {
        // Given
        whenever(repository.getCartItems()).thenReturn(flowOf(Result.success(emptyList())))

        // When
        viewModel = CartViewModel(repository, navigator)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.cartItems).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.subtotal).isEqualTo(0.0)
            assertThat(state.total).isEqualTo(0.0)
        }
    }

    @Test
    fun givenCartItems_whenLoadCartItems_thenItemsAreLoadedAndCalculated() = runTest {
        // Given
        whenever(repository.getCartItems()).thenReturn(flowOf(Result.success(listOf(testCartItem))))

        // When
        viewModel = CartViewModel(repository, navigator)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.cartItems).hasSize(1)
            assertThat(state.isLoading).isFalse()
            assertThat(state.subtotal).isEqualTo(100.0)
            assertThat(state.shipping).isEqualTo(5.0)
            assertThat(state.total).isEqualTo(105.0)
        }
    }

    @Test
    fun givenRepositoryError_whenLoadCartItems_thenErrorMessageIsSet() = runTest {
        // Given
        whenever(repository.getCartItems()).thenReturn(flowOf(Result.failure(Exception("Error"))))

        // When
        viewModel = CartViewModel(repository, navigator)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isNotNull()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun givenCartItem_whenUpdateQuantity_thenRepositoryIsCalled() = runTest {
        // Given
        whenever(repository.getCartItems()).thenReturn(flowOf(Result.success(listOf(testCartItem))))
        whenever(repository.updateQuantity("1", 2)).thenReturn(Result.success(Unit))
        viewModel = CartViewModel(repository, navigator)

        // When
        viewModel.updateQuantity("1", 2)

        // Then
        verify(repository).updateQuantity("1", 2)
    }

    @Test
    fun givenCartItem_whenRemoveItem_thenRepositoryIsCalled() = runTest {
        // Given
        whenever(repository.getCartItems()).thenReturn(flowOf(Result.success(listOf(testCartItem))))
        whenever(repository.removeFromCart("1")).thenReturn(Result.success(Unit))
        viewModel = CartViewModel(repository, navigator)

        // When
        viewModel.removeItem("1")

        // Then
        verify(repository).removeFromCart("1")
    }

    @Test
    fun givenCartItems_whenClearCart_thenRepositoryIsCalled() = runTest {
        // Given
        whenever(repository.getCartItems()).thenReturn(flowOf(Result.success(listOf(testCartItem))))
        whenever(repository.clearCart()).thenReturn(Result.success(Unit))
        viewModel = CartViewModel(repository, navigator)

        // When
        viewModel.clearCart()

        // Then
        verify(repository).clearCart()
    }

    @Test
    fun givenUserOnCart_whenCheckoutClick_thenNavigateToCheckout() = runTest {
        // Given
        whenever(repository.getCartItems()).thenReturn(flowOf(Result.success(emptyList())))
        viewModel = CartViewModel(repository, navigator)

        // When
        viewModel.onCheckoutClick()

        // Then
        verify(navigator).add(Screen.Checkout)
    }
}
