package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.AdminComboDto
import com.example.appgoimon.data.remote.ManageComboRequest
import com.example.appgoimon.data.remote.MutationResultDto
import com.example.appgoimon.data.remote.RetrofitClient

/** Admin-side combo management: list combos, read a combo's assigned dishes, and create/update/delete. */
class ComboRepository {

    suspend fun getCombos(): Result<List<AdminComboDto>> {
        return try {
            val response = RetrofitClient.apiService.getAdminCombos()
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Không lấy được danh sách combo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Không thể kết nối máy chủ"))
        }
    }

    suspend fun getComboFoodIds(comboId: Int): Result<List<Int>> {
        return try {
            val response = RetrofitClient.apiService.getComboFoodIds(comboId)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Không lấy được món của combo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Không thể kết nối máy chủ"))
        }
    }

    suspend fun createCombo(
        name: String,
        pricePerPerson: Int,
        description: String,
        status: String,
        foodIds: List<Int>
    ): Result<MutationResultDto> = manage(
        ManageComboRequest(
            action = "create",
            name = name,
            price_per_person = pricePerPerson,
            description = description,
            status = status,
            food_ids = foodIds
        )
    )

    suspend fun updateCombo(
        comboId: Int,
        name: String,
        pricePerPerson: Int,
        description: String,
        status: String,
        foodIds: List<Int>
    ): Result<MutationResultDto> = manage(
        ManageComboRequest(
            action = "update",
            combo_id = comboId,
            name = name,
            price_per_person = pricePerPerson,
            description = description,
            status = status,
            food_ids = foodIds
        )
    )

    suspend fun deleteCombo(comboId: Int): Result<MutationResultDto> = manage(
        ManageComboRequest(action = "delete", combo_id = comboId)
    )

    private suspend fun manage(request: ManageComboRequest): Result<MutationResultDto> {
        return try {
            val response = RetrofitClient.apiService.manageCombo(request)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Cập nhật combo thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Không thể kết nối máy chủ"))
        }
    }
}
