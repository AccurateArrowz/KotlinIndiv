package com.example.foodorderingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foodorderingapp.screens.LoginScreen
import com.example.foodorderingapp.screens.RegisterScreen
import com.example.foodorderingapp.screens.SplashScreen
import com.example.foodorderingapp.viewmodel.AuthViewModel

object AppRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val MAIN_APP_CONTAINER = "main_app_container"
}

object MainAppRoutes {
    const val HOME_CONTENT = "home_content"
    const val INBOX = "inbox"
    const val BASKET = "basket"
    const val ACCOUNT = "account"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(navController = navController, startDestination = AppRoutes.SPLASH) {
        composable(AppRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(AppRoutes.HOME) {
            androidx.compose.material3.Text(text = "Welcome to the Food Ordering App! You are logged in.")
        }
    }
}