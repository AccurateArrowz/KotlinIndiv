package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)

    val authState: StateFlow<AuthResult> = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _authState.value = AuthResult.Success
                        } else {
                            val errorMessage = task.exception?.message ?: "Unknown error occurred"
                            _authState.value = AuthResult.Error(errorMessage)
                        }
                    }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _authState.value = AuthResult.Success
                        } else {
                            val errorMessage = when (task.exception) {
                                is FirebaseAuthWeakPasswordException -> "Password is too weak. Please choose a stronger one."
                                is FirebaseAuthInvalidCredentialsException -> "Invalid email format or password."
                                is FirebaseAuthUserCollisionException -> "An account with this email already exists."
                                else -> task.exception?.message ?: "Registration failed. Please try again."
                            }
                            _authState.value = AuthResult.Error(errorMessage)
                        }
                    }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthResult.Idle
    }
}
