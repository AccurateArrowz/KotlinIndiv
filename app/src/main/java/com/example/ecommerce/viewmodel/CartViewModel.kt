
package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourpackage.data.model.Product
import com.yourpackage.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Represents a product in the cart along with its quantity and details
data class CartDisplayItem(
    val product: Product,
    val quantityInCart: Int,
    val cartEntityId: Int // Original CartItemEntity.id, useful for some operations
)

data class CartUiState(
    val items: List<CartDisplayItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    var currentUserId: String? = null // To ensure we're acting on the correct user's cart
)

class CartViewModel(
    private val cartRepository: CartRepository,
    private val allProducts: List<Product> // Static list of all available products
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
                cartRepository.getCartItems(userId).collect { cartItemEntities ->
                    val displayItems = cartItemEntities.mapNotNull { entity ->
                        allProducts.find { it.id == entity.productId }?.let { product ->
                            CartDisplayItem(
                                product = product,
                                quantityInCart = entity.quantity,
                                cartEntityId = entity.id
                            )
                        }
                    }
                    val totalPrice = displayItems.sumOf { it.product.price * it.quantityInCart }
                    _uiState.value = _uiState.value.copy(
                        items = displayItems,
                        totalPrice = totalPrice,
                        isLoading = false
                    )
                }
            } catch (e: Exception