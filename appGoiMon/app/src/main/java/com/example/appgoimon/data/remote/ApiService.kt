package com.example.appgoimon.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class AuthLoginRequest(
    val username: String,
    val password: String
)

data class AuthRegisterRequest(
    val username: String,
    val password: String,
    val full_name: String,
    val phone: String
)

data class AuthUserDto(
    val id: Int,
    val username: String,
    val full_name: String?,
    val phone: String?,
    val role: String,
    val created_at: String?
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
    val start_time: String?,
    val end_time: String? = null,
    val paid_at: String? = null,
    val is_expired: Boolean? = null,
    val remaining_seconds: Int? = null,
    val remaining_minutes: Int? = null
)

data class TableSessionResponseDto(
    val session: TableSessionDto,
    val order_items: List<OrderItemDto>,
    val unfinished_item_count: Int? = null,
    val has_unfinished_items: Boolean? = null
)

data class TableSessionDto(
    val id: Int,
    val table_id: Int,
    val combo_id: Int,
    val paid_guest_count: Int,
    val free_child_count: Int,
    val payment_method: String?,
    val payment_status: String?,
    val status: String,
    val total_amount: String,
    val start_time: String?,
    val end_time: String?,
    val paid_at: String? = null,
    val is_expired: Boolean? = null,
    val remaining_seconds: Int? = null,
    val remaining_minutes: Int? = null,
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

data class CloseTableRequest(
    val session_id: Int
)

data class DashboardStatsDto(
    val total_revenue: String,
    val today_revenue: String,
    val active_sessions: Int,
    val pending_payment_sessions: Int,
    val tables: DashboardTableStatsDto,
    val order_items: DashboardOrderItemStatsDto,
    val menu_items: DashboardMenuItemStatsDto,
    val categories: DashboardCategoryStatsDto
)

data class DashboardTableStatsDto(
    val total: Int,
    val available: Int,
    val occupied: Int,
    val waiting_payment: Int
)

data class DashboardOrderItemStatsDto(
    val pending: Int,
    val processing: Int = 0,
    val approved: Int = 0,
    val served: Int,
    val rejected: Int
)

data class DashboardMenuItemStatsDto(
    val total: Int,
    val available: Int,
    val out_of_stock: Int,
    val hidden: Int
)

data class DashboardCategoryStatsDto(
    val total: Int,
    val active: Int,
    val inactive: Int
)

data class CategoryDto(
    val id: Int,
    val category_name: String,
    val status: String
)

data class ManageCategoryRequest(
    val action: String,
    val category_id: Int? = null,
    val category_name: String? = null,
    val status: String? = null
)

data class MenuItemDto(
    val id: Int,
    val category_id: Int,
    val category_name: String?,
    val name: String,
    val image: String?,
    val description: String?,
    val status: String
)

data class ManageMenuItemRequest(
    val action: String,
    val food_id: Int? = null,
    val category_id: Int? = null,
    val name: String? = null,
    val image: String? = null,
    val description: String? = null,
    val status: String? = null
)

data class UploadImageDto(
    val filename: String
)

data class AdminComboDto(
    val id: Int,
    val combo_name: String,
    val price_per_person: String,
    val description: String? = null,
    val status: String,
    val item_count: Int = 0
)

data class ManageComboRequest(
    val action: String,
    val combo_id: Int? = null,
    val name: String? = null,
    val price_per_person: Int? = null,
    val description: String? = null,
    val status: String? = null,
    val food_ids: List<Int>? = null
)

data class PendingOrderItemDto(
    val order_item_id: Int,
    val order_id: Int,
    val session_id: Int,
    val table_id: Int,
    val table_code: String,
    val table_name: String,
    val food_id: Int,
    val food_name: String,
    val image: String? = null,
    val quantity: Int,
    val note: String?,
    val status: String,
    val created_at: String?
)

data class OrderItemActionRequest(
    val order_item_id: Int
)

data class MutationResultDto(
    val session_id: Int? = null,
    val category_id: Int? = null,
    val food_id: Int? = null,
    val combo_id: Int? = null,
    val order_item_id: Int? = null,
    val status: String? = null
)

data class TableCheckRequest(
    val table_code: String,
    val user_id: Int
)

data class UserTableDto(
    val id: Int,
    val table_code: String,
    val table_name: String,
    val status: String,
    // True when this table's active session belongs to the current user (resumable),
    // false/null when it is free or owned by another guest.
    val is_mine: Boolean = false
)

data class UserComboDto(
    val id: Int,
    @SerializedName(value = "name", alternate = ["combo_name"])
    val name: String,
    val price_per_person: String,
    val description: String?,
    val image: String? = null,
    val images: List<String> = emptyList(),
    val status: String
)

data class UserSessionDto(
    val id: Int,
    val table_id: Int,
    // Owner of the session (logged-in user). Null for legacy/anonymous sessions.
    val user_id: Int? = null,
    val combo_id: Int,
    val paid_guest_count: Int,
    val free_child_count: Int,
    val payment_method: String?,
    val payment_status: String?,
    val status: String,
    val total_amount: String,
    val start_time: String?,
    val end_time: String? = null,
    val paid_at: String? = null,
    val is_expired: Boolean? = null,
    val remaining_seconds: Int? = null,
    val remaining_minutes: Int? = null,
    val table_code: String? = null,
    val table_name: String? = null,
    val combo_name: String? = null,
    // OFFLINE-generated VietQR (Napas EMVCo) bank-transfer payload + display fields for QR sessions.
    val vietqr_payload: String? = null,
    val bank_account_no: String? = null,
    val bank_account_name: String? = null,
    val bank_name_or_bin: String? = null
)

data class TableCheckResponseDto(
    val table: UserTableDto,
    val session: UserSessionDto?
)

data class CreateSessionRequest(
    val table_code: String,
    val combo_id: Int,
    val paid_guest_count: Int,
    val free_child_count: Int,
    val payment_method: String,
    val user_id: Int
)

data class CreateSessionResponseDto(
    val session_id: Int,
    val table_id: Int,
    val combo: UserComboDto?,
    val total_amount: String,
    val status: String,
    val session: UserSessionDto? = null,
    // OFFLINE-generated VietQR (Napas EMVCo) bank-transfer payload + display fields for QR sessions.
    val vietqr_payload: String? = null,
    val bank_account_no: String? = null,
    val bank_account_name: String? = null,
    val bank_name_or_bin: String? = null
)

data class SimulatePaymentRequest(
    val session_id: Int
)

data class SimulatePaymentDataDto(
    val result: String? = null,
    val session_id: Int? = null,
    val transaction_id: String? = null,
    val amount: Double? = null
)

data class CreateOrderItemRequest(
    val food_id: Int,
    val quantity: Int,
    val note: String = ""
)

data class CreateOrderRequest(
    val session_id: Int,
    val items: List<CreateOrderItemRequest>,
    val note: String = ""
)

data class CreatedOrderItemDto(
    val order_item_id: Int,
    val food_id: Int,
    val quantity: Int,
    val status: String
)

data class CreateOrderResponseDto(
    val order_id: Int,
    val items: List<CreatedOrderItemDto>
)

data class OrderHistoryItemDto(
    val food_name: String,
    val image: String? = null,
    val quantity: Int,
    val note: String?,
    val status: String
)

data class OrderHistoryDto(
    val order_id: Int,
    val created_at: String,
    val items: List<OrderHistoryItemDto>
)

interface ApiService {

    @POST("api/auth/login.php")
    suspend fun login(
        @Body request: AuthLoginRequest
    ): Response<ApiResponse<AuthUserDto>>

    @POST("api/auth/register.php")
    suspend fun register(
        @Body request: AuthRegisterRequest
    ): Response<ApiResponse<AuthUserDto>>

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

    @POST("api/admin/close_table.php")
    suspend fun closeTable(
        @Body request: CloseTableRequest
    ): Response<ApiResponse<MutationResultDto>>

    @GET("api/admin/get_dashboard_stats.php")
    suspend fun getDashboardStats(): Response<ApiResponse<DashboardStatsDto>>

    @GET("api/admin/get_categories.php")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    @POST("api/admin/manage_category.php")
    suspend fun manageCategory(
        @Body request: ManageCategoryRequest
    ): Response<ApiResponse<MutationResultDto>>

    @GET("api/admin/get_menu_items.php")
    suspend fun getMenuItems(): Response<ApiResponse<List<MenuItemDto>>>

    @POST("api/admin/manage_menu_item.php")
    suspend fun manageMenuItem(
        @Body request: ManageMenuItemRequest
    ): Response<ApiResponse<MutationResultDto>>

    @Multipart
    @POST("api/admin/upload_image.php")
    suspend fun uploadFoodImage(
        @Part image: MultipartBody.Part
    ): Response<ApiResponse<UploadImageDto>>

    @GET("api/admin/get_combos.php")
    suspend fun getAdminCombos(): Response<ApiResponse<List<AdminComboDto>>>

    @GET("api/admin/get_combo_foods.php")
    suspend fun getComboFoodIds(
        @Query("combo_id") comboId: Int
    ): Response<ApiResponse<List<Int>>>

    @POST("api/admin/manage_combo.php")
    suspend fun manageCombo(
        @Body request: ManageComboRequest
    ): Response<ApiResponse<MutationResultDto>>

    @GET("api/admin/get_pending_orders.php")
    suspend fun getPendingOrders(
        @Query("status") status: String = "pending",
        @Query("date") date: String? = null
    ): Response<ApiResponse<List<PendingOrderItemDto>>>

    @POST("api/admin/approve_order_item.php")
    suspend fun approveOrderItem(
        @Body request: OrderItemActionRequest
    ): Response<ApiResponse<MutationResultDto>>

    @POST("api/admin/reject_order_item.php")
    suspend fun rejectOrderItem(
        @Body request: OrderItemActionRequest
    ): Response<ApiResponse<MutationResultDto>>

    @POST("api/admin/mark_item_served.php")
    suspend fun markItemServed(
        @Body request: OrderItemActionRequest
    ): Response<ApiResponse<MutationResultDto>>

    @GET("api/user/list_tables.php")
    suspend fun listUserTables(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<List<UserTableDto>>>

    @GET("api/user/my_active_session.php")
    suspend fun getMyActiveSession(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<UserSessionDto?>>

    @POST("api/user/check_table.php")
    suspend fun checkTable(
        @Body request: TableCheckRequest
    ): Response<ApiResponse<TableCheckResponseDto>>

    @GET("api/user/get_combos.php")
    suspend fun getUserCombos(): Response<ApiResponse<List<UserComboDto>>>

    @POST("api/user/create_session.php")
    suspend fun createUserSession(
        @Body request: CreateSessionRequest
    ): Response<ApiResponse<CreateSessionResponseDto>>

    @GET("api/user/get_session_status.php")
    suspend fun getUserSessionStatus(
        @Query("session_id") sessionId: Int
    ): Response<ApiResponse<UserSessionDto>>

    @POST("api/payment/simulate.php")
    suspend fun simulatePayment(
        @Body request: SimulatePaymentRequest
    ): Response<ApiResponse<SimulatePaymentDataDto>>

    @GET("api/user/get_menu_by_combo.php")
    suspend fun getMenuByCombo(
        @Query("combo_id") comboId: Int
    ): Response<ApiResponse<List<MenuItemDto>>>

    @POST("api/user/create_order.php")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Response<ApiResponse<CreateOrderResponseDto>>

    @GET("api/user/get_order_history.php")
    suspend fun getOrderHistory(
        @Query("session_id") sessionId: Int
    ): Response<ApiResponse<List<OrderHistoryDto>>>
}
