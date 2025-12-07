package com.example.pixelbit.presentation.features.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.CartRepository
import com.example.pixelbit.domain.repository.FavoritesRepository
import com.example.pixelbit.domain.repository.ShopRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ProductsState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
)

class ProductsViewModel(
    private val shopRepository: ShopRepository,
    private val favoritesRepository: FavoritesRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state = _state.asStateFlow()

    private var productsJob: Job? = null

    fun getProductsByCategory(category: String) {
        _state.value = _state.value.copy(isLoading = true)
        productsJob = shopRepository.getProductsByCategory(category)
            .onEach { products ->
                _state.value = _state.value.copy(products = products, isLoading = false)
            }
            .catch { e ->
                e.printStackTrace()
                _state.value = _state.value.copy(isLoading = false)
            }
            .launchIn(viewModelScope)
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            var wasFavorite = true
            val currentProducts = _state.value.products.map {
                if (it.id == productId) {
                    wasFavorite = it.isFavorite
                    it.copy(isFavorite = !it.isFavorite)
                } else {
                    it
                }
            }
            _state.value = _state.value.copy(products = currentProducts)

            try {
                val product = currentProducts.first { it.id == productId }
                if (wasFavorite) {
                    favoritesRepository.removeFromFavorites(product.id)
                } else {
                    favoritesRepository.addToFavorites(product.id)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(products = _state.value.products.map {
                    if (it.id == productId) {
                        it.copy(isFavorite = !it.isFavorite)
                    } else {
                        it
                    }
                })
                e.printStackTrace()
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(
                    productId = product.id,
                    title = product.title,
                    brand = product.brand,
                    price = product.price,
                    images = product.images
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}