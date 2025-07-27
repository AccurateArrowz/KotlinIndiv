package com.example.ecommerce.data.repository

import com.example.ecommerce.data.local.ProductDao
import com.example.ecommerce.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getProductById(productId: String): Flow<ProductEntity?> = 
        productDao.getProductById(productId)

    suspend fun insertInitialProducts(products: List<ProductEntity>) {
        productDao.insertAll(products)
    }
}