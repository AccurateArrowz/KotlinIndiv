package com.example.ecommerce.ui.features.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val currentUser: GoogleSignInAccount? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setSigningIn(isSigningIn: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isSigningIn)
    }

    fun onSignInSuccess(account: GoogleSignInAccount) {
        _uiState.value = AuthUiState(isSignedIn = true, currentUser = account, isLoading = false)
    }

    fun onSignInFailed(errorMessage: String?) {
        _uiState.value = AuthUiState(isSignedIn = false, error = errorMessage, isLoading = false)
    }

    fun signOut() {
        // Actual sign out logic will be handled by GoogleSignInClient in the Activity/Composable
        _uiState.value = AuthUiState() // Reset to initial state
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updateCurrentUser(account: GoogleSignInAccount?) {
        if (account != null) {
            _uiState.value = AuthUiState(isSignedIn = true, currentUser = account, isLoading = false)
        } else {
            // This case might happen if a silent sign-in fails or user is signed out elsewhere
            _uiState.value = AuthUiState(isSignedIn = false, currentUser = null, isLoading = false)
        }
    }
}
