package com.example.pixelbit.presentation.features.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.model.User
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.CartRepository
import com.example.pixelbit.domain.repository.FavoritesRepository
import com.example.pixelbit.domain.repository.ShopRepository
import com.example.pixelbit.presentation.theme.PixelbitTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var shopRepository: ShopRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        shopRepository = mock()
        authRepository = mock()
        favoritesRepository = mock()
        cartRepository = mock()
    }

    @Test
    fun given_home_screen_when_displayed_then_banners_are_visible() {
        // Given
        val banners = listOf(Banner("1", "https://example.com/image.jpg"))
        
        runBlocking {
            whenever(shopRepository.getProducts()).thenReturn(flowOf(emptyList()))
            whenever(shopRepository.getBanners()).thenReturn(banners)
            whenever(shopRepository.getCategories()).thenReturn(emptyList())
            whenever(authRepository.getCurrentUser()).thenReturn(User("1", "Ibrahim Mohamed", "Ibrahim@gmail.com"))
        }
        
        viewModel = HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Ibrahim Mohamed").assertIsDisplayed()
    }

    @Test
    fun given_products_when_displayed_then_product_title_is_visible() {
         // Given
        val product = Product(
            id = "1",
            title = "Test Product",
            category = "Electronics",
            brand = "Brand",
            price = "100",
            images = "https://example.com/image.jpg",
            description = "Description",
            isFavorite = false
        )
        
        runBlocking {
            whenever(shopRepository.getProducts()).thenReturn(flowOf(listOf(product)))
            whenever(shopRepository.getBanners()).thenReturn(emptyList())
            whenever(shopRepository.getCategories()).thenReturn(emptyList())
            whenever(authRepository.getCurrentUser()).thenReturn(null)
        }
        
        viewModel = HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                HomeScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test Product").assertIsDisplayed()
    }

    @Test
    fun given_categories_when_tab_selected_then_category_is_visible() {
         // Given
        val category = Category(id = "1", title = "Electronics", itemCount = 10, imageUrl = "https://example.com/cat.jpg")
        
        runBlocking {
            whenever(shopRepository.getProducts()).thenReturn(flowOf(emptyList()))
            whenever(shopRepository.getBanners()).thenReturn(emptyList())
            whenever(shopRepository.getCategories()).thenReturn(listOf(category))
            whenever(authRepository.getCurrentUser()).thenReturn(null)
        }
        
        viewModel = HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)

        // When
        composeTestRule.setContent {
            PixelbitTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
        
        composeTestRule.onNodeWithText("Category").performClick()

        // Then
        composeTestRule.onNodeWithText("Electronics").assertIsDisplayed()
    }
}
