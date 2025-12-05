package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<Result<List<Order>>>
}
