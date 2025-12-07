package com.example.pixelbit.data.repository

import app.cash.turbine.test
import com.example.pixelbit.domain.model.AuthResult
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.google.firebase.auth.AuthResult as FirebaseAuthResult

@ExperimentalCoroutinesApi
class AuthRepositoryImplTest {

    private lateinit var repository: AuthRepositoryImpl
    private val firebaseAuth: FirebaseAuth = mock()
    private val firestore: FirebaseFirestore = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val firebaseAuthResult: FirebaseAuthResult = mock()
    private val documentReference: DocumentReference = mock()
    private val documentSnapshot: DocumentSnapshot = mock()
    private val signInMethodQueryResult: SignInMethodQueryResult = mock()

    private val testEmail = "test@example.com"
    private val testPassword = "password123"
    private val testName = "Test User"
    private val testPhone = "+1234567890"
    private val testUid = "test-uid-123"

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(firebaseAuth, firestore)
    }

    // Helper function to create successful Task
    private fun <T> successTask(result: T): Task<T> = Tasks.forResult(result)

    // Helper function to create failed Task
    private fun <T> failureTask(exception: Exception): Task<T> = Tasks.forException(exception)

    @Test
    fun `test signOut calls firebase auth signOut`() = runTest {
        repository.signOut()

        verify(firebaseAuth).signOut()
    }

    @Test
    fun `test getCurrentUser returns user when logged in`() {
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(testUid)
        whenever(firebaseUser.displayName).thenReturn(testName)
        whenever(firebaseUser.email).thenReturn(testEmail)
        whenever(firebaseUser.isEmailVerified).thenReturn(true)

        val user = repository.getCurrentUser()

        assertThat(user).isNotNull()
        assertThat(user?.uid).isEqualTo(testUid)
        assertThat(user?.name).isEqualTo(testName)
        assertThat(user?.email).isEqualTo(testEmail)
        assertThat(user?.isEmailVerified).isTrue()
    }

    @Test
    fun `test getCurrentUser returns null when not logged in`() {
        whenever(firebaseAuth.currentUser).thenReturn(null)

        val user = repository.getCurrentUser()

        assertThat(user).isNull()
    }

    @Test
    fun `test isUserLoggedIn returns true when user exists`() {
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        val isLoggedIn = repository.isUserLoggedIn()

        assertThat(isLoggedIn).isTrue()
    }

    @Test
    fun `test isUserLoggedIn returns false when user is null`() {
        whenever(firebaseAuth.currentUser).thenReturn(null)

        val isLoggedIn = repository.isUserLoggedIn()

        assertThat(isLoggedIn).isFalse()
    }

    @Test
    fun `test login success returns success result`() = runTest {
        whenever(firebaseAuth.signInWithEmailAndPassword(testEmail, testPassword))
            .thenReturn(successTask(firebaseAuthResult))

        val result = repository.login(testEmail, testPassword)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `test login failure returns failure result`() = runTest {
        val exception = Exception("Login failed")
        whenever(firebaseAuth.signInWithEmailAndPassword(testEmail, testPassword))
            .thenReturn(failureTask(exception))

        val result = repository.login(testEmail, testPassword)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `test isEmailRegistered returns true when email exists`() = runTest {
        whenever(signInMethodQueryResult.signInMethods).thenReturn(listOf("password"))
        whenever(firebaseAuth.fetchSignInMethodsForEmail(testEmail))
            .thenReturn(successTask(signInMethodQueryResult))

        val isRegistered = repository.isEmailRegistered(testEmail)

        assertThat(isRegistered).isTrue()
    }

    @Test
    fun `test isEmailRegistered returns false when email does not exist`() = runTest {
        whenever(signInMethodQueryResult.signInMethods).thenReturn(emptyList())
        whenever(firebaseAuth.fetchSignInMethodsForEmail(testEmail))
            .thenReturn(successTask(signInMethodQueryResult))

        val isRegistered = repository.isEmailRegistered(testEmail)

        assertThat(isRegistered).isFalse()
    }

    @Test
    fun `test isEmailRegistered returns false on exception`() = runTest {
        whenever(firebaseAuth.fetchSignInMethodsForEmail(testEmail))
            .thenReturn(failureTask(Exception("Network error")))

        val isRegistered = repository.isEmailRegistered(testEmail)

        assertThat(isRegistered).isFalse()
    }

    @Test
    fun `test sendPasswordResetEmail success`() = runTest {
        whenever(signInMethodQueryResult.signInMethods).thenReturn(listOf("password"))
        whenever(firebaseAuth.fetchSignInMethodsForEmail(testEmail))
            .thenReturn(successTask(signInMethodQueryResult))
        whenever(firebaseAuth.sendPasswordResetEmail(testEmail))
            .thenReturn(successTask(null))

        repository.sendPasswordResetEmail(testEmail).test {
            assertThat(awaitItem()).isInstanceOf(AuthResult.Loading::class.java)

            val success = awaitItem()
            assertThat(success).isInstanceOf(AuthResult.Success::class.java)

            awaitComplete()
        }
    }

    @Test
    fun `test sendPasswordResetEmail with unregistered email returns error`() = runTest {
        whenever(signInMethodQueryResult.signInMethods).thenReturn(emptyList())
        whenever(firebaseAuth.fetchSignInMethodsForEmail(testEmail))
            .thenReturn(successTask(signInMethodQueryResult))

        repository.sendPasswordResetEmail(testEmail).test {
            assertThat(awaitItem()).isInstanceOf(AuthResult.Loading::class.java)

            val error = awaitItem()
            assertThat(error).isInstanceOf(AuthResult.Error::class.java)
            assertThat((error as AuthResult.Error).message).contains("No account found")

            awaitComplete()
        }
    }
}
