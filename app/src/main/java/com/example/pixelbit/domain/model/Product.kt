package com.example.pixelbit.domain.model

data class Product(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val brand: String = "",
    val price: String = "0.0",
    val images: String = "",
    val description: String = "",
    val isFavorite: Boolean = false,
    val rating: Double = 0.0,
    val reviewCount: Int = 0
)