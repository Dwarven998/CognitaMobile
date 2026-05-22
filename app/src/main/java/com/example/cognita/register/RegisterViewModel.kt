package com.example.cognita.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepository()

    private val _state = MutableLiveData<RegisterState>(RegisterState.Idle)
    val state: LiveData<RegisterState> = _state

    // Added fullName parameter
    fun register(email: String, pass1: String, pass2: String, fullName: String) {
        // Validate that all fields, including fullName, are filled
        if (email.isBlank() || pass1.isBlank() || pass2.isBlank() || fullName.isBlank()) {
            _state.value = RegisterState.Error("Please fill in all fields")
            return
        }
        if (pass1 != pass2) {
            _state.value = RegisterState.Error("Passwords do not match")
            return
        }

        _state.value = RegisterState.Loading

        // Use the new RegisterRequest class with all 3 fields
        val request = RegisterRequest(email, pass1, fullName)

        repository.registerUser(request) { result ->
            _state.value = result
        }
    }
}