package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CheckoutRepository {
    fun getCheckoutItems(): Flow<Result<List<CartItem>>>
    suspend fun placeOrder(items: List<CartItem>): Result<Unit>
}
