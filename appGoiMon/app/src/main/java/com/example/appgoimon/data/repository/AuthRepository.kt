package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.AdminLoginRequest
import com.example.appgoimon.data.remote.AdminUserDto
import com.example.appgoimon.data.remote.RetrofitClient

class AuthRepository {

    suspend fun loginAdmin(
        username: String,
        password: String
    ): Result<AdminUserDto> {
        return try {
            val response = RetrofitClient.apiService.adminLogin(
                AdminLoginRequest(
                    username = username,
                    password = password
                )
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Đăng nhập thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi server: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối server: ${e.message}"))
        }
    }
}