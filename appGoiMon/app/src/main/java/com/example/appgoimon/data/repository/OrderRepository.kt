package com.example.appgoimon.data.repository

import com.example.appgoimon.data.remote.ApiResponse
import com.example.appgoimon.data.remote.CreateOrderRequest
import com.example.appgoimon.data.remote.CreateOrderResponseDto
import com.example.appgoimon.data.remote.MutationResultDto
import com.example.appgoimon.data.remote.OrderItemActionRequest
import com.example.appgoimon.data.remote.PendingOrderItemDto
import com.example.appgoimon.data.remote.RetrofitClient
import retrofit2.Response

class OrderRepository {

    suspend fun createOrder(request: CreateOrderRequest): Result<CreateOrderResponseDto> {
        return try {
            val response = RetrofitClient.apiService.createOrder(request)
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Không thể gọi món"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
        }
    }

    suspend fun getPendingOrders(
        status: String = "pending",
        date: String? = null
    ): Result<List<PendingOrderItemDto>> {
        return try {
            val response = RetrofitClient.apiService.getPendingOrders(status, date)
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

    suspend fun approveOrderItem(orderItemId: Int): Result<MutationResultDto> {
        return updateOrderItem(orderItemId) { request ->
            RetrofitClient.apiService.approveOrderItem(request)
        }
    }

    suspend fun rejectOrderItem(orderItemId: Int): Result<MutationResultDto> {
        return updateOrderItem(orderItemId) { request ->
            RetrofitClient.apiService.rejectOrderItem(request)
        }
    }

    suspend fun markItemServed(orderItemId: Int): Result<MutationResultDto> {
        return updateOrderItem(orderItemId) { request ->
            RetrofitClient.apiService.markItemServed(request)
        }
    }

    private suspend fun updateOrderItem(
        orderItemId: Int,
        call: suspend (OrderItemActionRequest) -> Response<ApiResponse<MutationResultDto>>
    ): Result<MutationResultDto> {
        return try {
            val response = call(OrderItemActionRequest(order_item_id = orderItemId))
            val body = response.body()

            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Cap nhat trang thai mon that bai"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Khong the ket noi server"))
        }
    }
}
