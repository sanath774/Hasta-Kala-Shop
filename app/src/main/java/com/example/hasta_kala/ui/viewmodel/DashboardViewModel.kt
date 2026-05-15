package com.example.hasta_kala.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalSales: Double = 0.0,
    val totalIncome: Double = 0.0,
    val bestSeller: String = "Loading...",
    val lowStockCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DashboardViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshDashboard()
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val stats = firebaseManager.getDashboardStats()
                
                _uiState.value = DashboardUiState(
                    totalSales = (stats["totalSalesCount"] as? Int ?: 0).toDouble(),
                    totalIncome = stats["totalIncome"] as? Double ?: 0.0,
                    bestSeller = stats["bestSeller"] as? String ?: "No sales yet",
                    lowStockCount = stats["lowStockCount"] as? Int ?: 0,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load dashboard: ${e.message}"
                )
            }
        }
    }
}
