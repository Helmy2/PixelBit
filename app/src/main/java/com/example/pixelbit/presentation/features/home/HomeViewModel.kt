package com.example.pixelbit.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.model.User
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.CartRepository
import com.example.pixelbit.domain.repository.FavoritesRepository
import com.example.pixelbit.domain.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val shopRepository: ShopRepository,
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners = _banners.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        loadData()
    }

    fun loadData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            } else {
                _loading.value = true
            }
            try {
                launch {
                    shopRepository.getProducts().collect { products ->
                        val updatedProducts = products.map { product ->
                            val isFavorite = try {
                                favoritesRepository.isFavorite(product.id)
                            } catch (e: Exception) {
                                false
                            }
                            product.copy(isFavorite = isFavorite)
                        }
                        _products.value = updatedProducts
                    }
                }
                val user = authRepository.getCurrentUser()
                val categoriesResult = shopRepository.getCategories()
                val bannersResult = shopRepository.getBanners()
                _categories.value = categoriesResult
                _banners.value = bannersResult
                _user.value = user
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (isRefresh) {
                    _isRefreshing.value = false
                } else {
                    _loading.value = false
                }
            }
        }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val userId = _user.value?.uid ?: return@launch // Return if user is not logged in

            var wasFavorite = true
            val currentProducts = _products.value.map {
                if (it.id == productId) {
                    wasFavorite = it.isFavorite
                    it.copy(isFavorite = !it.isFavorite)
                } else {
                    it
                }
            }
            _products.value = currentProducts

            try {
                val product = currentProducts.first { it.id == productId }
                if (wasFavorite) {
                    favoritesRepository.removeFromFavorites(userId, productId)
                } else {
                    favoritesRepository.addToFavorites(userId, product)
                }
            } catch (e: Exception) {
                _products.value = _products.value.map {
                    if (it.id == productId) {
                        it.copy(isFavorite = !it.isFavorite)
                    } else {
                        it
                    }
                }
                e.printStackTrace()
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1, userId: String = "current_user_id") {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(
                    productId = product.id,
                    title = product.title,
                    brand = product.brand,
                    price = product.price,
                    images = product.images,
                    quantity = quantity,
                    userId = userId
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}