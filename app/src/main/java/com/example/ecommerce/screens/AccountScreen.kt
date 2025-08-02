package com.example.ecommerce.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ecommerce.navigation.AppRoutes
import com.example.ecommerce.viewmodel.AuthResult
import com.example.ecommerce.viewmodel.AuthViewModel

@Composable
fun AccountScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val logoutState by authViewModel.logoutResult.collectAsState()

    LaunchedEffect(logoutState) {
        if (logoutState is AuthResult.Success) {
            navController.navigate(AppRoutes.LOGIN) {
                popUpTo(AppRoutes.HOME) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            Text("Logged in as: ${currentUser.email}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            onClick = { authViewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            enabled = logoutState !is AuthResult.Loading
        ) {
            if (logoutState is AuthResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Log Out")
            }
        }
    }
}
