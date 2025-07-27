package com.example.ecommerce.data.model

import androidx.annotation.DrawableRes

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    @DrawableRes val imageResId: Int, // For local drawable resources
    val category: String = "General"
)

object SampleProducts {
    val list = listOf(
        Product(
            id = "prod101",
            name = "Classic T-Shirt",
            description = "A comfortable and stylish classic t-shirt made from premium cotton.",
            price = 29.99,
            imageResId = R.drawable.ic_placeholder, // Replace with actual drawable
            category = "Apparel"
        ),
        Product(
            id = "prod102",
            name = "Wireless Headphones",
            description = "High-fidelity wireless headphones with noise-cancellation.",
            price = 149.50,
            imageResId = R.drawable.ic_placeholder, // Replace with actual drawable
            category = "Electronics"
        ),
        Product(
            id = "prod103",
            name = "Coffee Mug",
            description = "A sturdy ceramic coffee mug, perfect for your morning brew.",
            price = 12.00,
            imageResId = R.drawable.ic_placeholder, // Replace with actual drawable
            category = "Home Goods"
        ),
        Product(
            id = "prod104",
            name = "Running Shoes",
            description = "Lightweight and durable running shoes for all terrains.",
            price = 89.99,
            imageResId = R.drawable.ic_placeholder, // Replace with actual drawable
            category = "Footwear"
        ),
        Product(
            id = "prod105",
            name = "Smartphone Stand",
            description = "Adjustable smartphone stand for your desk.",
            price = 15.75,
            imageResId = R.drawable.ic_placeholder, // Replace with actual drawable
            category = "Accessories"
        )
    )
}
