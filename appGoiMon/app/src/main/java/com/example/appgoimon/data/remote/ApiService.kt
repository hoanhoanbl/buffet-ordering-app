package com.example.appgoimon.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class AdminLoginRequest(
    val username: String,
    val password: String
)

data class AdminUserDto(
    val id: Int,
    val username: String,
    val role: String
)

data class TableDto(
    val id: Int,
    val table_code: String,
    val table_name: String,
    val status: String,
    val session_id: Int?,
    val combo_id: Int?,
    val combo_name: String?,
    val paid_guest_count: Int?,
    val free_child_count: Int?,
    val payment_method: String?,
    val payment_status: String?,
    val session_status: String?,
    val total_amount: String?,
    val start_time: String?
)

data class TableSessionResponseDto(
    val session: TableSessionDto,
    val order_items: List<OrderItemDto>
)

data class TableSessionDto(
    val id: Int,
    val table_id: Int,
    val combo_id: Int,
    val paid_guest_count: Int,
    val free_child_count: Int,
    val payment_method: String,
    val payment_status: String,
    val status: String,
    val total_amount: String,
    val start_time: String?,
    val end_time: String?,
    val table_code: String,
    val table_name: String,
    val combo_name: String?,
    val price_per_person: String?
)

data class OrderItemDto(
    val order_id: Int,
    val order_status: String,
    val order_created_at: String?,
    val order_item_id: Int,
    val food_id: Int,
    val food_name: String,
    val quantity: Int,
    val note: String?,
    val item_status: String,
    val created_at: String?,
    val updated_at: String?
)

data class ConfirmPaymentRequest(
    val session_id: Int
)

data class ConfirmPaymentDataDto(
    val session_id: Int,
    val status: String
)

interface ApiService {

    @POST("api/admin/login.php")
    suspend fun adminLogin(
        @Body request: AdminLoginRequest
    ): Response<ApiResponse<AdminUserDto>>

    @GET("api/admin/get_tables.php")
    suspend fun getTables(): Response<ApiResponse<List<TableDto>>>

    @GET("api/admin/get_table_session.php")
    suspend fun getTableSession(
        @Query("table_id") tableId: Int
    ): Response<ApiResponse<TableSessionResponseDto>>

    @POST("api/admin/confirm_payment.php")
    suspend fun confirmPayment(
        @Body request: ConfirmPaymentRequest
    ): Response<ApiResponse<ConfirmPaymentDataDto>>
}
