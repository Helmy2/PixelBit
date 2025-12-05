package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.repository.AddressRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AddressRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : AddressRepository {

    override fun getAddresses(): Flow<Result<List<Address>>> = flow {
        try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                emit(Result.failure(Exception("User not logged in")))
                return@flow
            }

            val addresses = firestore.collection("users").document(userId)
                .collection("addresses").get().await().map {
                    it.toObject(Address::class.java).copy(id = it.id)
                }

            emit(Result.success(addresses))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun addAddress(address: Address): Result<Unit> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val newAddressRef = firestore.collection("users").document(userId)
                .collection("addresses").document()
            val newAddress = address.copy(id = newAddressRef.id)

            if (newAddress.default) {
                // Ensure only one default address
                val batch = firestore.batch()
                val currentDefault = firestore.collection("users").document(userId)
                    .collection("addresses").whereEqualTo("default", true).get().await()

                currentDefault.documents.forEach { doc ->
                    batch.update(doc.reference, "default", false)
                }
                batch.commit().await()
            }

            newAddressRef.set(newAddress).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users").document(userId)
                .collection("addresses").document(addressId).delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setDefaultAddress(addressId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not logged in"))
            }

            val batch = firestore.batch()
            val currentDefault = firestore.collection("users").document(userId)
                .collection("addresses").whereEqualTo("default", true).get().await()

            currentDefault.documents.forEach { doc ->
                batch.update(doc.reference, "default", false)
            }

            val newDefault = firestore.collection("users").document(userId)
                .collection("addresses").document(addressId)
            batch.update(newDefault, "default", true)

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
