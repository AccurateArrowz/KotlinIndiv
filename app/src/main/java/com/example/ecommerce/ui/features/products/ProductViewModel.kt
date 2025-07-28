package com.example.ecommerce.ui.features.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.local.entities.ProductEntity
import com.example.ecommerce.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductListUiState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Check if we need to insert initial data
                val products = productRepository.getAllProducts()
                products.collectLatest { productList ->
                    if (productList.isEmpty()) {
                        // Insert sample products if the database is empty
                        insertSampleProducts()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            products = productList,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error loading products"
                )
            }
        }
    }

    private suspend fun insertSampleProducts() {
        val sampleProducts = listOf(
            ProductEntity(
                id = "1",
                name = "Smartphone",
                description = "Latest smartphone with advanced features",
                price = 599.99,
                imageIdentifier = "https://via.placeholder.com/300x300?text=Smartphone",
                category = "Electronics"
            ),
            ProductEntity(
                id = "2",
                name = "Laptop",
                description = "High-performance laptop for work and gaming",
                price = 999.99,
                imageIdentifier = "https://via.placeholder.com/300x300?text=Laptop",
                category = "Electronics"
            ),
            ProductEntity(
                id = "3",
                name = "Headphones",
                description = "Wireless noise-canceling headphones",
                price = 199.99,
                imageIdentifier = "https://via.placeholder.com/300x300?text=Headphones",
                category = "Electronics"
            ),
            ProductEntity(
                id = "4",
                name = "T-Shirt",
                description = "Comfortable cotton t-shirt",
                price = 24.99,
                imageIdentifier = "https://via.placeholder.com/300x300?text=T-Shirt",
                category = "Clothing"
            ),
            ProductEntity(
                id = "5",
                name = "Sneakers",
                description = "Stylish and comfortable sneakers",
                price = 89.99,
                imageIdentifier = "https://via.placeholder.com/300x300?text=Sneakers",
                category = "Clothing"
            )
        )
        
        productRepository.insertInitialProducts(sampleProducts)
        
        // Update UI state with the newly inserted products
        _uiState.value = _uiState.value.copy(
            products = sampleProducts,
            isLoading = false,
            error = null
        )
    }
}

// ViewModel Factory for ProductViewModel
class ProductViewModelFactory @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}