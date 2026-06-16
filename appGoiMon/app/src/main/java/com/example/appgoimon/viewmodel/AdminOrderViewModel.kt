package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.PendingOrderItemDto
import com.example.appgoimon.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminOrderUiState(
    val isLoading: Boolean = false,
    val actionOrderId: Int? = null,
    val selectedStatus: String = "pending",
    val items: List<PendingOrderItemDto> = emptyList(),
    val errorMessage: String = "",
    val successMessage: String = ""
) {
    val orderGroups: List<AdminOrderGroup>
        get() = items
            .groupBy { it.order_id }
            .map { (orderId, orderItems) ->
                val firstItem = orderItems.first()
                AdminOrderGroup(
                    orderId = orderId,
                    sessionId = firstItem.session_id,
                    tableName = firstItem.table_name,
                    tableCode = firstItem.table_code,
                    status = firstItem.status,
                    createdAt = firstItem.created_at,
                    items = orderItems
                )
            }
}

data class AdminOrderGroup(
    val orderId: Int,
    val sessionId: Int,
    val tableName: String,
    val tableCode: String,
    val status: String,
    val createdAt: String?,
    val items: List<PendingOrderItemDto>
) {
    val totalQuantity: Int = items.sumOf { it.quantity }
    val itemCount: Int = items.size
}

class AdminOrderViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _uiState = MutableStateFlow(AdminOrderUiState())
    val uiState: StateFlow<AdminOrderUiState> = _uiState

    fun selectStatus(status: String) {
        if (_uiState.value.selectedStatus == status) return
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        loadOrders(status)
    }

    fun loadPendingOrders() {
        loadOrders(_uiState.value.selectedStatus)
    }

    private fun loadOrders(status: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = "",
                successMessage = ""
            )

            val result = repository.getPendingOrders(status)
            result.onSuccess { items ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = items
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Không lấy được đơn"
                )
            }
        }
    }

    fun approveOrderItem(orderItemId: Int) {
        runAction(orderItemId, "Đã duyệt món") {
            repository.approveOrderItem(orderItemId)
        }
    }

    fun rejectOrderItem(orderItemId: Int) {
        runAction(orderItemId, "Đã từ chối món") {
            repository.rejectOrderItem(orderItemId)
        }
    }

    fun markItemServed(orderItemId: Int) {
        runAction(orderItemId, "Đã đánh dấu phục vụ") {
            repository.markItemServed(orderItemId)
        }
    }

    fun approveOrder(orderId: Int) {
        runOrderAction(
            orderId = orderId,
            successMessage = "Đã duyệt lượt gọi"
        ) { item ->
            repository.approveOrderItem(item.order_item_id)
        }
    }

    fun rejectOrder(orderId: Int) {
        runOrderAction(
            orderId = orderId,
            successMessage = "Đã từ chối lượt gọi"
        ) { item ->
            repository.rejectOrderItem(item.order_item_id)
        }
    }

    fun markOrderServed(orderId: Int) {
        runOrderAction(
            orderId = orderId,
            successMessage = "Đã đánh dấu lượt gọi đã phục vụ"
        ) { item ->
            repository.markItemServed(item.order_item_id)
        }
    }

    private fun runAction(
        orderItemId: Int,
        successMessage: String,
        action: suspend () -> Result<*>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                actionOrderId = orderItemId,
                errorMessage = "",
                successMessage = ""
            )

            val result = action()
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    actionOrderId = null,
                    successMessage = successMessage
                )
                loadOrders(_uiState.value.selectedStatus)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    actionOrderId = null,
                    errorMessage = error.message ?: "Cập nhật thất bại"
                )
            }
        }
    }

    private fun runOrderAction(
        orderId: Int,
        successMessage: String,
        action: suspend (PendingOrderItemDto) -> Result<*>
    ) {
        val orderItems = _uiState.value.items.filter { it.order_id == orderId }
        if (orderItems.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                actionOrderId = orderId,
                errorMessage = "",
                successMessage = ""
            )

            val failed = orderItems
                .map { item -> action(item) }
                .firstOrNull { it.isFailure }

            if (failed == null) {
                _uiState.value = _uiState.value.copy(
                    actionOrderId = null,
                    successMessage = successMessage
                )
                loadOrders(_uiState.value.selectedStatus)
            } else {
                _uiState.value = _uiState.value.copy(
                    actionOrderId = null,
                    errorMessage = failed.exceptionOrNull()?.message ?: "Cập nhật lượt gọi thất bại"
                )
            }
        }
    }
}
