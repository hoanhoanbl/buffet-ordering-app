package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.CreateSessionRequest
import com.example.appgoimon.data.remote.CreateSessionResponseDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.OrderHistoryDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.data.remote.SimulatePaymentDataDto
import com.example.appgoimon.data.remote.SimulatePaymentRequest
import com.example.appgoimon.data.remote.TableCheckRequest
import com.example.appgoimon.data.remote.TableCheckResponseDto
import com.example.appgoimon.data.remote.UserComboDto
import com.example.appgoimon.data.remote.UserSessionDto
import com.example.appgoimon.data.remote.UserTableDto

class UserSessionRepository {

    suspend fun listTables(userId: Int): Result<List<UserTableDto>> {
        return try {
            val response = RetrofitClient.apiService.listUserTables(userId)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc danh sach ban"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }

    suspend fun checkTable(tableCode: String, userId: Int): Result<TableCheckResponseDto> {
        return try {
            val response = RetrofitClient.apiService.checkTable(
                TableCheckRequest(table_code = tableCode, user_id = userId)
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
        paymentMethod: String,
        userId: Int
    ): Result<CreateSessionResponseDto> {
        return try {
            val response = RetrofitClient.apiService.createUserSession(
                CreateSessionRequest(
                    table_code = tableCode,
                    combo_id = comboId,
                    paid_guest_count = paidGuestCount,
                    free_child_count = freeChildCount,
                    payment_method = paymentMethod,
                    user_id = userId
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

    /**
     * Fetches the current user's single active session (or null when they have none), so the app can
     * auto-resume the running session on login / cold-start instead of showing the table picker.
     * A `success=true` response with null data is a valid "no active session" outcome.
     */
    suspend fun getMyActiveSession(userId: Int): Result<UserSessionDto?> {
        return try {
            val response = RetrofitClient.apiService.getMyActiveSession(userId)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc phien hien tai"))
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

    /**
     * Asks the MOCK payment gateway (api/payment/simulate.php) to report a payment for the session.
     * This does NOT itself mark the session paid — the server validates and flips the status, which
     * the waiting screen's poll of [getSessionStatus] then picks up.
     */
    suspend fun simulatePayment(sessionId: Int): Result<SimulatePaymentDataDto> {
        return try {
            val response = RetrofitClient.apiService.simulatePayment(
                SimulatePaymentRequest(session_id = sessionId)
            )
            val body = response.body()

            if (response.isSuccessful && body != null && body.success) {
                Result.success(body.data ?: SimulatePaymentDataDto())
            } else {
                Result.failure(Exception(body?.message ?: "Giả lập thanh toán thất bại"))
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
