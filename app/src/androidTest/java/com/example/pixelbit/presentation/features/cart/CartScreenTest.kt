package com.example.pixelbit.presentation.features.cart

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.pixelbit.domain.model.CartItem
import org.junit.Rule
import org.junit.Test

class CartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenEmptyCart_whenScreenLoaded_thenEmptyViewIsDisplayed() {
        // Given
        val emptyState = CartUiState(
            cartItems = emptyList(),
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            CartContent(
                uiState = emptyState,
                onQuantityIncrease = {},
                onQuantityDecrease = {},
                onRemoveItem = {},
                onCheckoutClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Your Cart is Empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add products to your cart to see them here").assertIsDisplayed()
    }

    @Test
    fun givenCartItems_whenScreenLoaded_thenItemsAndSummaryDisplayed() {
        // Given
        val cartItem = CartItem(
            id = "1",
            title = "Test Product",
            price = "100.0",
            quantity = 1,
            images = ""
        )
        val state = CartUiState(
            cartItems = listOf(cartItem),
            isLoading = false,
            subtotal = 100.0,
            shipping = 5.0,
            total = 105.0
        )

        // When
        composeTestRule.setContent {
            CartContent(
                uiState = state,
                onQuantityIncrease = {},
                onQuantityDecrease = {},
                onRemoveItem = {},
                onCheckoutClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Test Product").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("$100.0").assertCountEquals(2)
        composeTestRule.onAllNodesWithText("$100.0").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("Subtotal").assertIsDisplayed()
        composeTestRule.onNodeWithText("$105.0").assertIsDisplayed()
    }

    @Test
    fun givenCartWithItems_whenCheckoutClicked_thenCallbackInvoked() {
        // Given
        var checkoutClicked = false
        val state = CartUiState(
            cartItems = listOf(
                CartItem(id = "1", title = "Product", price = "50.0")
            ),
            isLoading = false,
            total = 55.0
        )

        // When
        composeTestRule.setContent {
            CartContent(
                uiState = state,
                onQuantityIncrease = {},
                onQuantityDecrease = {},
                onRemoveItem = {},
                onCheckoutClick = { checkoutClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Proceed to Checkout").performClick()

        // Then
        assert(checkoutClicked)
    }
}
