package com.example.pixelbit.presentation.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.repository.CheckoutRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val checkoutItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isPlacingOrder: Boolean = false,
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val orderPlaced: Boolean = false
) {
    fun calculatePrices(): CheckoutUiState {
        val subtotal = checkoutItems.sumOf { it.getTotalPrice() }
        val shipping = if (subtotal > 0) 5.0 else 0.0
        val total = subtotal + shipping

        return copy(
            subtotal = subtotal,
            shipping = shipping,
            total = total
        )
    }
}

class CheckoutViewModel(
    private val checkoutRepository: CheckoutRepository,
    private val appNavigator: AppNavigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadCheckoutItems()
    }

    fun onBackClicked() {
        appNavigator.back()
    }

    private fun loadCheckoutItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            checkoutRepository.getCheckoutItems().collect { result ->
                result.onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        checkoutItems = items,
                        isLoading = false,
                        errorMessage = null
                    ).calculatePrices()
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load checkout items"
                    )
                }
            }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPlacingOrder = true)

            val result = checkoutRepository.placeOrder(_uiState.value.checkoutItems)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isPlacingOrder = false, orderPlaced = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isPlacingOrder = false,
                    errorMessage = error.message ?: "Failed to place order"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
