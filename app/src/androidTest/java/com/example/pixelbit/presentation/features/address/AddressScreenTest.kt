package com.example.pixelbit.presentation.features.address

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.repository.AddressRepository
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.OnboardingRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class AddressScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var addressRepository: AddressRepository
    private lateinit var appNavigator: AppNavigator
    private lateinit var viewModel: AddressViewModel

    private lateinit var onboardingRepository: OnboardingRepository
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        addressRepository = mock()
        onboardingRepository = mock()
        authRepository = mock()
        appNavigator = AppNavigator(onboardingRepository, authRepository)
    }

    private fun getString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }

    @Test
    fun givenEmptyAddressList_whenScreenDisplayed_thenEmptyStateVisible() {
        // Given
        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(emptyList())))
        viewModel = AddressViewModel(addressRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                AddressScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText(getString(R.string.no_addresses_found)).assertIsDisplayed()
    }

    @Test
    fun givenAddressesList_whenScreenDisplayed_thenAddressesAreVisible() {
        // Given
        val address1 = Address(id = "1", street = "1St", city = "Cairo", default = true)
        val address2 = Address(id = "2", street = "2St", city = "2City", default = false)
        whenever(addressRepository.getAddresses()).thenReturn(
            flowOf(
                Result.success(
                    listOf(
                        address1,
                        address2
                    )
                )
            )
        )
        viewModel = AddressViewModel(addressRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                AddressScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("1St, Cairo").assertIsDisplayed()
        composeTestRule.onNodeWithText("2St, 2City").assertIsDisplayed()
    }

    @Test
    fun givenScreenDisplayed_whenAddClicked_thenDialogShown() {
        // Given
        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(emptyList())))
        viewModel = AddressViewModel(addressRepository, appNavigator)

        composeTestRule.setContent {
            PixelbitTheme {
                AddressScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription(getString(R.string.add_address)).performClick()

        // Then
        composeTestRule.onNodeWithText(getString(R.string.street)).assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.city)).assertIsDisplayed()
    }

    @Test
    fun givenAddAddressDialog_whenDetailsEnteredAndSaved_thenAddressListUpdated() {
        // Given
        val newAddress = Address(street = "3St", city = "Giza", default = false)

        whenever(addressRepository.getAddresses())
            .thenReturn(flowOf(Result.success(emptyList())))
            .thenReturn(flowOf(Result.success(listOf(newAddress))))

        runBlocking {
            whenever(addressRepository.addAddress(any())).thenReturn(Result.success(Unit))
        }

        viewModel = AddressViewModel(addressRepository, appNavigator)

        composeTestRule.setContent {
            PixelbitTheme {
                AddressScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onNodeWithContentDescription(getString(R.string.add_address)).performClick()

        composeTestRule.onNodeWithText(getString(R.string.street)).performTextInput("3St")
        composeTestRule.onNodeWithText(getString(R.string.city)).performTextInput("Giza")

        composeTestRule.onNodeWithText(getString(R.string.save)).performClick()

        // Then
        composeTestRule.onNodeWithText("3St, Giza").assertIsDisplayed()
    }
}
