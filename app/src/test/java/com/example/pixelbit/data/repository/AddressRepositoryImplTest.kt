package com.example.pixelbit.data.repository

import com.example.pixelbit.domain.model.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AddressRepositoryImplTest {

    private lateinit var repository: AddressRepositoryImpl
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser
    private lateinit var collectionReference: CollectionReference
    private lateinit var documentReference: DocumentReference
    private lateinit var query: Query
    private lateinit var querySnapshot: QuerySnapshot

    @Before
    fun setUp() {
        firestore = mock()
        firebaseAuth = mock()
        mockUser = mock()
        collectionReference = mock()
        documentReference = mock()
        query = mock()
        querySnapshot = mock()

        whenever(firebaseAuth.currentUser).thenReturn(mockUser)
        whenever(mockUser.uid).thenReturn("test_user")
        whenever(firestore.collection(any())).thenReturn(collectionReference)
        whenever(collectionReference.document(any())).thenReturn(documentReference)
        whenever(documentReference.collection(any())).thenReturn(collectionReference)

        repository = AddressRepositoryImpl(firestore, firebaseAuth)
    }

    @Test
    fun `given No User when Add Address then Returns Error`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)
        val address = Address(street = "Street 1", city = "City 1")

        // When
        val result = repository.addAddress(address)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Update Address then Returns Error`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)
        val address = Address(id = "1", street = "Street 1", city = "City 1")

        // When
        val result = repository.updateAddress(address)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Delete Address then Returns Error`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = repository.deleteAddress("1")

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `given No User when Set Default Address then Returns Error`() = runTest {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val result = repository.setDefaultAddress("1")

        // Then
        assertTrue(result.isFailure)
    }
}