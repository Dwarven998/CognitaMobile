package com.example.cognita.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val repository = LoginRepository()

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Please fill in all fields")
            return
        }

        val request = LoginRequest(email, password)
        repository.authenticateUser(request) { state ->
            _loginState.value = state
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}