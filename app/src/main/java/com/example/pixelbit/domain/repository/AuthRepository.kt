package com.example.pixelbit.domain.repository

import com.example.pixelbit.domain.model.AuthResult
import com.example.pixelbit.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Flow<AuthResult<User>>

    suspend fun sendEmailVerification(): Flow<AuthResult<Boolean>>

    suspend fun checkEmailVerification(): Flow<AuthResult<Boolean>>

    suspend fun signIn(email: String, password: String): Flow<AuthResult<User>>

    suspend fun signOut()

    suspend fun deleteCurrentUser(): Flow<AuthResult<Boolean>>

    fun getCurrentUser(): User?

    fun isUserLoggedIn(): Boolean
}

