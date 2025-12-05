package com.example.pixelbit.presentation.features.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.repository.AddressRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddressUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isDialogShown: Boolean = false,
    val selectedAddress: Address? = null
)

class AddressViewModel(
    private val addressRepository: AddressRepository,
    private val appNavigator: AppNavigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            addressRepository.getAddresses().collect { result ->
                result.onSuccess { addresses ->
                    _uiState.value = _uiState.value.copy(
                        addresses = addresses,
                        isLoading = false,
                        errorMessage = null
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load addresses"
                    )
                }
            }
        }
    }

    fun onAddAddressClicked() {
        _uiState.value = _uiState.value.copy(isDialogShown = true, selectedAddress = null)
    }

    fun onEditAddressClicked(address: Address) {
        _uiState.value = _uiState.value.copy(isDialogShown = true, selectedAddress = address)
    }

    fun onDialogDismissed() {
        _uiState.value = _uiState.value.copy(isDialogShown = false, selectedAddress = null)
    }

    fun onAddressSaved(address: Address) {
        viewModelScope.launch {
            addressRepository.addAddress(address).onSuccess {
                onDialogDismissed()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Failed to save address"
                )
            }
        }
    }

    fun onDeleteAddressClicked(addressId: String) {
        viewModelScope.launch {
            addressRepository.deleteAddress(addressId).onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Failed to delete address"
                )
            }
        }
    }

    fun onSetDefaultAddressClicked(addressId: String) {
        viewModelScope.launch {
            addressRepository.setDefaultAddress(addressId).onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Failed to set default address"
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
}
