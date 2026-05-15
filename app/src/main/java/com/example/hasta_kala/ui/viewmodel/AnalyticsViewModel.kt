package com.example.hasta_kala.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.FirebaseManager
import com.example.hasta_kala.data.Sale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TimeRange { WEEK, MONTH, YEAR }

data class AnalyticsUiState(
    val allSales: List<Sale> = emptyList(),
    val filteredSales: List<Sale> = emptyList(),
    val colorSales: Map<String, Int> = emptyMap(),
    val selectedRange: TimeRange = TimeRange.WEEK,
    val isLoading: Boolean = false
)

class AnalyticsViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()
    private val TAG = "AnalyticsVM"
    
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val sales = firebaseManager.getSalesLogs()
                Log.d(TAG, "Fetched ${sales.size} total sales from Firebase")
                updateFilteredData(sales, _uiState.value.selectedRange)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading analytics", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setTimeRange(range: TimeRange) {
        viewModelScope.launch {
            updateFilteredData(_uiState.value.allSales, range)
        }
    }

    private fun updateFilteredData(allSales: List<Sale>, range: TimeRange) {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        
        val filterTime = when (range) {
            TimeRange.WEEK -> now - (7 * dayMillis)
            TimeRange.MONTH -> now - (30 * dayMillis)
            TimeRange.YEAR -> now - (365 * dayMillis)
        }

        // Filter sales by timestamp
        val filtered = allSales.filter { it.timestamp >= filterTime }
        Log.d(TAG, "Range ${range.name}: Showing ${filtered.size} of ${allSales.size} sales")
        
        // Calculate best selling colors for this specific period
        val colorStats = filtered.groupBy { it.color }
            .mapValues { entry -> entry.value.sumOf { it.quantity } }

        _uiState.value = _uiState.value.copy(
            allSales = allSales,
            filteredSales = filtered,
            colorSales = colorStats,
            selectedRange = range,
            isLoading = false
        )
    }
}
