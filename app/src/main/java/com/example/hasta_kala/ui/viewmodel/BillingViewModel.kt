package com.example.hasta_kala.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.FirebaseManager
import com.example.hasta_kala.data.Product
import com.example.hasta_kala.data.Sale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BillingUiState(
    val products: List<Product> = emptyList(),
    val selectedProduct: Product? = null,
    val quantity: Int = 1,
    val isProcessing: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class BillingViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            val products = firebaseManager.getProducts()
            _uiState.value = _uiState.value.copy(products = products)
        }
    }

    fun selectProduct(product: Product) {
        _uiState.value = _uiState.value.copy(selectedProduct = product, quantity = 1, error = null)
    }

    fun updateQuantity(qty: Int) {
        val stock = _uiState.value.selectedProduct?.stock ?: 0
        if (qty > 0 && qty <= stock) {
            _uiState.value = _uiState.value.copy(quantity = qty, error = null)
        } else if (qty > stock) {
            _uiState.value = _uiState.value.copy(error = "Cannot exceed available stock ($stock)")
        }
    }

    fun processSale() {
        val state = _uiState.value
        val product = state.selectedProduct ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            val sale = Sale(
                productId = product.id,
                productName = product.name,
                quantity = state.quantity,
                color = product.color,
                totalAmount = product.price * state.quantity
            )
            val result = firebaseManager.processSale(sale)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isProcessing = false, success = true)
                loadProducts() // Refresh to show new stock
            } else {
                val exception = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(
                    isProcessing = false, 
                    error = exception?.message ?: "Sale failed. Please try again."
                )
            }
        }
    }
    
    fun resetState() {
        _uiState.value = _uiState.value.copy(success = false, error = null, selectedProduct = null)
    }
}
