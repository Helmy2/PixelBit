package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.FavoritesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavoritesRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FavoritesRepository {

    private fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun getFavoriteProducts(): Flow<Result<List<Product>>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(Result.failure(Exception("User not logged in")))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { userSnapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (userSnapshot == null || !userSnapshot.exists()) {
                    trySend(Result.success(emptyList()))
                    return@addSnapshotListener
                }

                val favoriteIds = userSnapshot.get("favorites") as? List<*>
                if (favoriteIds.isNullOrEmpty()) {
                    trySend(Result.success(emptyList()))
                    return@addSnapshotListener
                }

                val productIds = favoriteIds.filterIsInstance<String>()
                if (productIds.isEmpty()) {
                    trySend(Result.success(emptyList()))
                    return@addSnapshotListener
                }

                launch {
                    try {
                        val productsSnapshot = firestore.collection("products")
                            .whereIn("id", productIds)
                            .get()
                            .await()

                        val products = productsSnapshot.documents.mapNotNull { doc ->
                            try {
                                Product(
                                    id = doc.getString("id") ?: "",
                                    title = doc.getString("title") ?: "",
                                    brand = doc.getString("brand") ?: "",
                                    category = doc.getString("category") ?: "",
                                    description = doc.getString("description") ?: "",
                                    price = doc.getString("price") ?: "",
                                    images = doc.getString("images") ?: "",
                                    isFavorite = true
                                )
                            } catch (_: Exception) {
                                null
                            }
                        }
                        trySend(Result.success(products))
                    } catch (exception: Exception) {
                        trySend(Result.failure(exception))
                    }
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addToFavorites(productId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .update("favorites", FieldValue.arrayUnion(productId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(productId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .update("favorites", FieldValue.arrayRemove(productId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isFavorite(productId: String): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val favorites = document.get("favorites") as? List<*>
            favorites?.contains(productId) == true
        } catch (_: Exception) {
            false
        }
    }
}

