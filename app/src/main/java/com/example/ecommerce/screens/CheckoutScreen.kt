package com.example.ecommerce.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecommerce.viewmodel.BasketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    basketViewModel: BasketViewModel,
    onCheckoutSuccess: () -> Unit
) {
    val basketItems by basketViewModel.basketItems.collectAsState()
    var checkoutState by remember { mutableStateOf("idle") } // idle, processing, success
    val coroutineScope = rememberCoroutineScope()

    val progress by animateFloatAsState(targetValue = if (checkoutState == "processing") 1f else 0f, label = "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (checkoutState != "success") {
            Text("Order Summary", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(basketItems.keys.toList()) { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${basketItems[item]} x ${item.name}")
                        Text("$%.2f".format(item.price.removePrefix("$").toDouble() * basketItems[item]!!))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Total: $%.2f".format(basketViewModel.totalPrice),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        checkoutState = "processing"
                        delay(2000) // Simulate network request
                        basketViewModel.clearBasket()
                        checkoutState = "success"
                        delay(1500) // Show success message briefly
                        onCheckoutSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = checkoutState == "idle"
            ) {
                if (checkoutState == "processing") {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirm & Pay")
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Payment Successful!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
