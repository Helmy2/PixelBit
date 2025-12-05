package com.example.pixelbit.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val items: List<CartItem> = emptyList(),
    val timestamp: Long = 0,
    val status: OrderStatus = OrderStatus.PENDING,
    val address: Address? = null
)
