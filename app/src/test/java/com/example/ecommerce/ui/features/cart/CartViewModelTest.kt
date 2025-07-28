package com.example.ecommerce.ui.features.cart

import app.cash.turbine.test
import com.example.ecommerce.data.local.CartItemWithProduct
import com.example.ecommerce.data.local.entities.CartItemEntity
import com.example.ecommerce.data.local.entities.ProductEntity
import com.example.ecommerce.data.repository.CartRepository
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
class CartViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val cartRepository = mockk<CartRepository>()
    private lateinit var viewModel: CartViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CartViewModel(cartRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCartItems updates uiState with cart items`() = runTest {
        // Given
        val testItems = listOf(
            CartItemWithProduct(
                CartItemEntity("1", "1", 2, 10.0),
                ProductEntity("1", "Product 1", "Desc 1", 10.0, "img1", "cat1")
            )
        )
        coEvery { cartRepository.getCartItemsWithProductDetails() } returns flowOf(testItems)

        // When & Then
        viewModel.uiState.test {
            // Initial state
            assertEquals(CartUiState(), awaitItem())
            
            // Loading state
            viewModel.loadCartItems()
            assertEquals(CartUiState(isLoading = true), awaitItem())
            
            // Success state with data
            val successState = awaitItem()
            assertEquals(false, successState.isLoading)
            assertEquals(1, successState.cartItems.size)
            assertEquals("Product 1", successState.cartItems[0].product.name)
            assertEquals(2, successState.cartItems[0].cartItem.quantity)
            assertEquals("", successState.errorMessage)
        }
    }

    @Test
    fun `updateCartItemQuantity calls repository and reloads items`() = runTest {
        // Given
        val cartItem = CartItemEntity("1", "1", 1, 10.0)
        coEvery { cartRepository.updateCartItemQuantity("1", 2) } returns Unit
        coEvery { cartRepository.getCartItemsWithProductDetails() } returns flowOf(emptyList())

        // When
        viewModel.updateCartItemQuantity(cartItem, 2)

        // Then
        coVerify { cartRepository.updateCartItemQuantity("1", 2) }
        coVerify { cartRepository.getCartItemsWithProductDetails() }
    }

    @Test
    fun `removeCartItem calls repository and reloads items`() = runTest {
        // Given
        val cartItem = CartItemWithProduct(
            CartItemEntity("1", "1", 1, 10.0),
            ProductEntity("1", "Product 1", "Desc 1", 10.0, "img1", "cat1")
        )
        coEvery { cartRepository.removeCartItem("1") } returns Unit
        coEvery { cartRepository.getCartItemsWithProductDetails() } returns flowOf(emptyList())

        // When
        viewModel.removeCartItem(cartItem)

        // Then
        coVerify { cartRepository.removeCartItem("1") }
        coVerify { cartRepository.getCartItemsWithProductDetails() }
    }
}
