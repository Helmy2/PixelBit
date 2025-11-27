package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.ShopRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ShopRepositoryImpl : ShopRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = firestore.collection("products").get().await()
            snapshot.documents.map { doc ->
                Product(
                    id = doc.id, // Correctly use the document ID
                    title = doc.getString("title") ?: "",
                    category = doc.getString("category") ?: "",
                    brand = doc.getString("brand") ?: "",
                    price = doc.getString("price") ?: "",
                    images = doc.getString("images") ?: "",
                    description = doc.getString("description") ?: "",
                    isFavorite = doc.getBoolean("isFavorite") ?: false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getCategories(): List<Category> {
        return try {
            val snapshot = firestore.collection("categories").get().await()
            snapshot.documents.map { doc ->
                Category(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    itemCount = doc.getLong("itemCount")?.toInt() ?: 0,
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getBanners(): List<Banner> {
        return try {
            val snapshot = firestore.collection("banners").get().await()
            snapshot.documents.map { doc ->
                Banner(
                    id = doc.id,
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun updateProductFavoriteStatus(productId: String, isFavorite: Boolean) {
        firestore.collection("products").document(productId).update("isFavorite", isFavorite).await()
    }
}