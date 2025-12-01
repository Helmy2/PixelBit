package com.example.pixelbit.presentation.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0
) {
    fun calculatePrices(): CartUiState {
        val subtotal = cartItems.sumOf { it.getTotalPrice() }
        val shipping = if (subtotal > 0) 5.0 else 0.0
        val total = subtotal + shipping

        return copy(
            subtotal = subtotal,
            shipping = shipping,
            total = total
        )
    }
}

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            cartRepository.getCartItems().collect { result ->
                result.onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        cartItems = items,
                        isLoading = false,
                        errorMessage = null
                    ).calculatePrices()
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load cart"
                    )
                }
            }
        }
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)

            val result = cartRepository.updateQuantity(cartItemId, newQuantity)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isUpdating = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    errorMessage = error.message ?: "Failed to update quantity"
                )
            }
        }
    }

    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)

            val result = cartRepository.removeFromCart(cartItemId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isUpdating = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    errorMessage = error.message ?: "Failed to remove item"
                )
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)

            val result = cartRepository.clearCart()

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isUpdating = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    errorMessage = error.message ?: "Failed to clear cart"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

