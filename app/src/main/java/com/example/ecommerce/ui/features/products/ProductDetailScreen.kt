package com.example.ecommerce.ui.features.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ecommerce.data.local.entities.ProductEntity
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.ui.features.cart.CartViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Added imports
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel<ProductDetailViewModel>(),
    cartViewModel: CartViewModel = hiltViewModel<CartViewModel>()
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
fun ProductDetailContent(
    product: ProductEntity,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedImage by remember { mutableStateOf(product.imageIdentifier) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Main Product Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            // Main product image with zoom capability
            Image(
                painter = rememberAsyncImagePainter(
                    model = selectedImage,
                    error = null
                ),
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )
            
            // Image gallery (horizontal scroll for multiple images)
            // For now, we're using the same image, but you can extend this to show multiple images
            if (false) { // Set to true if you have multiple images
                LazyRow(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf(product.imageIdentifier)) { imageUrl ->
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedImage == imageUrl) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedImage = imageUrl }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUrl, error = null),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Product Info Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Product Name and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { /* Navigate to category */ },
                    label = { Text(product.category) },
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                // Rating (you can add actual rating logic)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.8",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = " (128)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description Section
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Features/Highlights
            Text(
                text = "Highlights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sample features - you can customize this based on your product data
            val features = listOf(
                "Premium quality materials",
                "1-year manufacturer warranty",
                "Free shipping on all orders",
                "Easy returns within 30 days"
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        // Add bottom padding for the FAB
        Spacer(modifier = Modifier.height(80.dp))
    }
    
    // Add to Cart Button (FAB)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { 
                // Handle add to cart
                // cartViewModel.addToCart(...)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .height(48.dp)
                .width(200.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Add to Cart",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add to Cart",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
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