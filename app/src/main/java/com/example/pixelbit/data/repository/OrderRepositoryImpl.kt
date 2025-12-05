package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Order
import com.example.pixelbit.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : OrderRepository {

    override fun getOrders(): Flow<Result<List<Order>>> = flow {
        try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                emit(Result.failure(Exception("User not logged in")))
                return@flow
            }

            val orders = firestore.collection("users").document(userId)
                .collection("orders")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .map {
                    val order = it.toObject(Order::class.java)
                    order.copy(id = it.id)
                }

            emit(Result.success(orders))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
