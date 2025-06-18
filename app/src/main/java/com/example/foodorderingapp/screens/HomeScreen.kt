package com.example.foodorderingapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class FoodItem(val name: String, val description: String, val price: String)

@Composable
fun HomeScreen() {
    val sampleFoodItems = listOf(
        FoodItem("Classic Burger", "Juicy beef patty, lettuce, tomato, cheese.", "$12.99"),
        FoodItem("Margherita Pizza", "Fresh mozzarella, basil, tomato sauce.", "$15.00"),
        FoodItem("Caesar Salad", "Crisp romaine, croutons, parmesan, Caesar dressing.", "$9.50"),
        FoodItem("Spicy Chicken Tacos", "Grilled chicken, salsa, avocado, spicy mayo.", "$11.75"),
        FoodItem("Vegetable Biryani", "Fragrant basmati rice cooked with mixed vegetables.", "$14.00"),
        FoodItem("Sushi Combo", "Assortment of fresh nigiri and maki rolls.", "$22.00"),
        FoodItem("Pad Thai", "Stir-fried noodles with shrimp, peanuts, and bean sprouts.", "$13.50")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Popular Dishes",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items(sampleFoodItems) { item ->
                FoodItemCard(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FoodItemCard(item: FoodItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.price, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
