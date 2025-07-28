package com.example.ecommerce.ui.features.products

import app.cash.turbine.test
import com.example.ecommerce.data.local.entities.ProductEntity
import com.example.ecommerce.data.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val productRepository = mockk<ProductRepository>()
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductViewModel(productRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts updates uiState with success when repository returns products`() = runTest {
        // Given
        val testProducts = listOf(
            ProductEntity("1", "Product 1", "Desc 1", 10.0, "img1", "cat1"),
            ProductEntity("2", "Product 2", "Desc 2", 20.0, "img2", "cat2")
        )
        coEvery { productRepository.getAllProducts() } returns flowOf(testProducts)

        // When & Then
        viewModel.uiState.test {
            // Initial state
            assertEquals(ProductUiState(), awaitItem())
            
            // Loading state
            viewModel.loadProducts()
            assertEquals(ProductUiState(isLoading = true), awaitItem())
            
            // Success state with data
            val successState = awaitItem()
            assertEquals(false, successState.isLoading)
            assertEquals(testProducts, successState.products)
            assertEquals("", successState.errorMessage)
        }
    }

    @Test
    fun `loadProducts updates uiState with error when repository throws exception`() = runTest {
        // Given
        val errorMessage = "Test error"
        coEvery { productRepository.getAllProducts() } throws Exception(errorMessage)

        // When & Then
        viewModel.uiState.test {
            // Initial state
            assertEquals(ProductUiState(), awaitItem())
            
            // Loading state
            viewModel.loadProducts()
            assertEquals(ProductUiState(isLoading = true), awaitItem())
            
            // Error state
            val errorState = awaitItem()
            assertEquals(false, errorState.isLoading)
            assertEquals(errorMessage, errorState.errorMessage)
            assert(errorState.products.isEmpty())
        }
    }
}
