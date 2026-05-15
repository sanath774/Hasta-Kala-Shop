package com.example.hasta_kala.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AIInsightsUiState(
    val insights: String = "Tap the button below to generate business insights based on your current stock and sales.",
    val isGenerating: Boolean = false,
    val error: String? = null
)

class AIInsightsViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager()
    
    // NOTE: In a production app, the API key should be stored securely (e.g., local.properties or Secrets Gradle Plugin)
    // For this prototype, you would replace this with your Gemini API Key
    private val GEMINI_API_KEY = "REPLACE_WITH_YOUR_KEY"

    private val _uiState = MutableStateFlow(AIInsightsUiState())
    val uiState: StateFlow<AIInsightsUiState> = _uiState.asStateFlow()

    fun generateInsights() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, error = null)
            try {
                val result = firebaseManager.generateAIInsights(GEMINI_API_KEY)
                _uiState.value = AIInsightsUiState(insights = result, isGenerating = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = "Could not reach AI services. Please check your internet connection."
                )
            }
        }
    }
}
