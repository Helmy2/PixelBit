package com.example.pixelbit.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CartRepositoryImplTest {

    private lateinit var repository: CartRepositoryImpl
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setUp() {
        firebaseAuth = mock()
        firestore = mock()
        repository = CartRepositoryImpl(firebaseAuth, firestore)
        whenever(firebaseAuth.currentUser).thenReturn(null)
    }

    @Test
    fun `given No User when Get Cart Items then Emits Failure`() = runTest {
        // When
        val result = repository.getCartItems().first()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Add To Cart then Returns Error`() = runTest {
        // When
        val result = repository.addToCart("1", "title", "brand", "100", "image")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Update Quantity then Returns Error`() = runTest {
        // When
        val result = repository.updateQuantity("1", 2)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Remove From Cart then Returns Error`() = runTest {
        // When
        val result = repository.removeFromCart("1")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Clear Cart then Returns Error`() = runTest {
        // When
        val result = repository.clearCart()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Get Cart Item Count then Returns Zero`() = runTest {
        // When
        val result = repository.getCartItemCount()

        // Then
        assertEquals(0, result)
    }
}