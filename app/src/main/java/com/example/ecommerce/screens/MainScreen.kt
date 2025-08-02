package com.example.ecommerce.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ecommerce.navigation.AppRoutes
import com.example.ecommerce.navigation.MainAppRoutes
import com.example.ecommerce.viewmodel.AuthResult
import com.example.ecommerce.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecommerce.viewmodel.BasketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, authViewModel: AuthViewModel) {
    val basketViewModel: BasketViewModel = viewModel()
    val innerNavController = rememberNavController()
    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OrderOnline") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home Button
                        TextButton(
                            onClick = { innerNavController.navigate(MainAppRoutes.HOME_CONTENT) },
                            enabled = currentRoute != MainAppRoutes.HOME_CONTENT
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Home, contentDescription = "Home")
                                Text("Home", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        // Inbox Button
                        TextButton(
                            onClick = { innerNavController.navigate(MainAppRoutes.INBOX) },
                            enabled = currentRoute != MainAppRoutes.INBOX
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Inbox")
                                Text("Inbox", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        // Basket Button
                        TextButton(
                            onClick = { innerNavController.navigate(MainAppRoutes.BASKET) },
                            enabled = currentRoute != MainAppRoutes.BASKET
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Basket")
                                Text("Basket", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        // Account Button
                        TextButton(
                            onClick = { innerNavController.navigate(MainAppRoutes.ACCOUNT) },
                            enabled = currentRoute != MainAppRoutes.ACCOUNT
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                                Text("Account", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = MainAppRoutes.HOME_CONTENT,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(MainAppRoutes.HOME_CONTENT) {
                HomeScreen(basketViewModel = basketViewModel)
            }
            composable(MainAppRoutes.INBOX) {
                InboxScreen()
            }
            composable(MainAppRoutes.BASKET) {
                BasketScreen(navController = innerNavController, basketViewModel = basketViewModel)
            }
            composable(MainAppRoutes.ACCOUNT) {
                // Pass the main NavController for logout navigation
                AccountScreen(navController = navController, authViewModel = authViewModel)
            }
            composable(MainAppRoutes.CHECKOUT) {
                CheckoutScreen(basketViewModel = basketViewModel, onCheckoutSuccess = {
                    innerNavController.navigate(MainAppRoutes.HOME_CONTENT) {
                        popUpTo(MainAppRoutes.HOME_CONTENT) { inclusive = true }
                    }
                })
            }
        }
    }
}
