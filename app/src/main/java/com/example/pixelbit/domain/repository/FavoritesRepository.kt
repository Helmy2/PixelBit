package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun getFavoriteProducts(userId: String): Flow<Result<List<Product>>>
    suspend fun addToFavorites(userId: String, product: Product): Result<Unit>
    suspend fun removeFromFavorites(userId: String, productId: String): Result<Unit>
    suspend fun isFavorite(productId: String): Boolean
}

