package com.example.cognita.api


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes matching what your Spring Boot backend expects and returns
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val message: String?)

interface AuthApi {
    // This targets your LoginController on the backend
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}