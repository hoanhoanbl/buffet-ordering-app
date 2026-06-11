package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.AuthLoginRequest
import com.example.appgoimon.data.remote.AuthRegisterRequest
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.data.remote.RetrofitClient

class AuthRepository {

    suspend fun login(
        username: String,
        password: String
    ): Result<AuthUserDto> {
        return try {
            val response = RetrofitClient.apiService.login(
                AuthLoginRequest(
                    username = username,
                    password = password
                )
            )

            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Dang nhap that bai"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }

    suspend fun register(
        username: String,
        password: String,
        fullName: String,
        phone: String
    ): Result<AuthUserDto> {
        return try {
            val response = RetrofitClient.apiService.register(
                AuthRegisterRequest(
                    username = username,
                    password = password,
                    full_name = fullName,
                    phone = phone
                )
            )

            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Dang ky that bai"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }
}
