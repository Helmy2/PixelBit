package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<Result<List<CartItem>>>
    suspend fun addToCart(productId: String, title: String, brand: String, price: String, images: String, quantity: Int,userId: String): Result<Unit>
    suspend fun updateQuantity(cartItemId: String, quantity: Int): Result<Unit>
    suspend fun removeFromCart(cartItemId: String): Result<Unit>
    suspend fun clearCart(): Result<Unit>
    suspend fun getCartItemCount(): Int
}

