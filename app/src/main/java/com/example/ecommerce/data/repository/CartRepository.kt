package com.example.ecommerce.data.repository

import com.example.ecommerce.data.local.CartDao
import com.example.ecommerce.data.local.entities.CartItemEntity
import com.example.ecommerce.data.local.CartItemWithProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    fun getCartItems(userId: String): Flow<List<CartItemEntity>> {
        return cartDao.getCartItems(userId)
    }
    
    fun getCartItemsWithProductDetails(userId: String): Flow<List<CartItemWithProduct>> {
        return cartDao.getCartItemsWithProductDetails(userId)
    }

    suspend fun addToCart(userId: String, productId: String, quantity: Int) {
        val existingItem = cartDao.getItem(userId, productId)
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
            cartDao.updateItem(updatedItem)
        } else {
            cartDao.insertItem(CartItemEntity(userId = userId, productId = productId, quantity = quantity))
        }
    }

    suspend fun updateQuantity(userId: String, productId: String, newQuantity: Int) {
        val item = cartDao.getItem(userId, productId)
        if (item != null) {
            if (newQuantity > 0) {
                cartDao.updateItem(item.copy(quantity = newQuantity))
            } else {
                cartDao.deleteItem(userId = userId, productId = productId)
            }
        }
    }

    suspend fun removeFromCart(userId: String, productId: String) {
        cartDao.deleteItem(userId = userId, productId = productId)
    }

    suspend fun clearCart(userId: String) {
        cartDao.clearCart(userId)
    }
}
