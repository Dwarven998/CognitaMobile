package com.example.cognita.dashboard


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface DashboardApi {
    @GET("api/profile/stats")
    fun getUserStats(@Header("Authorization") token: String): Call<UserStats>
}