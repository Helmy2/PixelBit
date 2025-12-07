package com.example.pixelbit.presentation.features.myorders

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.Address
import com.example.pixelbit.domain.model.CartItem
import com.example.pixelbit.domain.model.Order
import com.example.pixelbit.domain.model.OrderStatus
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.OnboardingRepository
import com.example.pixelbit.domain.repository.OrderRepository
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class MyOrdersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var orderRepository: OrderRepository
    private lateinit var appNavigator: AppNavigator
    private lateinit var onboardingRepository: OnboardingRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: MyOrdersViewModel

    @Before
    fun setUp() {
        orderRepository = mock()
        onboardingRepository = mock()
        authRepository = mock()
        appNavigator = AppNavigator(onboardingRepository, authRepository)
    }

    private fun getString(id: Int, vararg args: Any): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
    }

    @Test
    fun givenEmptyOrderList_whenScreenDisplayed_thenEmptyStateVisible() {
        // Given
        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(emptyList())))
        viewModel = MyOrdersViewModel(orderRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                MyOrdersScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText(getString(R.string.no_orders_yet)).assertIsDisplayed()
    }

    @Test
    fun givenOrderList_whenScreenDisplayed_thenOrdersAreVisible() {
        // Given
        val timestamp = System.currentTimeMillis()
        val formattedDate =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
        val item1 = CartItem(
            id = "item1",
            title = "Product 1",
            brand = "Brand A",
            price = "10.0",
            quantity = 1,
            images = ""
        )
        val order1 = Order(
            id = "ORDER123",
            items = listOf(item1),
            timestamp = timestamp,
            status = OrderStatus.PENDING,
            address = Address(street = "1St", city = "Cairo")
        )

        whenever(orderRepository.getOrders()).thenReturn(flowOf(Result.success(listOf(order1))))
        viewModel = MyOrdersViewModel(orderRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                MyOrdersScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText(getString(R.string.order_number, "ORDER123"))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("PENDING").assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.placed_on, formattedDate))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("1St, Cairo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Product 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Brand A").assertIsDisplayed()
        composeTestRule.onNodeWithText(getString(R.string.total_price, 10.0)).assertIsDisplayed()
    }

    @Test
    fun givenErrorLoadingOrders_whenScreenDisplayed_thenErrorMessageVisible() {
        // Given
        val errorMessage = "Network Error"
        whenever(orderRepository.getOrders()).thenReturn(
            flowOf(
                Result.failure(
                    Exception(
                        errorMessage
                    )
                )
            )
        )
        viewModel = MyOrdersViewModel(orderRepository, appNavigator)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                MyOrdersScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
