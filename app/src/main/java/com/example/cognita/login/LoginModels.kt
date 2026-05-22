package com.example.cognita.login

data class LoginRequest(val email: String, val password: String)

// Update this to handle just the message, since your backend returns text
data class LoginResponse(val message: String?)

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    // Changed from 'token' to 'message'
    data class Success(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}