package com.example.pixelbit.domain.model

data class CartItem(
    val id: String = "",
    val productId: String = "",
    val title: String = "",
    val brand: String = "",
    val price: String = "",
    val images: String = "",
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun getTotalPrice(): Double {
        return try {
            price.toDouble() * quantity
        } catch (e: Exception) {
            0.0
        }
    }
}

