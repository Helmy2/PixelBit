package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : CartRepository {

    private fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun getCartItems(): Flow<Result<List<CartItem>>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(Result.failure(Exception("User not logged in")))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(userId)
            .collection("cart")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val cartItems = snapshot.documents.mapNotNull { doc ->
                        try {
                            CartItem(
                                id = doc.id,
                                productId = doc.getString("productId") ?: "",
                                title = doc.getString("title") ?: "",
                                brand = doc.getString("brand") ?: "",
                                price = doc.getString("price") ?: "0",
                                images = doc.getString("images") ?: "",
                                quantity = doc.getLong("quantity")?.toInt() ?: 1,
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(Result.success(cartItems))
                } else {
                    trySend(Result.success(emptyList()))
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addToCart(
        productId: String,
        title: String,
        brand: String,
        price: String,
        images: String,
        quantity: Int,
    ): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            val existingItems = firestore.collection("users")
                .document(userId)
                .collection("cart")
                .whereEqualTo("productId", productId)
                .get()
                .await()

            if (!existingItems.isEmpty) {
                val docId = existingItems.documents[0].id
                val currentQuantity = existingItems.documents[0].getLong("quantity")?.toInt() ?: 0
                firestore.collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(docId)
                    .update("quantity", currentQuantity + quantity)
                    .await()
            } else {
                val cartItem = hashMapOf(
                    "productId" to productId,
                    "title" to title,
                    "brand" to brand,
                    "price" to price,
                    "images" to images,
                    "quantity" to quantity,
                    "userId" to userId,
                    "addedAt" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(userId)
                    .collection("cart")
                    .add(cartItem)
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            if (quantity <= 0) {
                return removeFromCart(cartItemId)
            }

            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document(cartItemId)
                .update("quantity", quantity)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(cartItemId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document(cartItemId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            val cartItems = firestore.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .await()

            val batch = firestore.batch()
            cartItems.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCartItemCount(): Int {
        return try {
            val userId = getCurrentUserId() ?: return 0

            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .await()

            snapshot.documents.sumOf { doc ->
                doc.getLong("quantity")?.toInt() ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }
}

