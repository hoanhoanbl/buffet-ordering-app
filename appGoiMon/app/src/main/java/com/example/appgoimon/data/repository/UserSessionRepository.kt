package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.CreateSessionRequest
import com.example.appgoimon.data.remote.CreateSessionResponseDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.OrderHistoryDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.data.remote.TableCheckRequest
import com.example.appgoimon.data.remote.TableCheckResponseDto
import com.example.appgoimon.data.remote.UserComboDto
import com.example.appgoimon.data.remote.UserSessionDto

class UserSessionRepository {

    suspend fun checkTable(tableCode: String): Result<TableCheckResponseDto> {
        return try {
            val response = RetrofitClient.apiService.checkTable(
                TableCheckRequest(table_code = tableCode)
            )
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong tim thay ban"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }

    suspend fun getCombos(): Result<List<UserComboDto>> {
        return try {
            val response = RetrofitClient.apiService.getUserCombos()
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc combo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }

    suspend fun createSession(
        tableCode: String,
        comboId: Int,
        paidGuestCount: Int,
        freeChildCount: Int,
        paymentMethod: String
    ): Result<CreateSessionResponseDto> {
        return try {
            val response = RetrofitClient.apiService.createUserSession(
                CreateSessionRequest(
                    table_code = tableCode,
                    combo_id = comboId,
                    paid_guest_count = paidGuestCount,
                    free_child_count = freeChildCount,
                    payment_method = paymentMethod
                )
            )
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Tao phien that bai"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }

    suspend fun getSessionStatus(sessionId: Int): Result<UserSessionDto> {
        return try {
            val response = RetrofitClient.apiService.getUserSessionStatus(sessionId)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc trang thai phien"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }

    suspend fun getMenuByCombo(comboId: Int): Result<List<MenuItemDto>> {
        return try {
            val response = RetrofitClient.apiService.getMenuByCombo(comboId)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc menu"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }

    suspend fun getOrderHistory(sessionId: Int): Result<List<OrderHistoryDto>> {
        return try {
            val response = RetrofitClient.apiService.getOrderHistory(sessionId)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc lich su goi mon"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }
}
