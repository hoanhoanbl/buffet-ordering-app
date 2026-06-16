package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.DashboardStatsDto
import com.example.appgoimon.data.remote.RetrofitClient

class DashboardRepository {

    suspend fun getDashboardStats(): Result<DashboardStatsDto> {
        return try {
            val response = RetrofitClient.apiService.getDashboardStats()
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc thong ke"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
        }
    }
}
