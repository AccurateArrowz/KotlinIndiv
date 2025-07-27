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

    private fun loadProducts() {
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
                name = "Sample Product 1",
                description = "This is a sample product description.",
                price = 29.99,
                imageIdentifier = "sample_image_1",
                category = "Sample Category"
            ),
            ProductEntity(
                id = "2",
                name = "Sample Product 2",
                description = "Another sample product description.",
                price = 39.99,
                imageIdentifier = "sample_image_2",
                category = "Sample Category"
            )
            // Add more sample products as needed
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