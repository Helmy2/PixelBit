package com.example.pixelbit.presentation.features.home

import app.cash.turbine.test
import com.example.pixelbit.domain.model.Banner
import com.example.pixelbit.domain.model.Category
import com.example.pixelbit.domain.model.Product
import com.example.pixelbit.domain.model.User
import com.example.pixelbit.domain.repository.AuthRepository
import com.example.pixelbit.domain.repository.CartRepository
import com.example.pixelbit.domain.repository.FavoritesRepository
import com.example.pixelbit.domain.repository.ShopRepository
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val shopRepository: ShopRepository = mock()
    private val authRepository: AuthRepository = mock()
    private val favoritesRepository: FavoritesRepository = mock()
    private val cartRepository: CartRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val mockUser = User(uid = "1", email = "test@example.com", name = "Test User")
    private val mockProduct1 = Product(
        id = "1",
        title = "Product 1",
        brand = "Brand A",
        price = "100.0",
        images = "image1.jpg",
        category = "dsfa",
        description = "Description",
        isFavorite = false
    )
    private val mockProduct2 = Product(
        id = "2",
        title = "Product 2",
        brand = "Brand B",
        price = "200.0",
        images = "image2.jpg",
        category = "dsfa",
        description = "Description",
        isFavorite = true
    )
    private val mockCategory1 =
        Category(id = "1", title = "Electronics", itemCount = 10, imageUrl = "")
    private val mockCategory2 =
        Category(id = "2", title = "Clothing", itemCount = 10, imageUrl = "")
    private val mockBanner1 = Banner(id = "1", imageUrl = "banner1.jpg")
    private val mockBanner2 = Banner(id = "2", imageUrl = "banner2.jpg")

    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        // Set default mock behaviors for suspend functions
        whenever(shopRepository.getProducts()).thenReturn(flowOf(emptyList()))
        whenever(shopRepository.getCategories()).thenReturn(emptyList())
        whenever(shopRepository.getBanners()).thenReturn(emptyList())
        whenever(authRepository.getCurrentUser()).thenReturn(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state`() {
        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)

        assertThat(viewModel.user.value).isNull()
        assertThat(viewModel.products.value).isEmpty()
        assertThat(viewModel.categories.value).isEmpty()
        assertThat(viewModel.banners.value).isEmpty()
        assertThat(viewModel.loading.value).isFalse()
        assertThat(viewModel.isRefreshing.value).isFalse()
    }

    @Test
    fun `test loadData success loads all data`() = runTest {
        val products = listOf(mockProduct1, mockProduct2)
        val categories = listOf(mockCategory1, mockCategory2)
        val banners = listOf(mockBanner1, mockBanner2)

        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))
        whenever(shopRepository.getCategories()).thenReturn(categories)
        whenever(shopRepository.getBanners()).thenReturn(banners)
        whenever(authRepository.getCurrentUser()).thenReturn(mockUser)

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.products.value).isEqualTo(products)
        assertThat(viewModel.categories.value).isEqualTo(categories)
        assertThat(viewModel.banners.value).isEqualTo(banners)
        assertThat(viewModel.user.value).isEqualTo(mockUser)
        assertThat(viewModel.loading.value).isFalse()
    }

    @Test
    fun `test loadData sets loading state`() = runTest {
        val products = listOf(mockProduct1)
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)

        viewModel.loading.test {
            assertThat(awaitItem()).isFalse() // Initial state
            assertThat(awaitItem()).isTrue()  // Loading started

            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isFalse() // Loading finished
        }
    }

    @Test
    fun `test loadData with isRefresh true sets refreshing state`() = runTest {
        val products = listOf(mockProduct1)
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.isRefreshing.test {
            assertThat(awaitItem()).isFalse() // Initial state

            viewModel.loadData(isRefresh = true)
            assertThat(awaitItem()).isTrue()  // Refreshing started

            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isFalse() // Refreshing finished
        }
    }

    @Test
    fun `test toggleFavorite adds product to favorites`() = runTest {
        val products = listOf(mockProduct1.copy(isFavorite = false))
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite(mockProduct1.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedProduct = viewModel.products.value.first { it.id == mockProduct1.id }
        assertThat(updatedProduct.isFavorite).isTrue()
        verify(favoritesRepository).addToFavorites(mockProduct1.id)
    }

    @Test
    fun `test toggleFavorite removes product from favorites`() = runTest {
        val products = listOf(mockProduct2.copy(isFavorite = true))
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite(mockProduct2.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedProduct = viewModel.products.value.first { it.id == mockProduct2.id }
        assertThat(updatedProduct.isFavorite).isFalse()
        verify(favoritesRepository).removeFromFavorites(mockProduct2.id)
    }

    @Test
    fun `test toggleFavorite reverts on failure`() = runTest {
        val products = listOf(mockProduct1.copy(isFavorite = false))
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))
        whenever(favoritesRepository.addToFavorites(any())).thenThrow(RuntimeException("Network error"))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite(mockProduct1.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val revertedProduct = viewModel.products.value.first { it.id == mockProduct1.id }
        assertThat(revertedProduct.isFavorite).isFalse()
    }

    @Test
    fun `test toggleFavorite optimistic update shows immediately`() = runTest {
        val products = listOf(mockProduct1.copy(isFavorite = false))
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.products.test {
            skipItems(1) // Skip initial state

            viewModel.toggleFavorite(mockProduct1.id)

            // Optimistic update should happen immediately
            val optimisticState = awaitItem()
            assertThat(optimisticState.first().isFavorite).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test toggleFavorite only affects target product`() = runTest {
        val products = listOf(mockProduct1, mockProduct2)
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite(mockProduct1.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val product1 = viewModel.products.value.first { it.id == mockProduct1.id }
        val product2 = viewModel.products.value.first { it.id == mockProduct2.id }

        assertThat(product1.isFavorite).isTrue()
        assertThat(product2.isFavorite).isEqualTo(mockProduct2.isFavorite)
    }

    @Test
    fun `test addToCart success`() = runTest {
        val products = listOf(mockProduct1)
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addToCart(mockProduct1)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(cartRepository).addToCart(
            productId = mockProduct1.id,
            title = mockProduct1.title,
            brand = mockProduct1.brand,
            price = mockProduct1.price,
            images = mockProduct1.images
        )
    }

    @Test
    fun `test addToCart handles exception`() = runTest {
        val products = listOf(mockProduct1)
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))
        whenever(cartRepository.addToCart(any(), any(), any(), any(), any())).thenThrow(
            RuntimeException("Cart error")
        )

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addToCart(mockProduct1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Should not crash, error is caught
        verify(cartRepository).addToCart(any(), any(), any(), any(), any())
    }

    @Test
    fun `test loadData with null user`() = runTest {
        val products = listOf(mockProduct1)
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))
        whenever(authRepository.getCurrentUser()).thenReturn(null)

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.user.value).isNull()
        assertThat(viewModel.products.value).isNotEmpty()
    }

    @Test
    fun `test multiple refresh calls`() = runTest {
        val products = listOf(mockProduct1)
        whenever(shopRepository.getProducts()).thenReturn(flowOf(products))

        viewModel =
            HomeViewModel(shopRepository, authRepository, favoritesRepository, cartRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadData(isRefresh = true)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.isRefreshing.value).isFalse()

        viewModel.loadData(isRefresh = true)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.isRefreshing.value).isFalse()
    }
}
