package com.example.pixelbit.presentation.features.products.details

import ProductRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.CartRepository
import com.example.pixelbit.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val favoritesRepository: FavoritesRepository, // Inject this
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _productState = MutableStateFlow<Product?>(null)
    private val _addToCartMessage = MutableStateFlow<String?>(null)
    val addToCartMessage = _addToCartMessage.asStateFlow()
    val productState = _productState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity = _quantity.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    fun loadProduct(productId: String) {
        if (productId.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = productRepository.getProductById(productId)

            result.onSuccess { product ->
                _productState.value = product
                _isFavorite.value = product.isFavorite
                _isLoading.value = false
                checkIfFavorite(productId)
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Failed to load product"
                _isLoading.value = false
            }
        }
    }

    private fun checkIfFavorite(productId: String) {
        val userId = authRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            try {
                favoritesRepository.getFavoriteProducts(userId).collect { result ->
                    result.onSuccess { favorites ->
                        _isFavorite.value = favorites.any { it.id == productId }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
    fun toggleFavorite() {
        val product = _productState.value ?: return
        val userId = authRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            if (_isFavorite.value) {
                // Remove
                val result = favoritesRepository.removeFromFavorites(userId, product.id)
                if (result.isSuccess) {
                    _isFavorite.value = false
                }
            } else {
                // Add
                val result = favoritesRepository.addToFavorites(userId, product)
                if (result.isSuccess) {
                    _isFavorite.value = true
                }
            }
        }
    }

    fun increaseQuantity() {
        _quantity.value += 1
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
        }
    }

    fun addToCart() {
        val currentProduct = _productState.value ?: return
        val currentUserId = authRepository.getCurrentUserId() ?: return
        val qty = _quantity.value

        viewModelScope.launch {
            _isLoading.value = true

            val result = cartRepository.addToCart(
                productId = currentProduct.id,
                title = currentProduct.title,
                brand = currentProduct.brand,
                price = currentProduct.price,
                images = currentProduct.images,
                quantity = qty,
                userId = currentUserId
            )
            _isLoading.value = false

            result.onSuccess {
                _addToCartMessage.value = "Successfully added $qty item(s) to cart"
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Failed to add to cart"
            }
        }
    }
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}