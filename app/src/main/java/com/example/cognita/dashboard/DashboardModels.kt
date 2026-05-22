package com.example.cognita.dashboard


data class UserStats(
    val name: String,
    val rank: String,
    val sp: Int,
    val streak: Int
)

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val stats: UserStats) : DashboardState()
    data class Error(val message: String) : DashboardState()
}