package com.example.pixelbit.presentation.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.repository.AddressRepository
import com.example.pixelbit.domain.repository.CheckoutRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val checkoutItems: List<CartItem> = emptyList(),
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
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
    private val addressRepository: AddressRepository,
    private val appNavigator: AppNavigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            addressRepository.getAddresses().collect { result ->
                result.onSuccess { addresses ->
                    _uiState.value = _uiState.value.copy(
                        addresses = addresses,
                        selectedAddress = addresses.find { it.default } ?: addresses.firstOrNull()
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load addresses"
                    )
                }
            }

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

    fun onAddressSelected(address: Address) {
        _uiState.value = _uiState.value.copy(selectedAddress = address)
    }

    fun onAddAddressClicked() {
        appNavigator.add(Screen.Address)
    }

    fun placeOrder() {
        viewModelScope.launch {
            val selectedAddress = _uiState.value.selectedAddress
            if (selectedAddress == null) {
                _uiState.value =
                    _uiState.value.copy(errorMessage = "Please select a shipping address")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isPlacingOrder = true)

            val result =
                checkoutRepository.placeOrder(_uiState.value.checkoutItems, selectedAddress)

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

    fun onBackClicked() {
        appNavigator.back()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun onContinueShopping() {
        appNavigator.addAsStart(Screen.Home)
    }
}
