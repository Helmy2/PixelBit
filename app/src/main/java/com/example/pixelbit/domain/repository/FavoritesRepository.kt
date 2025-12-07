package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteProducts(): Flow<Result<List<Product>>>
    suspend fun addToFavorites(productId: String): Result<Unit>
    suspend fun removeFromFavorites(productId: String): Result<Unit>
    suspend fun isFavorite(productId: String): Boolean
}

