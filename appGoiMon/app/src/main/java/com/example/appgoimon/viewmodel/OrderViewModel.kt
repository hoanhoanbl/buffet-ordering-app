package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.CreateSessionResponseDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.UserComboDto
import com.example.appgoimon.data.remote.UserSessionDto
import com.example.appgoimon.data.remote.UserTableDto
import com.example.appgoimon.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class UserOrderStep {
    TABLE_ENTRY,
    COMBO_SETUP,
    WAITING_PAYMENT,
    ACTIVE_MENU
}

data class UserOrderUiState(
    val step: UserOrderStep = UserOrderStep.TABLE_ENTRY,
    val tableCode: String = "",
    val table: UserTableDto? = null,
    val session: UserSessionDto? = null,
    val createSessionResult: CreateSessionResponseDto? = null,
    val combos: List<UserComboDto> = emptyList(),
    val selectedComboId: Int? = null,
    val paidGuestCount: String = "1",
    val freeChildCount: String = "0",
    val paymentMethod: String = "cash",
    val menuItems: List<MenuItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val isMenuLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
) {
    val selectedCombo: UserComboDto?
        get() = combos.firstOrNull { it.id == selectedComboId }

    val paidGuestCountValue: Int
        get() = paidGuestCount.toIntOrNull() ?: 0

    val freeChildCountValue: Int
        get() = freeChildCount.toIntOrNull() ?: 0

    val totalPreview: Double
        get() = (selectedCombo?.price_per_person?.toDoubleOrNull() ?: 0.0) * paidGuestCountValue
}

class OrderViewModel : ViewModel() {

    private val repository = UserSessionRepository()

    private val _uiState = MutableStateFlow(UserOrderUiState())
    val uiState: StateFlow<UserOrderUiState> = _uiState

    fun onTableCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(
            tableCode = value.uppercase(),
            errorMessage = ""
        )
    }

    fun checkTable() {
        val tableCode = _uiState.value.tableCode.trim()
        if (tableCode.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui long nhap ma ban")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = repository.checkTable(tableCode)

            result.onSuccess { response ->
                val session = response.session
                when {
                    session == null -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            table = response.table,
                            session = null,
                            createSessionResult = null,
                            step = UserOrderStep.COMBO_SETUP
                        )
                        loadCombos()
                    }

                    session.status == "active" -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            table = response.table,
                            session = session,
                            step = UserOrderStep.ACTIVE_MENU
                        )
                        loadMenu(session.combo_id)
                    }

                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            table = response.table,
                            session = session,
                            step = UserOrderStep.WAITING_PAYMENT
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Kiem tra ban that bai"
                )
            }
        }
    }

    fun loadCombos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = repository.getCombos()

            result.onSuccess { combos ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    combos = combos,
                    selectedComboId = _uiState.value.selectedComboId ?: combos.firstOrNull()?.id
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Khong lay duoc combo"
                )
            }
        }
    }

    fun selectCombo(comboId: Int) {
        _uiState.value = _uiState.value.copy(selectedComboId = comboId, errorMessage = "")
    }

    fun onPaidGuestCountChange(value: String) {
        _uiState.value = _uiState.value.copy(
            paidGuestCount = value.filter { it.isDigit() },
            errorMessage = ""
        )
    }

    fun onFreeChildCountChange(value: String) {
        _uiState.value = _uiState.value.copy(
            freeChildCount = value.filter { it.isDigit() },
            errorMessage = ""
        )
    }

    fun onPaymentMethodChange(value: String) {
        _uiState.value = _uiState.value.copy(paymentMethod = value, errorMessage = "")
    }

    fun createSession() {
        val state = _uiState.value
        val comboId = state.selectedComboId
        val paidGuests = state.paidGuestCountValue
        val freeChildren = state.freeChildCountValue
        val tableCode = state.table?.table_code ?: state.tableCode.trim()

        when {
            comboId == null -> {
                _uiState.value = state.copy(errorMessage = "Vui long chon combo")
                return
            }

            paidGuests <= 0 -> {
                _uiState.value = state.copy(errorMessage = "So khach tra phi phai lon hon 0")
                return
            }

            freeChildren < 0 -> {
                _uiState.value = state.copy(errorMessage = "So tre em mien phi khong hop le")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = repository.createSession(
                tableCode = tableCode,
                comboId = comboId,
                paidGuestCount = paidGuests,
                freeChildCount = freeChildren,
                paymentMethod = state.paymentMethod
            )

            result.onSuccess { created ->
                val session = UserSessionDto(
                    id = created.session_id,
                    table_id = created.table_id,
                    combo_id = comboId,
                    paid_guest_count = paidGuests,
                    free_child_count = freeChildren,
                    payment_method = state.paymentMethod,
                    payment_status = "unpaid",
                    status = created.status,
                    total_amount = created.total_amount,
                    start_time = null,
                    table_code = tableCode,
                    table_name = state.table?.table_name,
                    combo_name = state.selectedCombo?.name
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    session = session,
                    createSessionResult = created,
                    step = UserOrderStep.WAITING_PAYMENT,
                    successMessage = "Da tao phien, cho admin xac nhan"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Tao phien that bai"
                )
            }
        }
    }

    fun refreshSessionStatus() {
        val sessionId = _uiState.value.session?.id ?: _uiState.value.createSessionResult?.session_id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = repository.getSessionStatus(sessionId)

            result.onSuccess { session ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    session = session,
                    step = if (session.status == "active") UserOrderStep.ACTIVE_MENU else UserOrderStep.WAITING_PAYMENT
                )
                if (session.status == "active") {
                    loadMenu(session.combo_id)
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Khong cap nhat duoc trang thai"
                )
            }
        }
    }

    fun loadMenu(comboId: Int? = _uiState.value.session?.combo_id) {
        val id = comboId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isMenuLoading = true, errorMessage = "")
            val result = repository.getMenuByCombo(id)

            result.onSuccess { menu ->
                _uiState.value = _uiState.value.copy(
                    isMenuLoading = false,
                    menuItems = menu
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isMenuLoading = false,
                    errorMessage = error.message ?: "Khong lay duoc menu"
                )
            }
        }
    }

    fun backToTableEntry() {
        _uiState.value = UserOrderUiState()
    }

    fun backToComboSetup() {
        _uiState.value = _uiState.value.copy(
            step = UserOrderStep.COMBO_SETUP,
            errorMessage = "",
            successMessage = ""
        )
    }
}
