package com.example.ecommerce.data.local

data class CartItemWithProduct(
    val cartItemId: Int,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val productName: String,
    val productDescription: String,
    val productPrice: Double,
    val productImageIdentifier: String,
    val productCategory: String
)
