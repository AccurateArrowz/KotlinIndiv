package com.example.ecommerce.data.local.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String, // Google User ID
    val productId: String, // ID from your static Product data class
    val quantity: Int
)
