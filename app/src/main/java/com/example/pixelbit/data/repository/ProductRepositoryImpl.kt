package com.example.pixelbit.data.repository

import ProductRepository
import com.example.pixelbit.domain.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val document = firestore.collection("products")
                .document(productId)
                .get()
                .await()

            val product = document.toObject(Product::class.java)

            if (product != null) {
                Result.success(product.copy(id = document.id))
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}