package com.example.ecommerce.ui.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.local.CartItemWithProduct
import com.example.ecommerce.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// Represents a product in the cart along with its quantity and details
data class CartDisplayItem(
    val productId: String,
    val productName: String,
    val productDescription: String,
    val productPrice: Double,
    val productImageIdentifier: String,
    val productCategory: String,
    val quantityInCart: Int,
    val cartItemId: Int // Original CartItemEntity.id, useful for some operations
)

data class CartUiState(
    val items: List<CartDisplayItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    var currentUserId: String? = null // To ensure we're acting on the correct user's cart
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun loadCartForUser(userId: String?) {
        if (userId == null) {
            _uiState.value = CartUiState(isLoading = false, error = "User not logged in.", currentUserId = null)
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, currentUserId = userId, error = null)

        viewModelScope.launch {
            try {
                cartRepository.getCartItemsWithProductDetails(userId).collectLatest { cartItemsWithDetails ->
                    val displayItems = cartItemsWithDetails.map { item ->
                        CartDisplayItem(
                            productId = item.productId,
                            productName = item.productName,
                            productDescription = item.productDescription,
                            productPrice = item.productPrice,
                            productImageIdentifier = item.productImageIdentifier,
                            productCategory = item.productCategory,
                            quantityInCart = item.quantity,
                            cartItemId = item.cartItemId
                        )
                    }
                    
                    val totalPrice = displayItems.sumOf { it.productPrice * it.quantityInCart }
                    
                    _uiState.value = _uiState.value.copy(
                        items = displayItems,
                        totalPrice = totalPrice,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error loading cart"
                )
            }
        }
    }

    fun updateCartItemQuantity(cartItemId: Int, newQuantity: Int) {
        val userId = _uiState.value.currentUserId
        if (userId == null) {
            _uiState.value = _uiState.value.copy(error = "User not logged in.")
            return
        }

        viewModelScope.launch {
            try {
                // Find the cart item by ID
                val cartItem = _uiState.value.items.find { it.cartItemId == cartItemId }
                if (cartItem != null) {
                    cartRepository.updateQuantity(userId, cartItem.productId, newQuantity)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error updating cart item"
                )
            }
        }
    }

    fun removeFromCart(cartItemId: Int) {
        val userId = _uiState.value.currentUserId
        if (userId == null) {
            _uiState.value = _uiState.value.copy(error = "User not logged in.")
            return
        }

        viewModelScope.launch {
            try {
                // Find the cart item by ID
                val cartItem = _uiState.value.items.find { it.cartItemId == cartItemId }
                if (cartItem != null) {
                    cartRepository.removeFromCart(userId, cartItem.productId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error removing item from cart"
                )
            }
        }
    }

    fun clearCart() {
        val userId = _uiState.value.currentUserId
        if (userId == null) {
            _uiState.value = _uiState.value.copy(error = "User not logged in.")
            return
        }

        viewModelScope.launch {
            try {
                cartRepository.clearCart(userId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error clearing cart"
                )
            }
        }
    }
}

// ViewModel Factory for CartViewModel
class CartViewModelFactory @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }