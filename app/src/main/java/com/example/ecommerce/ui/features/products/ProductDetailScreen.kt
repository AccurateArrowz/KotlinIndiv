package com.example.ecommerce.ui.features.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ecommerce.data.local.entities.ProductEntity
import com.example.ecommerce.ui.features.cart.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val productState by viewModel.productState.collectAsState()
    val cartState by cartViewModel.uiState.collectAsState()
    
    // Load product details when the screen is first displayed
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // You can add more actions here if needed
                }
            )
        },
        bottomBar = {
            productState.product?.let { product ->
                val cartItem = cartState.items.find { it.productId == product.id }
                val quantityInCart = cartItem?.quantityInCart ?: 0
                
                BottomAppBar {
                    Button(
                        onClick = {
                            if (quantityInCart > 0) {
                                // Navigate to cart or show cart dialog
                            } else {
                                cartViewModel.addToCart(product.id, 1)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            if (quantityInCart > 0) "Added to Cart ($quantityInCart)" else "Add to Cart",
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Add to Cart")
                    }
                }
            }
        }
    ) { padding ->
        productState.product?.let { product ->
            ProductDetailContent(
                product = product,
                modifier = Modifier.padding(padding)
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (productState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        productState.error ?: "Product not found",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: ProductEntity,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Product Image
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.imageIdentifier)
                .crossfade(true)
                .build(),
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
            error = painterResource(id = android.R.drawable.ic_dialog_alert)
        )
        
        Image(
            painter = painter,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product Name
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Product Price
        Text(
            text = "$${String.format("%.2f", product.price)}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product Category
        Text(
            text = "Category: ${product.category}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product Description
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// ViewModel for ProductDetailScreen
class ProductDetailViewModel @Inject constructor(
    private val productRepository: com.example.ecommerce.data.repository.ProductRepository
) : ViewModel() {
    private val _productState = MutableStateFlow(ProductDetailState())
    val productState: StateFlow<ProductDetailState> = _productState.asStateFlow()
    
    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _productState.value = _productState.value.copy(isLoading = true, error = null)
            try {
                productRepository.getProductById(productId).collect { product ->
                    _productState.value = _productState.value.copy(
                        product = product,
                        isLoading = false,
                        error = if (product == null) "Product not found" else null
                    )
                }
            } catch (e: Exception) {
                _productState.value = _productState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error loading product"
                )
            }
        }
    }
}

data class ProductDetailState(
    val product: ProductEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)