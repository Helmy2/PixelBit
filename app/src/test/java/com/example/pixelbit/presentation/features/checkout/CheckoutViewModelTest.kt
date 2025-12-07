package com.example.pixelbit.presentation.features.checkout

import app.cash.turbine.test
import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.repository.AddressRepository
import com.example.pixelbit.domain.repository.CheckoutRepository
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CheckoutViewModelTest {

    private lateinit var viewModel: CheckoutViewModel
    private val checkoutRepository: CheckoutRepository = mock()
    private val addressRepository: AddressRepository = mock()
    private val appNavigator: AppNavigator = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val mockCartItem1 = mock<CartItem>().apply {
        whenever(this.getTotalPrice()).thenReturn(100.0)
    }
    private val mockCartItem2 = mock<CartItem>().apply {
        whenever(this.getTotalPrice()).thenReturn(50.0)
    }
    private val mockAddress1 = Address(id = "1", street = "123 Main St", default = true)
    private val mockAddress2 = Address(id = "2", street = "456 Oak Ave", default = false)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Set default behavior for mocks to prevent NullPointerException
        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(emptyList())))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(emptyList())))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() {
        val initialState = CheckoutUiState()
        assertThat(initialState.checkoutItems).isEmpty()
        assertThat(initialState.addresses).isEmpty()
        assertThat(initialState.selectedAddress).isNull()
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.errorMessage).isNull()
        assertThat(initialState.isPlacingOrder).isFalse()
        assertThat(initialState.orderPlaced).isFalse()
    }

    @Test
    fun `test calculatePrices with items`() {
        val items = listOf(mockCartItem1, mockCartItem2)
        val state = CheckoutUiState(checkoutItems = items)

        val updatedState = state.calculatePrices()

        assertThat(updatedState.subtotal).isEqualTo(150.0)
        assertThat(updatedState.shipping).isEqualTo(5.0)
        assertThat(updatedState.total).isEqualTo(155.0)
    }

    @Test
    fun `test calculatePrices with empty cart`() {
        val state = CheckoutUiState(checkoutItems = emptyList())

        val updatedState = state.calculatePrices()

        assertThat(updatedState.subtotal).isEqualTo(0.0)
        assertThat(updatedState.shipping).isEqualTo(0.0)
        assertThat(updatedState.total).isEqualTo(0.0)
    }

    @Test
    fun `test loadCheckoutData success loads addresses and items`() = runTest {
        val addresses = listOf(mockAddress1, mockAddress2)
        val items = listOf(mockCartItem1, mockCartItem2)

        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(addresses)))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(items)))

        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.addresses).isEqualTo(addresses)
        assertThat(state.selectedAddress).isEqualTo(mockAddress1) // Default address selected
        assertThat(state.checkoutItems).isEqualTo(items)
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
        assertThat(state.subtotal).isEqualTo(150.0)
        assertThat(state.shipping).isEqualTo(5.0)
        assertThat(state.total).isEqualTo(155.0)
    }

    @Test
    fun `test loadCheckoutData selects first address when no default`() = runTest {
        val addresses = listOf(
            Address(id = "1", street = "123 Main St", default = false),
            Address(id = "2", street = "456 Oak Ave", default = false)
        )
        val items = listOf(mockCartItem1)

        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(addresses)))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(items)))

        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.selectedAddress).isEqualTo(addresses.first())
    }

    @Test
    fun `test loadCheckoutData items failure shows error`() = runTest {
        val addresses = listOf(mockAddress1)
        val errorMessage = "Failed to load checkout items"

        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(addresses)))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(
            flowOf(Result.failure(Exception(errorMessage)))
        )

        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `test onAddressSelected updates selected address`() = runTest {
        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onAddressSelected(mockAddress2)

        assertThat(viewModel.uiState.value.selectedAddress).isEqualTo(mockAddress2)
    }

    @Test
    fun `test onAddAddressClicked navigates to address screen`() = runTest {
        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onAddAddressClicked()

        verify(appNavigator).add(Screen.Address)
    }

    @Test
    fun `test placeOrder without selected address shows error`() = runTest {
        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Please select a shipping address")
        assertThat(viewModel.uiState.value.isPlacingOrder).isFalse()
    }

    @Test
    fun `test placeOrder success updates state`() = runTest {
        val addresses = listOf(mockAddress1)
        val items = listOf(mockCartItem1)

        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(addresses)))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(items)))
        whenever(checkoutRepository.placeOrder(any(), any())).thenReturn(Result.success(Unit))

        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isPlacingOrder).isFalse()
        assertThat(state.orderPlaced).isTrue()
        verify(checkoutRepository).placeOrder(items, mockAddress1)
    }

    @Test
    fun `test placeOrder failure shows error`() = runTest {
        val addresses = listOf(mockAddress1)
        val items = listOf(mockCartItem1)
        val errorMessage = "Payment failed"

        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(addresses)))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(items)))
        whenever(checkoutRepository.placeOrder(any(), any())).thenReturn(
            Result.failure(Exception(errorMessage))
        )

        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isPlacingOrder).isFalse()
        assertThat(state.orderPlaced).isFalse()
        assertThat(state.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `test placeOrder sets isPlacingOrder to true during processing`() = runTest {
        val addresses = listOf(mockAddress1)
        val items = listOf(mockCartItem1)

        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(addresses)))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(items)))
        whenever(checkoutRepository.placeOrder(any(), any())).thenReturn(Result.success(Unit))

        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(1) // Skip initial state

            viewModel.placeOrder()

            // State with isPlacingOrder = true
            val placingState = awaitItem()
            assertThat(placingState.isPlacingOrder).isTrue()

            testDispatcher.scheduler.advanceUntilIdle()

            // Final state with orderPlaced = true
            val finalState = awaitItem()
            assertThat(finalState.isPlacingOrder).isFalse()
            assertThat(finalState.orderPlaced).isTrue()
        }
    }

    @Test
    fun `test onBackClicked navigates back`() = runTest {
        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onBackClicked()

        verify(appNavigator).back()
    }

    @Test
    fun `test onContinueShopping navigates to home`() = runTest {
        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onContinueShopping()

        verify(appNavigator).addAsStart(Screen.Home)
    }
}
