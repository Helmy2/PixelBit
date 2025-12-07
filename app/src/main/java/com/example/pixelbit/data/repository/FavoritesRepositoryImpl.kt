package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.FavoritesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavoritesRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : FavoritesRepository {

    override fun getFavoriteProducts(): Flow<Result<List<Product>>> =
        callbackFlow {
            val userId = firebaseAuth.currentUser!!.uid

            val listener = firestore.collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        @Suppress("UNCHECKED_CAST")
                        val favoriteIds = snapshot.get("favorites") as? List<String> ?: emptyList()

                        if (favoriteIds.isEmpty()) {
                            trySend(Result.success(emptyList()))
                        } else {
                            launch {
                                try {
                                    val products = fetchProductsByIds(favoriteIds)
                                    trySend(Result.success(products))
                                } catch (e: Exception) {
                                    trySend(Result.failure(e))
                                }
                            }
                        }
                    } else {
                        trySend(Result.success(emptyList()))
                    }
                }

            awaitClose { listener.remove() }
        }

    private suspend fun fetchProductsByIds(ids: List<String>): List<Product> {
        val products = mutableListOf<Product>()

        ids.chunked(10).forEach { chunk ->
            val snapshot = firestore.collection("products")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()

            val chunkProducts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(
                    id = doc.id,
                    isFavorite = true
                )
            }
            products.addAll(chunkProducts)
        }
        return products
    }

    override suspend fun addToFavorites(productId: String): Result<Unit> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
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
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))


            firestore.collection("users")
                .document(userId)
                .update(
                    "favorites", FieldValue
                        .arrayRemove(productId)
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isFavorite(productId: String): Boolean {
        val userId = firebaseAuth.currentUser?.uid ?: return false
        val userDoc = firestore.collection("users")
            .document(userId)
            .get()
            .await()
        val favorites = userDoc.get("favorites") as? List<*>
        return favorites?.contains(productId) == true
    }
}

