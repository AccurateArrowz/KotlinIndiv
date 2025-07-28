package com.example.ecommerce.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecommerce.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        // Using an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        productDao = database.productDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetProduct() = runTest {
        // Given
        val product = ProductEntity(
            id = "1",
            name = "Test Product",
            description = "Test Description",
            price = 99.99,
            imageIdentifier = "test_image",
            category = "Test Category"
        )

        // When
        productDao.insertAll(listOf(product))
        val products = productDao.getAllProducts().first()

        // Then
        assertEquals(1, products.size)
        assertEquals("Test Product", products[0].name)
        assertEquals(99.99, products[0].price, 0.001)
    }

    @Test
    fun getProductById() = runTest {
        // Given
        val product = ProductEntity(
            id = "2",
            name = "Another Product",
            description = "Another Description",
            price = 199.99,
            imageIdentifier = "another_image",
            category = "Another Category"
        )
        productDao.insertAll(listOf(product))

        // When
        val retrievedProduct = productDao.getProductById("2").first()

        // Then
        assertNotNull(retrievedProduct)
        assertEquals("Another Product", retrievedProduct?.name)
        assertEquals(199.99, retrievedProduct?.price ?: 0.0, 0.001)
    }
}
