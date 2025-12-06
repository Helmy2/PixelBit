package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.repository.CheckoutRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class CheckoutRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CheckoutRepository {

    override fun getCheckoutItems(): Flow<Result<List<CartItem>>> = flow {
        try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                emit(Result.failure(Exception("User not logged in")))
                return@flow
            }

            val cartItems = firestore.collection("users").document(userId)
                .collection("cart").get().await().map {
                    it.toObject(CartItem::class.java).copy(id = it.id)
                }

            emit(Result.success(cartItems))
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            emit(Result.failure(e))
        }
    }

    override suspend fun placeOrder(items: List<CartItem>, address: Address): Result<Unit> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val order = hashMapOf(
                "items" to items.map { it.toMap() },
                "timestamp" to System.currentTimeMillis(),
                "address" to address
            )

            firestore.collection("users").document(userId).collection("orders").add(order).await()

            // Clear cart after placing order
            val cartRef = firestore.collection("users").document(userId).collection("cart")
            val batch = firestore.batch()
            val snapshot = cartRef.get().await()
            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun CartItem.toMap(): Map<String, Any> {
        return mapOf(
            "productId" to productId,
            "title" to title,
            "brand" to brand,
            "price" to price,
            "images" to images,
            "quantity" to quantity
        )
    }
}
