package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun getCategories(): List<Category>
    suspend fun getBanners(): List<Banner>
    fun getProductsByCategory(category: String): Flow<List<Product>>
}