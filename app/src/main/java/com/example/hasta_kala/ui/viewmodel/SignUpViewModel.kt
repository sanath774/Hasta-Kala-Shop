package com.example.hasta_kala.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hasta_kala.data.AuthManager
import com.example.hasta_kala.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

class SignUpViewModel : ViewModel() {
    private val authManager = AuthManager()

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun signUp(name: String, email: String, phone: String, pass: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()) {
            _signUpState.value = SignUpState.Error("All fields are required")
            return
        }
        
        if (pass.length < 6) {
            _signUpState.value = SignUpState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            val profile = UserProfile(name = name, email = email, phone = phone)
            val result = authManager.registerUser(email, pass, profile)
            
            result.fold(
                onSuccess = {
                    _signUpState.value = SignUpState.Success
                    onSuccess()
                },
                onFailure = { error ->
                    _signUpState.value = SignUpState.Error(error.localizedMessage ?: "Registration failed")
                }
            )
        }
    }
}
