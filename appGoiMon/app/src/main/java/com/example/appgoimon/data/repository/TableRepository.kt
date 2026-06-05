package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.data.remote.TableDto
import com.example.appgoimon.data.remote.TableSessionResponseDto
import com.example.appgoimon.data.remote.ConfirmPaymentRequest
import com.example.appgoimon.data.remote.ConfirmPaymentDataDto


class TableRepository {

    suspend fun getTables(): Result<List<TableDto>> {
        return try {
            val response = RetrofitClient.apiService.getTables()
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Không lấy được danh sách bàn"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối server: ${e.message}"))
        }
    }

    suspend fun getTableSession(tableId: Int): Result<TableSessionResponseDto> {
        return try {
            val response = RetrofitClient.apiService.getTableSession(tableId)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Không lấy được chi tiết bàn"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối server: ${e.message}"))
        }
    }


    suspend fun confirmPayment(sessionId: Int): Result<ConfirmPaymentDataDto> {
        return try {
            val response = RetrofitClient.apiService.confirmPayment(
                ConfirmPaymentRequest(session_id = sessionId)
            )

            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Không xác nhận được thanh toán"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối server: ${e.message}"))
        }
    }
}