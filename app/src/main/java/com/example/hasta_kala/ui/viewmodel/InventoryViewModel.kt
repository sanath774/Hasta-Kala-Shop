package com.example.hasta_kala.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.FirebaseManager
import com.example.hasta_kala.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InventoryUiState(
    val items: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

class InventoryViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadInventory()
    }

    private fun loadInventory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val allProducts = firebaseManager.getProducts()
            _uiState.value = InventoryUiState(items = allProducts, isLoading = false)
        }
    }
}
