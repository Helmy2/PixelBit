package com.example.pixelbit.presentation.features.myorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.Order
import com.example.pixelbit.domain.repository.OrderRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyOrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class MyOrdersViewModel(
    private val orderRepository: OrderRepository,
    private val appNavigator: AppNavigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyOrdersUiState())
    val uiState: StateFlow<MyOrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun onBackClicked() {
        appNavigator.back()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            orderRepository.getOrders().collect { result ->
                result.onSuccess { orders ->
                    _uiState.value = _uiState.value.copy(
                        orders = orders,
                        isLoading = false,
                        errorMessage = null
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load orders"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
