package com.example.pixelbit.presentation.features.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixelbit.R
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.repository.FavoritesRepository
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var repository: FavoritesRepository
    private lateinit var viewModel: FavoritesViewModel

    private val testProduct = Product(
        id = "1", title = "Gaming Laptop", category = "Electronics",
        brand = "Lenovo", price = "$1000.00", images = "laptop.jpg",
        description = "Gaming laptop", isFavorite = true
    )

    @Before
    fun setUp() {
        repository = mock()
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.success(emptyList())))
        viewModel = FavoritesViewModel(repository)
    }

    private fun getString(id: Int) =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun givenFavoritesScreen_whenDisplayed_thenTitleIsVisible() {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.success(emptyList())))

        // When
        composeTestRule.setContent {
            PixelbitTheme { FavoritesScreen(viewModel = viewModel) }
        }

        // Then
        composeTestRule.onNodeWithText(getString(R.string.my_favorites)).assertIsDisplayed()
    }

    @Test
    fun givenNoFavorites_whenScreenLoaded_thenEmptyStateIsDisplayed() {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.success(emptyList())))

        // When
        composeTestRule.setContent {
            PixelbitTheme { FavoritesScreen(viewModel = viewModel) }
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(getString(R.string.no_favorites_yet)).assertIsDisplayed()
    }

    @Test
    fun givenOneFavorite_whenScreenLoaded_thenProductIsDisplayed() {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.success(listOf(testProduct))))
        viewModel = FavoritesViewModel(repository)

        // When
        composeTestRule.setContent {
            PixelbitTheme { FavoritesScreen(viewModel = viewModel) }
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Gaming Laptop").assertIsDisplayed()
    }

    @Test
    fun givenError_whenLoadingFails_thenErrorIsDisplayedInSnackbar() {
        // Given
        whenever(repository.getFavoriteProducts()).thenReturn(flowOf(Result.failure(Exception("Error"))))
        viewModel = FavoritesViewModel(repository)

        // When
        composeTestRule.setContent {
            PixelbitTheme { FavoritesScreen(viewModel = viewModel) }
        }

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
    }
}

