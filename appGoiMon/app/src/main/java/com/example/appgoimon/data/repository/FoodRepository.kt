package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.ManageMenuItemRequest
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.MutationResultDto
import com.example.appgoimon.data.remote.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class FoodRepository {

    /**
     * Uploads raw image bytes as multipart/form-data and returns the stored filename
     * (to be saved in the menu item's `image` field).
     */
    suspend fun uploadImage(bytes: ByteArray, mimeType: String, fileName: String): Result<String> {
        return try {
            val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("image", fileName, body)
            val response = RetrofitClient.apiService.uploadFoodImage(part)
            val payload = response.body()

            if (response.isSuccessful && payload != null && payload.success && payload.data != null) {
                Result.success(payload.data.filename)
            } else {
                Result.failure(Exception(payload?.message ?: "Tải ảnh thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Không thể kết nối server"))
        }
    }

    suspend fun getMenuItems(): Result<List<MenuItemDto>> {
        return try {
            val response = RetrofitClient.apiService.getMenuItems()
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc danh sach mon"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
        }
    }

    suspend fun createMenuItem(
        categoryId: Int,
        name: String,
        image: String,
        description: String,
        status: String
    ): Result<MutationResultDto> {
        return manage(
            ManageMenuItemRequest(
                action = "create",
                category_id = categoryId,
                name = name,
                image = image,
                description = description,
                status = status
            )
        )
    }

    suspend fun updateMenuItem(
        foodId: Int,
        categoryId: Int,
        name: String,
        image: String,
        description: String,
        status: String
    ): Result<MutationResultDto> {
        return manage(
            ManageMenuItemRequest(
                action = "update",
                food_id = foodId,
                category_id = categoryId,
                name = name,
                image = image,
                description = description,
                status = status
            )
        )
    }

    suspend fun deleteMenuItem(foodId: Int): Result<MutationResultDto> {
        return manage(
            ManageMenuItemRequest(
                action = "delete",
                food_id = foodId
            )
        )
    }

    suspend fun setMenuItemStatus(foodId: Int, status: String): Result<MutationResultDto> {
        return manage(
            ManageMenuItemRequest(
                action = "set_status",
                food_id = foodId,
                status = status
            )
        )
    }

    private suspend fun manage(request: ManageMenuItemRequest): Result<MutationResultDto> {
        return try {
            val response = RetrofitClient.apiService.manageMenuItem(request)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Cap nhat mon that bai"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
        }
    }
}
