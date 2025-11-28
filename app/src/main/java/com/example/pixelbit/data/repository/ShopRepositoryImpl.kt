package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.ShopRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class ShopRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ShopRepository {

    override fun getProducts(): Flow<List<Product>> {
        val userId = firebaseAuth.currentUser?.uid

        val favoritesFlow: Flow<List<String>> = if (userId == null) {
            flowOf(emptyList())
        } else {
            callbackFlow {
                val listenerRegistration = firestore.collection("users").document(userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            cancel(message = "Error fetching favorites", cause = error)
                            return@addSnapshotListener
                        }
                        val favoriteIds = snapshot?.get("favorites") as? List<String> ?: emptyList()
                        trySend(favoriteIds)
                    }
                awaitClose { listenerRegistration.remove() }
            }
        }

        val productsFlow: Flow<List<Product>> = callbackFlow {
            val listenerRegistration = firestore.collection("products")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        cancel(message = "Error fetching products", cause = error)
                        return@addSnapshotListener
                    }
                    val products = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            Product(
                                id = doc.getString("id")!!,
                                title = doc.getString("title") ?: "",
                                category = doc.getString("category") ?: "",
                                brand = doc.getString("brand") ?: "",
                                price = doc.getString("price") ?: "",
                                images = doc.getString("images") ?: "",
                                description = doc.getString("description") ?: "",
                                isFavorite = false
                            )
                        } catch (_: Exception) {
                            null
                        }
                    } ?: emptyList()
                    trySend(products)
                }
            awaitClose { listenerRegistration.remove() }
        }

        return productsFlow.combine(favoritesFlow) { products, favorites ->
            products.map { product ->
                product.copy(isFavorite = favorites.contains(product.id))
            }
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
}