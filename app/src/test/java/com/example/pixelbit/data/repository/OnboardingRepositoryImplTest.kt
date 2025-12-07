package com.example.pixelbit.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.example.pixelbit.R
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class OnboardingRepositoryImplTest {

    private lateinit var repository: OnboardingRepositoryImpl
    private val dataStore: DataStore<Preferences> = mock()
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    @Before
    fun setUp() {
        repository = OnboardingRepositoryImpl(dataStore)
    }

    @Test
    fun `test getOnboardingItems returns three items`() = runTest {
        val items = repository.getOnboardingItems()

        assertThat(items).hasSize(3)
    }

    @Test
    fun `test getOnboardingItems first item has correct data`() = runTest {
        val items = repository.getOnboardingItems()
        val firstItem = items[0]

        assertThat(firstItem.id).isEqualTo(1)
        assertThat(firstItem.title).isEqualTo("Discover Amazing Products")
        assertThat(firstItem.description).isEqualTo("Browse through thousands of high-quality products")
        assertThat(firstItem.imageRes).isEqualTo(R.drawable.onboarding_1)
        assertThat(firstItem.isLastItem).isFalse()
    }

    @Test
    fun `test getOnboardingItems second item has correct data`() = runTest {
        val items = repository.getOnboardingItems()
        val secondItem = items[1]

        assertThat(secondItem.id).isEqualTo(2)
        assertThat(secondItem.title).isEqualTo("Easy & Secure Payment")
        assertThat(secondItem.description).isEqualTo("Multiple payment options with complete security")
        assertThat(secondItem.imageRes).isEqualTo(R.drawable.onboarding_2)
        assertThat(secondItem.isLastItem).isFalse()
    }

    @Test
    fun `test getOnboardingItems third item has correct data`() = runTest {
        val items = repository.getOnboardingItems()
        val thirdItem = items[2]

        assertThat(thirdItem.id).isEqualTo(3)
        assertThat(thirdItem.title).isEqualTo("Fast Delivery")
        assertThat(thirdItem.description).isEqualTo("Get your orders delivered quickly to your doorstep")
        assertThat(thirdItem.imageRes).isEqualTo(R.drawable.onboarding_3)
        assertThat(thirdItem.isLastItem).isTrue()
    }

    @Test
    fun `test getOnboardingItems only last item has isLastItem true`() = runTest {
        val items = repository.getOnboardingItems()

        assertThat(items[0].isLastItem).isFalse()
        assertThat(items[1].isLastItem).isFalse()
        assertThat(items[2].isLastItem).isTrue()
    }

    @Test
    fun `test getOnboardingItems returns items in correct order`() = runTest {
        val items = repository.getOnboardingItems()

        assertThat(items[0].id).isEqualTo(1)
        assertThat(items[1].id).isEqualTo(2)
        assertThat(items[2].id).isEqualTo(3)
    }

    @Test
    fun `test getOnboardingItems all items have unique ids`() = runTest {
        val items = repository.getOnboardingItems()
        val ids = items.map { it.id }

        assertThat(ids).containsNoDuplicates()
    }

    @Test
    fun `test getOnboardingItems all items have non-empty titles`() = runTest {
        val items = repository.getOnboardingItems()

        items.forEach { item ->
            assertThat(item.title).isNotEmpty()
        }
    }

    @Test
    fun `test getOnboardingItems all items have non-empty descriptions`() = runTest {
        val items = repository.getOnboardingItems()

        items.forEach { item ->
            assertThat(item.description).isNotEmpty()
        }
    }

    @Test
    fun `test getOnboardingItems returns consistent data on multiple calls`() = runTest {
        val items1 = repository.getOnboardingItems()
        val items2 = repository.getOnboardingItems()

        assertThat(items1).hasSize(items2.size)
        assertThat(items1[0].id).isEqualTo(items2[0].id)
        assertThat(items1[1].id).isEqualTo(items2[1].id)
        assertThat(items1[2].id).isEqualTo(items2[2].id)
    }

    @Test
    fun `test getOnboardingItems all items have valid image resources`() = runTest {
        val items = repository.getOnboardingItems()

        items.forEach { item ->
            assertThat(item.imageRes).isGreaterThan(0)
        }
    }

    @Test
    fun `test getOnboardingItems each item has unique image resource`() = runTest {
        val items = repository.getOnboardingItems()
        val imageResources = items.map { it.imageRes }

        assertThat(imageResources).containsNoDuplicates()
    }

    @Test
    fun `test getOnboardingItems titles are descriptive`() = runTest {
        val items = repository.getOnboardingItems()

        items.forEach { item ->
            assertThat(item.title.length).isAtLeast(10)
        }
    }

    @Test
    fun `test getOnboardingItems descriptions are detailed`() = runTest {
        val items = repository.getOnboardingItems()

        items.forEach { item ->
            assertThat(item.description.length).isAtLeast(20)
        }
    }

    @Test
    fun `test getOnboardingItems returns list not array`() = runTest {
        val items = repository.getOnboardingItems()

        assertThat(items).isInstanceOf(List::class.java)
    }

    @Test
    fun `test first two items are not marked as last`() = runTest {
        val items = repository.getOnboardingItems()

        val nonLastItems = items.take(2)
        nonLastItems.forEach { item ->
            assertThat(item.isLastItem).isFalse()
        }
    }

    @Test
    fun `test exactly one item is marked as last`() = runTest {
        val items = repository.getOnboardingItems()
        val lastItems = items.filter { it.isLastItem }

        assertThat(lastItems).hasSize(1)
        assertThat(lastItems.first()).isEqualTo(items.last())
    }
}
