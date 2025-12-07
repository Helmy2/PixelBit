package com.example.pixelbit.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class FavoritesRepositoryImplTest {

    private lateinit var repository: FavoritesRepositoryImpl
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mockUser: FirebaseUser

    @Before
    fun setUp() {
        firebaseAuth = mock()
        firestore = mock()
        mockUser = mock()
        whenever(mockUser.uid).thenReturn("test_user")
        whenever(firebaseAuth.currentUser).thenReturn(mockUser)
        repository = FavoritesRepositoryImpl(firebaseAuth, firestore)
    }

    @Test
    fun `given No User when Add To Favorites then Returns Error`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = repository.addToFavorites("product_1")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Remove From Favorites then Returns Error`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = repository.removeFromFavorites("product_1")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Is Favorite then Returns False`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = repository.isFavorite("product_1")

        // Then
        assertFalse(result)
    }
}

