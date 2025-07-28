package com.example.ecommerce.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.data.local.entities.CartItemEntity
import com.example.ecommerce.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var cartDao: CartDao
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        cartDao = database.cartDao()
        productDao = database.productDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetCartItem() = runTest {
        // Given
        val product = ProductEntity("1", "Test Product", "Test Desc", 10.0, "test_img", "test_cat")
        val cartItem = CartItemEntity("1", "1", 2, 20.0)

        // When
        productDao.insertAll(listOf(product))
        cartDao.insert(cartItem)
        val cartItems = cartDao.getCartItems().first()

        // Then
        assertEquals(1, cartItems.size)
        assertEquals("1", cartItems[0].productId)
        assertEquals(2, cartItems[0].quantity)
    }

    @Test
    fun getCartItemsWithProductDetails() = runTest {
        // Given
        val product = ProductEntity("1", "Test Product", "Test Desc", 10.0, "test_img", "test_cat")
        val cartItem = CartItemEntity("1", "1", 2, 20.0)

        // When
        productDao.insertAll(listOf(product))
        cartDao.insert(cartItem)
        val cartItemsWithProducts = cartDao.getCartItemsWithProductDetails().first()

        // Then
        assertEquals(1, cartItemsWithProducts.size)
        assertEquals("Test Product", cartItemsWithProducts[0].product.name)
        assertEquals(2, cartItemsWithProducts[0].cartItem.quantity)
    }

    @Test
    fun updateCartItemQuantity() = runTest {
        // Given
        val product = ProductEntity("1", "Test Product", "Test Desc", 10.0, "test_img", "test_cat")
        val cartItem = CartItemEntity("1", "1", 2, 20.0)

        // When
        productDao.insertAll(listOf(product))
        cartDao.insert(cartItem)
        cartDao.updateQuantity("1", 5)
        val updatedItem = cartDao.getCartItemById("1").first()

        // Then
        assertNotNull(updatedItem)
        assertEquals(5, updatedItem?.quantity)
    }

    @Test
    fun deleteCartItem() = runTest {
        // Given
        val product = ProductEntity("1", "Test Product", "Test Desc", 10.0, "test_img", "test_cat")
        val cartItem = CartItemEntity("1", "1", 2, 20.0)

        // When
        productDao.insertAll(listOf(product))
        cartDao.insert(cartItem)
        cartDao.delete("1")
        val cartItems = cartDao.getCartItems().first()

        // Then
        assertTrue(cartItems.isEmpty())
    }
}
