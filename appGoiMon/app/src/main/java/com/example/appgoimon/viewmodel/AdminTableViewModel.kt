package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.TableDto
import com.example.appgoimon.data.remote.TableSessionResponseDto
import com.example.appgoimon.data.repository.TableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminTableUiState(
    val isLoading: Boolean = false,
    val tables: List<TableDto> = emptyList(),
    val selectedTableSession: TableSessionResponseDto? = null,
    val errorMessage: String = "",
    val successMessage: String = ""
)

class AdminTableViewModel : ViewModel() {

    private val repository = TableRepository()

    private val _uiState = MutableStateFlow(AdminTableUiState())
    val uiState: StateFlow<AdminTableUiState> = _uiState

    fun loadTables() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = ""
            )

            val result = repository.getTables()

            result.onSuccess { tables ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tables = tables
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Lỗi tải danh sách bàn"
                )
            }
        }
    }

    fun loadTableSession(tableId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = "",
                selectedTableSession = null
            )

            val result = repository.getTableSession(tableId)

            result.onSuccess { session ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedTableSession = session
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Lỗi tải chi tiết bàn"
                )
            }
        }
    }

    fun confirmPayment(sessionId: Int, tableId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = "",
                successMessage = ""
            )

            val result = repository.confirmPayment(sessionId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Đã xác nhận thanh toán và mở bàn"
                )

                loadTableSession(tableId)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Xác nhận thanh toán thất bại"
                )
            }
        }
    }

    fun closeTable(sessionId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = "",
                successMessage = ""
            )

            val result = repository.closeTable(sessionId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedTableSession = null,
                    successMessage = "Đã đóng bàn"
                )

                loadTables()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Đóng bàn thất bại"
                )
            }
        }
    }
}
