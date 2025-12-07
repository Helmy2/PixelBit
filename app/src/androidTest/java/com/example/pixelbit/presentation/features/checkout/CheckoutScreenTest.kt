package com.example.pixelbit.presentation.features.checkout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.repository.AddressRepository
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.CheckoutRepository
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
class CheckoutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var checkoutRepository: CheckoutRepository
    private lateinit var addressRepository: AddressRepository
    private lateinit var onboardingRepository: OnboardingRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var appNavigator: AppNavigator
    private lateinit var viewModel: CheckoutViewModel

    @Before
    fun setUp() {
        checkoutRepository = mock()
        addressRepository = mock()
        onboardingRepository = mock()
        authRepository = mock()
        appNavigator = AppNavigator(onboardingRepository, authRepository)
    }

    private fun getString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }

    @Test
    fun givenEmptyCart_whenScreenDisplayed_thenEmptyStateVisible() {
        // Given
        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(emptyList())))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(emptyList())))
        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                CheckoutScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText(getString(R.string.your_cart_is_empty)).assertIsDisplayed()
    }

    @Test
    fun givenCartItems_whenScreenDisplayed_thenItemsAndSummaryVisible() {
        // Given
        val item = CartItem(
            id = "1",
            title = "Samsung A54",
            brand = "Samsung",
            price = "1000.0",
            quantity = 1
        )
        val address = Address(street = "1St", city = "Cairo", default = true)

        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(listOf(address))))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(listOf(item))))
        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                CheckoutScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Samsung A54").assertIsDisplayed()
        composeTestRule.onNodeWithText("Samsung").assertIsDisplayed()
        composeTestRule.onNodeWithText("1St, Cairo").assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.subtotal)).assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.shipping)).assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.total)).assertIsDisplayed()
    }

    @Test
    fun givenValidOrder_whenPlaceOrderClicked_thenOrderPlacedSuccessfully() {
        // Given
        val item = CartItem(
            id = "1",
            title = "Samsung A54",
            price = "1000.0",
            quantity = 1
        )
        val address = Address(street = "1St", city = "Cairo", default = true)

        whenever(addressRepository.getAddresses()).thenReturn(flowOf(Result.success(listOf(address))))
        whenever(checkoutRepository.getCheckoutItems()).thenReturn(flowOf(Result.success(listOf(item))))

        runBlocking {
            whenever(checkoutRepository.placeOrder(any(), any())).thenReturn(Result.success(Unit))
        }

        viewModel = CheckoutViewModel(checkoutRepository, addressRepository, appNavigator)

        composeTestRule.setContent {
            PixelbitTheme {
                CheckoutScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onNodeWithText(getString(R.string.place_order)).performClick()

        // Then
        composeTestRule.onNodeWithText(getString(R.string.order_placed_successfully))
            .assertIsDisplayed()
    }
}
