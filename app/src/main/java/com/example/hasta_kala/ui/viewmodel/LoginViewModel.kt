package com.example.hasta_kala.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authManager = AuthManager()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("Please enter email and password")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = authManager.loginUser(email, pass)
            
            result.fold(
                onSuccess = {
                    _loginState.value = LoginState.Success
                    onSuccess()
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error.localizedMessage ?: "Invalid credentials")
                }
            )
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
