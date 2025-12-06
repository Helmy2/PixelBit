package com.example.pixelbit.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ShopRepositoryImplTest {

    private lateinit var repository: ShopRepositoryImpl
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    @Before
    fun setUp() {
        firebaseAuth = mock()
        firestore = mock()
        collectionReference = mock()
        whenever(firestore.collection(any())).thenReturn(collectionReference)
        repository = ShopRepositoryImpl(firebaseAuth, firestore)
    }

    @Test
    fun `given No User when Get Products then Returns Products Without Favorites`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)
        val querySnapshot: QuerySnapshot = mock()
        val listenerRegistration: ListenerRegistration = mock()
        whenever(querySnapshot.documents).thenReturn(emptyList())

        whenever(collectionReference.addSnapshotListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<EventListener<QuerySnapshot>>(0)
            listener.onEvent(querySnapshot, null)
            listenerRegistration
        }

        // When
        val products = repository.getProducts().first()

        // Then
        assertEquals(emptyList<com.example.pixelbit.domain.model.Product>(), products)
    }

    @Test
    fun `when Get Categories then Returns Empty List On Error`() = runTest {
        // Given
        whenever(collectionReference.get()).thenThrow(RuntimeException("Test exception"))

        // When
        val categories = repository.getCategories()

        // Then
        assertEquals(emptyList<com.example.pixelbit.domain.model.Category>(), categories)
    }

    @Test
    fun `when Get Banners then Returns Empty List On Error`() = runTest {
        // Given
        whenever(collectionReference.get()).thenThrow(RuntimeException("Test exception"))

        // When
        val banners = repository.getBanners()

        // Then
        assertEquals(emptyList<com.example.pixelbit.domain.model.Banner>(), banners)
    }
}