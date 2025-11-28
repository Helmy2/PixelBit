package com.example.pixelbit.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val shopRepository: ShopRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners = _banners.asStateFlow()

    private val _loading = MutableStateFlow(true)
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
                val productsResult = shopRepository.getProducts()
                val categoriesResult = shopRepository.getCategories()
                val bannersResult = shopRepository.getBanners()
                _products.value = productsResult
                _categories.value = categoriesResult
                _banners.value = bannersResult
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
            val currentProducts = _products.value.map {
                if (it.id == productId) {
                    it.copy(isFavorite = !it.isFavorite)
                } else {
                    it
                }
            }
            _products.value = currentProducts

            try {
                val product = currentProducts.first { it.id == productId }
                shopRepository.updateProductFavoriteStatus(productId, product.isFavorite)
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
}