package com.example.ecommerce.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

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

data class FoodItem(val id: Int, val name: String, val description: String, val price: String, val imageUrl: String)


@Composable
fun HomeScreen(basketViewModel: com.example.ecommerce.viewmodel.BasketViewModel = viewModel()) {
    val sampleFoodItems = listOf(
        FoodItem(1, "Classic Burger", "Juicy beef patty, lettuce, tomato, cheese.", "$12.99", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=500&q=80"),
        FoodItem(2, "Margherita Pizza", "Fresh mozzarella, basil, tomato sauce.", "$15.00", "https://images.unsplash.com/photo-1598021680135-2d83e3a3e462?w=500&q=80"),
        FoodItem(3, "Caesar Salad", "Crisp romaine, croutons, parmesan, Caesar dressing.", "$9.50", "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=500&q=80"),
        FoodItem(4, "Spicy Chicken Tacos", "Grilled chicken, salsa, avocado, spicy mayo.", "$11.75", "https://images.unsplash.com/photo-1565299589934-3c0415735167?w=500&q=80"),
        FoodItem(5, "Vegetable Biryani", "Fragrant basmati rice with mixed vegetables.", "$14.00", "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=500&q=80"),
        FoodItem(6, "Sushi Combo", "Assortment of fresh nigiri and maki rolls.", "$22.00", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=500&q=80"),
        FoodItem(7, "Pad Thai", "Stir-fried noodles with shrimp, peanuts, and bean sprouts.", "$13.50", "https://images.unsplash.com/photo-1623879540659-530127b95135?w=500&q=80")
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
            items(sampleFoodItems, key = { it.id }) { item ->
                FoodItemCard(item = item, onAddItem = { basketViewModel.addItem(item) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun FoodItemCard(item: FoodItem, onAddItem: (FoodItem) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 12.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.price, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
            Button(
                onClick = { onAddItem(item) },
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "Add to Basket")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
