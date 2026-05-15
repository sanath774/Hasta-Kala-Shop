package com.example.hasta_kala.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.FirebaseManager
import com.example.hasta_kala.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProductsViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()
    
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts(category: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val cat = if (category == "All" || category == null) null else category
                val result = firebaseManager.getProducts(cat)
                _uiState.value = ProductsUiState(products = result, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            val success = firebaseManager.addProduct(product)
            if (success) loadProducts()
        }
    }

    fun seedDemoData() {
        val demoProducts = listOf(
            Product(name = "Banana Fiber Tote", category = "Bags", price = 850.0, stock = 12, color = "Natural"),
            Product(name = "Hand-woven Clutch", category = "Bags", price = 450.0, stock = 5, color = "Red"),
            Product(name = "Eco Shoulder Bag", category = "Bags", price = 1200.0, stock = 8, color = "Brown"),
            Product(name = "Jute Flower Keychain", category = "Keychains", price = 150.0, stock = 40, color = "Yellow"),
            Product(name = "Wooden Bead Keychain", category = "Keychains", price = 120.0, stock = 25, color = "Natural"),
            Product(name = "Bamboo Coaster Set", category = "Others", price = 350.0, stock = 15, color = "Beige")
        )
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            demoProducts.forEach { firebaseManager.addProduct(it) }
            loadProducts()
        }
    }
}
