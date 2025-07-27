package com.example.ecommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ecommerce.navigation.AppNavHost
import com.example.ecommerce.ui.theme.EcommerceAppTheme
import com.example.ecommerce.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcommerceAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EcommerceAppApp()
                }
            }
        }
    }
}

@Composable
fun EcommerceAppApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    AppNavHost(
        navController = navController,
        authViewModel = authViewModel
    )
}