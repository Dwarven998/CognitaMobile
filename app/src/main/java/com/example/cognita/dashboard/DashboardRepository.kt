package com.example.cognita.dashboard

import com.example.cognita.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardRepository {
    private val api = ApiClient.retrofit.create(DashboardApi::class.java)

    fun fetchStats(token: String, onResult: (DashboardState) -> Unit) {
        api.getUserStats("Bearer $token").enqueue(object : Callback<UserStats> {
            override fun onResponse(call: Call<UserStats>, response: Response<UserStats>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(DashboardState.Success(response.body()!!))
                } else {
                    onResult(DashboardState.Error("Failed to load stats"))
                }
            }
            override fun onFailure(call: Call<UserStats>, t: Throwable) {
                onResult(DashboardState.Error("Network error"))
            }
        })
    }
}