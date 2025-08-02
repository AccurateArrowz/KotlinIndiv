package com.example.ecommerce.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import com.example.ecommerce.viewmodel.BasketViewModel
import androidx.navigation.NavController
import com.example.ecommerce.navigation.MainAppRoutes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BasketScreen(navController: NavController, basketViewModel: BasketViewModel) {
    val basketItems by basketViewModel.basketItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Basket", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))

        if (basketItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your basket is empty.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(basketItems.keys.toList()) { item ->
                    BasketItemRow(item = item, quantity = basketItems[item]!!, basketViewModel = basketViewModel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total: $%.2f".format(basketViewModel.totalPrice),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(MainAppRoutes.CHECKOUT) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}

@Composable
fun BasketItemRow(item: FoodItem, quantity: Int, basketViewModel: BasketViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = item.price, style = MaterialTheme.typography.bodyMedium)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { basketViewModel.removeItem(item) }) {
                Icon(Icons.Default.Remove, contentDescription = "Remove one")
            }
            Text(text = quantity.toString(), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = { basketViewModel.addItem(item) }) {
                Icon(Icons.Default.Add, contentDescription = "Add one")
            }
        }
    }
}
