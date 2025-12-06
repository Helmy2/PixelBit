package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Address
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
class CheckoutRepositoryImplTest {

    private lateinit var repository: CheckoutRepositoryImpl
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setUp() {
        firebaseAuth = mock()
        firestore = mock()
        repository = CheckoutRepositoryImpl(firestore, firebaseAuth)
        whenever(firebaseAuth.currentUser).thenReturn(null)
    }

    @Test
    fun `given No User when Get Checkout Items then Emits Failure`() = runTest {
        // When
        val result = repository.getCheckoutItems().first()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Place Order then Returns Error`() = runTest {
        // Given
        val address = Address(id = "1", street = "Street 1", city = "City 1")

        // When
        val result = repository.placeOrder(emptyList(), address)

        // Then
        assertTrue(result.isFailure)
    }
}