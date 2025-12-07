package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.model.User
import com.example.pixelbit.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

@Suppress("DEPRECATION")
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Flow<AuthResult<User>> = callbackFlow {
        try {
            trySend(AuthResult.Loading)

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                val user = User(
                    uid = firebaseUser.uid,
                    name = name,
                    email = email,
                    phone = phone,
                    isEmailVerified = false,
                    createdAt = System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()

                trySend(AuthResult.Success(user))
            } else {
                trySend(AuthResult.Error("Failed to create user"))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Unknown error occurred"))
        }

        awaitClose()
    }

    override fun sendEmailVerification(): Flow<AuthResult<Boolean>> = callbackFlow {
        try {
            trySend(AuthResult.Loading)

            val user = firebaseAuth.currentUser
            if (user != null) {
                user.sendEmailVerification().await()
                trySend(AuthResult.Success(true))
            } else {
                trySend(AuthResult.Error("No user logged in"))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Failed to send verification email"))
        }

        awaitClose()
    }

    override fun checkEmailVerification(): Flow<AuthResult<Boolean>> = callbackFlow {
        try {
            trySend(AuthResult.Loading)

            val user = firebaseAuth.currentUser
            if (user != null) {
                user.reload().await()
                val isVerified = user.isEmailVerified

                if (isVerified) {
                    firestore.collection("users")
                        .document(user.uid)
                        .update("isEmailVerified", true)
                        .await()
                }

                trySend(AuthResult.Success(isVerified))
            } else {
                trySend(AuthResult.Error("No user logged in"))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Failed to check verification status"))
        }

        awaitClose()
    }

    override fun signIn(email: String, password: String): Flow<AuthResult<User>> = callbackFlow {
        try {
            trySend(AuthResult.Loading)

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userDoc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                val user = userDoc.toObject(User::class.java) ?: User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    isEmailVerified = firebaseUser.isEmailVerified
                )

                trySend(AuthResult.Success(user))
            } else {
                trySend(AuthResult.Error("Failed to sign in"))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Unknown error occurred"))
        }

        awaitClose()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun deleteCurrentUser(): Flow<AuthResult<Boolean>> = callbackFlow {
        try {
            trySend(AuthResult.Loading)

            val user = firebaseAuth.currentUser
            if (user != null) {
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .delete()
                        .await()
                } catch (e: Exception) {
                }

                user.delete().await()
                trySend(AuthResult.Success(true))
            } else {
                trySend(AuthResult.Error("No user logged in"))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Error(e.message ?: "Failed to delete user"))
        }

        awaitClose()
    }

    override fun getProfileFlow(): Flow<Result<User>> = callbackFlow {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            trySend(Result.failure(Exception("User not logged in")))
            close()
            return@callbackFlow
        }

        val userListener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        val firebaseUser = firebaseAuth.currentUser
                        val updatedUser = user.copy(
                            uid = userId,
                            isEmailVerified = firebaseUser?.isEmailVerified ?: user.isEmailVerified
                        )
                        trySend(Result.success(updatedUser))
                    } else {
                        trySend(Result.failure(Exception("Failed to parse user data")))
                    }
                } else {
                    trySend(Result.failure(Exception("User data not found")))
                }
            }

        awaitClose { userListener.remove() }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            User(
                uid = it.uid,
                name = it.displayName ?: "",
                email = it.email ?: "",
                isEmailVerified = it.isEmailVerified
            )
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isEmailRegistered(email: String): Boolean {
        return try {
            val signInMethods = firebaseAuth.fetchSignInMethodsForEmail(email).await()
            signInMethods.signInMethods?.isNotEmpty() ?: false
        } catch (e: Exception) {
            false
        }
    }

    override fun sendPasswordResetEmail(email: String): Flow<AuthResult<Unit>> = flow {
        try {
            emit(AuthResult.Loading)

            val methods = firebaseAuth.fetchSignInMethodsForEmail(email).await().signInMethods
            if (methods.isNullOrEmpty()) {
                emit(AuthResult.Error("No account found with this email."))
                return@flow
            }

            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(AuthResult.Success(Unit))

        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Failed to send reset email"))
        }
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
