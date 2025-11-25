package com.example.pixelbit.domain.model

data class Product(
    val id: String = "",
    val title: String = "",
    val brand: String = "",
    val category: String = "",
    val description: String = "",
    val price: String = "",
    val images: String = "",
    val isFavorite: Boolean = false
)


