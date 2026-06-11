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
    val actionItemId: Int? = null,
    val pendingItems: List<PendingOrderItemDto> = emptyList(),
    val errorMessage: String = "",
    val successMessage: String = ""
)

class AdminOrderViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _uiState = MutableStateFlow(AdminOrderUiState())
    val uiState: StateFlow<AdminOrderUiState> = _uiState

    fun loadPendingOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = "",
                successMessage = ""
            )

            val result = repository.getPendingOrders()
            result.onSuccess { items ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pendingItems = items
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Khong lay duoc don"
                )
            }
        }
    }

    fun approveOrderItem(orderItemId: Int) {
        runAction(orderItemId, "Da duyet mon") {
            repository.approveOrderItem(orderItemId)
        }
    }

    fun rejectOrderItem(orderItemId: Int) {
        runAction(orderItemId, "Da tu choi mon") {
            repository.rejectOrderItem(orderItemId)
        }
    }

    fun markItemServed(orderItemId: Int) {
        runAction(orderItemId, "Da danh dau phuc vu") {
            repository.markItemServed(orderItemId)
        }
    }

    private fun runAction(
        orderItemId: Int,
        successMessage: String,
        action: suspend () -> Result<*>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                actionItemId = orderItemId,
                errorMessage = "",
                successMessage = ""
            )

            val result = action()
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    actionItemId = null,
                    successMessage = successMessage
                )
                loadPendingOrders()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    actionItemId = null,
                    errorMessage = error.message ?: "Cap nhat that bai"
                )
            }
        }
    }
}
