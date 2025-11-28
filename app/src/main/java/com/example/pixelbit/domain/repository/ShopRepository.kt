package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.domain.model.Product

interface ShopRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getCategories(): List<Category>
    suspend fun getBanners(): List<Banner>
}