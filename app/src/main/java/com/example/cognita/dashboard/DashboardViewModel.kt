package com.example.cognita.dashboard


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    private val repository = DashboardRepository()

    private val _state = MutableLiveData<DashboardState>()
    val state: LiveData<DashboardState> = _state

    fun loadStats(token: String) {
        _state.value = DashboardState.Loading
        repository.fetchStats(token) { result ->
            _state.value = result
        }
    }
}