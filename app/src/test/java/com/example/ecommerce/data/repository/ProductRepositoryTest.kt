package com.example.ecommerce.data.repository

import app.cash.turbine.test
import com.example.ecommerce.data.local.ProductDao
import com.example.ecommerce.data.local.entities.ProductEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProductRepositoryTest {
    private val productDao = mockk<ProductDao>()
    private val repository = ProductRepository(productDao)

    @Test
    fun `getAllProducts returns flow of products`() = runTest {
        // Given
        val testProducts = listOf(
            ProductEntity("1", "Product 1", "Desc 1", 10.0, "img1", "cat1"),
            ProductEntity("2", "Product 2", "Desc 2", 20.0, "img2", "cat2")
        )
        coEvery { productDao.getAllProducts() } returns flowOf(testProducts)

        // When & Then
        repository.getAllProducts().test {
            assertEquals(testProducts, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getProductById returns correct product`() = runTest {
        // Given
        val testProduct = ProductEntity("1", "Test Product", "Test Desc", 15.0, "test_img", "test_cat")
        coEvery { productDao.getProductById("1") } returns flowOf(testProduct)

        // When & Then
        repository.getProductById("1").test {
            assertEquals(testProduct, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `insertInitialProducts calls dao insertAll`() = runTest {
        // Given
        val testProducts = listOf(
            ProductEntity("1", "P1", "D1", 10.0, "I1", "C1"),
            ProductEntity("2", "P2", "D2", 20.0, "I2", "C2")
        )
        coEvery { productDao.insertAll(testProducts) } returns Unit

        // When
        repository.insertInitialProducts(testProducts)

        // Then
        coVerify { productDao.insertAll(testProducts) }
    }
}
