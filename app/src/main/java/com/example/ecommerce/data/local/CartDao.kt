package com.example.ecommerce.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.ecommerce.data.local.entities.CartItemEntity

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItemEntity)

    @Update
    suspend fun updateItem(item: CartItemEntity)

    @Delete
    suspend fun deleteItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun deleteItem(userId: String, productId: String)

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getItem(userId: String, productId: String): CartItemEntity?

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: String): Flow<List<CartItemEntity>>

    @Query("""
        SELECT ci.id as cartItemId, ci.userId, ci.productId, ci.quantity,
               p.name as productName, p.description as productDescription,
               p.price as productPrice, p.imageIdentifier as productImageIdentifier,
               p.category as productCategory
        FROM cart_items ci
        INNER JOIN products p ON ci.productId = p.id
        WHERE ci.userId = :userId
    """)
    fun getCartItemsWithProductDetails(userId: String): Flow<List<CartItemWithProduct>>

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)
}
