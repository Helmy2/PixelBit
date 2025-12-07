package com.example.pixelbit.presentation.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteProducts: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isRemoving: Boolean = false
)

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            favoritesRepository.getFavoriteProducts().collect { result ->
                result.onSuccess { products ->
                    _uiState.value = _uiState.value.copy(
                        favoriteProducts = products,
                        isLoading = false,
                        errorMessage = null
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load favorites"
                    )
                }
            }
        }
    }

    fun removeFromFavorites(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRemoving = true)

            val result = favoritesRepository.removeFromFavorites(productId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isRemoving = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isRemoving = false,
                    errorMessage = error.message ?: "Failed to remove from favorites"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

