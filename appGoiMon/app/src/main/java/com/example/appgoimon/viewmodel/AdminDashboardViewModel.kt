package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.DashboardStatsDto
import com.example.appgoimon.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val isFirstLoad: Boolean = true,
    val stats: DashboardStatsDto? = null,
    val errorMessage: String = ""
)

class AdminDashboardViewModel : ViewModel() {

    private val repository = DashboardRepository()

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            val result = repository.getDashboardStats()
            result.onSuccess { stats ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isFirstLoad = false,
                    stats = stats,
                    errorMessage = ""
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Không lấy được thống kê"
                )
            }
        }
    }
}
