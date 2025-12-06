package com.example.pixelbit.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class OrderRepositoryImplTest {

    private lateinit var repository: OrderRepositoryImpl
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setUp() {
        firebaseAuth = mock()
        firestore = mock()
        repository = OrderRepositoryImpl(firestore, firebaseAuth)
        whenever(firebaseAuth.currentUser).thenReturn(null)
    }

    @Test
    fun `given No User when Get Orders then Emits Failure`() = runTest {
        // When
        val result = repository.getOrders().first()

        // Then
        assertTrue(result.isFailure)
    }
}