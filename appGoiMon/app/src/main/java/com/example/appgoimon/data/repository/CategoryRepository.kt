package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.CategoryDto
import com.example.appgoimon.data.remote.ManageCategoryRequest
import com.example.appgoimon.data.remote.MutationResultDto
import com.example.appgoimon.data.remote.RetrofitClient

class CategoryRepository {

    suspend fun getCategories(): Result<List<CategoryDto>> {
        return try {
            val response = RetrofitClient.apiService.getCategories()
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Khong lay duoc danh muc"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }

    suspend fun createCategory(name: String, status: String): Result<MutationResultDto> {
        return manage(
            ManageCategoryRequest(
                action = "create",
                category_name = name,
                status = status
            )
        )
    }

    suspend fun updateCategory(categoryId: Int, name: String, status: String): Result<MutationResultDto> {
        return manage(
            ManageCategoryRequest(
                action = "update",
                category_id = categoryId,
                category_name = name,
                status = status
            )
        )
    }

    suspend fun deleteCategory(categoryId: Int): Result<MutationResultDto> {
        return manage(
            ManageCategoryRequest(
                action = "delete",
                category_id = categoryId
            )
        )
    }

    suspend fun setCategoryStatus(categoryId: Int, status: String): Result<MutationResultDto> {
        return manage(
            ManageCategoryRequest(
                action = "set_status",
                category_id = categoryId,
                status = status
            )
        )
    }

    private suspend fun manage(request: ManageCategoryRequest): Result<MutationResultDto> {
        return try {
            val response = RetrofitClient.apiService.manageCategory(request)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Cap nhat danh muc that bai"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Khong the ket noi server: ${e.message}"))
        }
    }
}
