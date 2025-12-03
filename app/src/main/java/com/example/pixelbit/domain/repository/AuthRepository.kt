package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Flow<AuthResult<User>>

    fun sendEmailVerification(): Flow<AuthResult<Boolean>>

    fun checkEmailVerification(): Flow<AuthResult<Boolean>>

    fun signIn(email: String, password: String): Flow<AuthResult<User>>

    suspend fun signOut()

    fun deleteCurrentUser(): Flow<AuthResult<Boolean>>

    fun getCurrentUser(): User?

    fun isUserLoggedIn(): Boolean

    suspend fun login(email: String, pass: String): Result<Unit>
    fun getProfileFlow(): Flow<Result<User>>

    fun sendPasswordResetEmail(email: String): Flow<AuthResult<Unit>>
}
