package com.example.cognita.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 is the Android Emulator's way to access your computer's localhost:8080
    // If you test on a physical phone, change this to your computer's Wi-Fi IPv4 address (e.g., "http://192.168.1.5:8080/")
    private const val BASE_URL = "http://10.0.2.2:8081/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
