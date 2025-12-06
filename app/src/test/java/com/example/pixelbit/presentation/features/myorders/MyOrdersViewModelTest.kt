package com.example.pixelbit.presentation.features.myorders

import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.model.Order
import com.example.pixelbit.domain.model.OrderStatus
import com.example.pixelbit.domain.repository.OrderRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
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
class MyOrdersViewModelTest {

    private lateinit var viewModel: MyOrdersViewModel
    private val orderRepository: OrderRepository = mock()
    private val appNavigator: AppNavigator = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val mockAddress = Address(
        id = "addr1",
        street = "123 Main St",
        city = "City",
        default = true
    )

    private val mockCartItem1 = mock<CartItem>()
    private val mockCartItem2 = mock<CartItem>()

    private val mockOrder1 = Order(
        id = "1",
        items = listOf(mockCartItem1),
        timestamp = 1704067200000L, // 2024-01-01
        status = OrderStatus.DELIVERED,
        address = mockAddress
    )

    private val mockOrder2 = Order(
        id = "2",
        items = listOf(mockCartItem2),
        timestamp = 1705276800000L, // 2024-01-15
        status = OrderStatus.PENDING,
        address = mockAddress
    )

    private val mockOrder3 = Order(
        id = "3",
        items = listOf(mockCartItem1, mockCartItem2),
        timestamp = 1706745600000L, // 2024-02-01
        status = OrderStatus.SHIPPED,
        address = mockAddress
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Set default mock behavior to prevent NullPointerException
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(emptyList())))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() {
        val initialState = MyOrdersUiState()
        assertThat(initialState.orders).isEmpty()
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.errorMessage).isNull()
    }

    @Test
    fun `test loadOrders success loads orders`() = runTest {
        val orders = listOf(mockOrder1, mockOrder2, mockOrder3)
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(orders)))

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.orders).isEqualTo(orders)
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `test loadOrders with empty list`() = runTest {
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(emptyList())))

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.orders).isEmpty()
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `test loadOrders failure shows error message`() = runTest {
        val errorMessage = "Network error"
        whenever(orderRepository.getOrders()).thenReturn(
            flowOf(Result.failure(Exception(errorMessage)))
        )

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.orders).isEmpty()
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `test loadOrders failure with null message shows default error`() = runTest {
        whenever(orderRepository.getOrders()).thenReturn(
            flowOf(Result.failure(Exception()))
        )

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isEqualTo("Failed to load orders")
    }

    @Test
    fun `test onBackClicked navigates back`() = runTest {
        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onBackClicked()

        verify(appNavigator).back()
    }

    @Test
    fun `test clearError clears error message`() = runTest {
        whenever(orderRepository.getOrders()).thenReturn(
            flowOf(Result.failure(Exception("Network error")))
        )

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.errorMessage).isNotNull()

        viewModel.clearError()

        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test
    fun `test clearError does not affect orders or loading state`() = runTest {
        val orders = listOf(mockOrder1, mockOrder2)
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(orders)))

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        val state = viewModel.uiState.value
        assertThat(state.orders).isEqualTo(orders)
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `test multiple orders are loaded correctly`() = runTest {
        val orders = listOf(mockOrder1, mockOrder2, mockOrder3)
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(orders)))

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.orders).hasSize(3)
        assertThat(state.orders[0].id).isEqualTo("1")
        assertThat(state.orders[1].id).isEqualTo("2")
        assertThat(state.orders[2].id).isEqualTo("3")
    }

    @Test
    fun `test orders with different statuses are loaded`() = runTest {
        val orders = listOf(mockOrder1, mockOrder2, mockOrder3)
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(orders)))

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.orders[0].status).isEqualTo(OrderStatus.DELIVERED)
        assertThat(state.orders[1].status).isEqualTo(OrderStatus.PENDING)
        assertThat(state.orders[2].status).isEqualTo(OrderStatus.SHIPPED)
    }

    @Test
    fun `test orders maintain order from repository`() = runTest {
        val orders = listOf(mockOrder3, mockOrder1, mockOrder2)
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(orders)))

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.orders[0].id).isEqualTo("3")
        assertThat(state.orders[1].id).isEqualTo("1")
        assertThat(state.orders[2].id).isEqualTo("2")
    }

    @Test
    fun `test ViewModel initialization triggers loadOrders`() = runTest {
        val orders = listOf(mockOrder1)
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(orders)))

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(orderRepository).getOrders()
    }

    @Test
    fun `test order with multiple items is loaded correctly`() = runTest {
        val order = mockOrder3 // Has 2 items
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(listOf(order))))

        viewModel = MyOrdersViewModel(orderRepository, appNavigator)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.orders.first().items).hasSize(2)
    }

}
