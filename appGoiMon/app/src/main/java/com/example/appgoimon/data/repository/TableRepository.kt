package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.CloseTableRequest
import com.example.appgoimon.data.remote.ConfirmPaymentDataDto
import com.example.appgoimon.data.remote.ConfirmPaymentRequest
import com.example.appgoimon.data.remote.MutationResultDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.data.remote.TableDto
import com.example.appgoimon.data.remote.TableSessionResponseDto

class TableRepository {

    suspend fun getTables(): Result<List<TableDto>> {
        return try {
            val response = RetrofitClient.apiService.getTables()
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc danh sach ban"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
        }
    }

    suspend fun getTableSession(tableId: Int): Result<TableSessionResponseDto> {
        return try {
            val response = RetrofitClient.apiService.getTableSession(tableId)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc chi tiet ban"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
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
                Result.failure(Exception(body?.message ?: "Khong xac nhan duoc thanh toan"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
        }
    }

    suspend fun closeTable(sessionId: Int): Result<MutationResultDto> {
        return try {
            val response = RetrofitClient.apiService.closeTable(
                CloseTableRequest(session_id = sessionId)
            )

            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong dong duoc ban"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
        }
    }
}
