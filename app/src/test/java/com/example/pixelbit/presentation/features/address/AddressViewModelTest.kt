package com.example.pixelbit.presentation.features.address

import app.cash.turbine.test
import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.repository.AddressRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AddressViewModelTest {

    private lateinit var viewModel: AddressViewModel
    private val addressRepository: AddressRepository = mock()
    private val appNavigator: AppNavigator = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(emptyList())))
        viewModel = AddressViewModel(addressRepository, appNavigator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.addresses).isEmpty()
            assertThat(initialState.isLoading).isFalse()
            assertThat(initialState.errorMessage).isNull()
            assertThat(initialState.isDialogShown).isFalse()
            assertThat(initialState.selectedAddress).isNull()
        }
    }


    @Test
    fun `test load addresses success`() = runTest {
        val addresses = listOf(Address(id = "1", street = "123 Main St"))
        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(addresses)))

        // Create ViewModel - this triggers init block
        val testViewModel = AddressViewModel(addressRepository, appNavigator)

        // Advance dispatcher to execute the coroutine in init block
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.uiState.test {
            val updatedState = awaitItem()
            assertThat(updatedState.addresses).isEqualTo(addresses)
            assertThat(updatedState.isLoading).isFalse()
        }
    }

    @Test
    fun `test load addresses failure`() = runTest {
        val errorMessage = "Failed to load addresses"
        whenever(addressRepository.getAddresses()).thenReturn(
            flowOf(
                Result.failure(
                    Exception(
                        errorMessage
                    )
                )
            )
        )

        // Create ViewModel - this triggers init block
        val testViewModel = AddressViewModel(addressRepository, appNavigator)

        // Advance dispatcher to execute the coroutine in init block
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.uiState.test {
            val errorState = awaitItem()
            assertThat(errorState.errorMessage).isEqualTo(errorMessage)
            assertThat(errorState.isLoading).isFalse()
        }
    }


    @Test
    fun `test onAddAddressClicked opens dialog`() {
        viewModel.onAddAddressClicked()
        assertThat(viewModel.uiState.value.isDialogShown).isTrue()
        assertThat(viewModel.uiState.value.selectedAddress).isNull()
    }

    @Test
    fun `test onEditAddressClicked opens dialog with address`() {
        val address = Address(id = "1", street = "123 Main St")
        viewModel.onEditAddressClicked(address)
        assertThat(viewModel.uiState.value.isDialogShown).isTrue()
        assertThat(viewModel.uiState.value.selectedAddress).isEqualTo(address)
    }

    @Test
    fun `test onDialogDismissed closes dialog`() {
        viewModel.onDialogDismissed()
        assertThat(viewModel.uiState.value.isDialogShown).isFalse()
        assertThat(viewModel.uiState.value.selectedAddress).isNull()
    }

    @Test
    fun `test onAddressSaved adds new address`() = runTest {
        val newAddress = Address(street = "456 Oak Ave")
        whenever(addressRepository.addAddress(newAddress)).thenReturn(Result.success(Unit))

        viewModel.onAddressSaved(newAddress)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(addressRepository).addAddress(newAddress)
        assertThat(viewModel.uiState.value.isDialogShown).isFalse()
    }

    @Test
    fun `test onAddressSaved updates existing address`() = runTest {
        val existingAddress = Address(id = "1", street = "123 Main St")
        viewModel.onEditAddressClicked(existingAddress)
        whenever(addressRepository.updateAddress(existingAddress)).thenReturn(Result.success(Unit))

        viewModel.onAddressSaved(existingAddress)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(addressRepository).updateAddress(existingAddress)
        assertThat(viewModel.uiState.value.isDialogShown).isFalse()
    }

    @Test
    fun `test onDeleteAddressClicked`() = runTest {
        val addressId = "1"
        whenever(addressRepository.deleteAddress(addressId)).thenReturn(Result.success(Unit))

        viewModel.onDeleteAddressClicked(addressId)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(addressRepository).deleteAddress(addressId)
    }

    @Test
    fun `test onSetDefaultAddressClicked`() = runTest {
        val addressId = "1"
        whenever(addressRepository.setDefaultAddress(addressId)).thenReturn(Result.success(Unit))

        viewModel.onSetDefaultAddressClicked(addressId)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(addressRepository).setDefaultAddress(addressId)
    }

    @Test
    fun `test onBackClicked`() {
        viewModel.onBackClicked()
        verify(appNavigator).back()
    }
}
