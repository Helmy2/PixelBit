package com.example.pixelbit.domain.model

data class Product(
    val id: String,
    val title: String,
    val category: String,
    val brand: String,
    val price: String,
    val images: String,
    val description: String,
    val isFavorite: Boolean
)