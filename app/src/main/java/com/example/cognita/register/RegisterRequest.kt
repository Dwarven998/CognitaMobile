package com.example.cognita.register


data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String // This is the field that was likely missing
)