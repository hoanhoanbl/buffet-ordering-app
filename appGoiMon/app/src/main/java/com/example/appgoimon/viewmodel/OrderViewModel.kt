package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.CreateOrderItemRequest
import com.example.appgoimon.data.remote.CreateOrderRequest
import com.example.appgoimon.data.remote.CreateSessionResponseDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.OrderHistoryDto
import com.example.appgoimon.data.remote.UserComboDto
import com.example.appgoimon.data.remote.UserSessionDto
import com.example.appgoimon.data.remote.UserTableDto
import com.example.appgoimon.data.repository.OrderRepository
import com.example.appgoimon.data.repository.UserSessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

enum class UserOrderStep {
    TABLE_ENTRY,
    COMBO_SETUP,
    WAITING_PAYMENT,
    ACTIVE_MENU
}

data class CartItem(
    val menuItem: MenuItemDto,
    val quantity: Int,
    val note: String = ""
)

data class UserOrderUiState(
    val step: UserOrderStep = UserOrderStep.TABLE_ENTRY,
    val tableCode: String = "",
    val table: UserTableDto? = null,
    val availableTables: List<UserTableDto> = emptyList(),
    val isTablesLoading: Boolean = false,
    val session: UserSessionDto? = null,
    val createSessionResult: CreateSessionResponseDto? = null,
    val combos: List<UserComboDto> = emptyList(),
    val selectedComboId: Int? = null,
    val paidGuestCount: String = "1",
    val freeChildCount: String = "0",
    val paymentMethod: String = "cash",
    val menuItems: List<MenuItemDto> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryName: String? = null,
    val orderHistory: List<OrderHistoryDto> = emptyList(),
    val isLoading: Boolean = false,
    val isMenuLoading: Boolean = false,
    val isSubmittingOrder: Boolean = false,
    val isLoadingHistory: Boolean = false,
    // True while the mock-gateway simulate call is in flight (waiting-payment screen only).
    val isSimulatingPayment: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = "",
    // Live-ticking remaining seconds, driven by the countdown coroutine in OrderViewModel.
    // Null when there is no active session to count down.
    val liveRemainingSeconds: Int? = null,
    // One-shot signal: number of items just sent to the kitchen on a successful order.
    // Consumed by the UI (Snackbar) then reset via consumeOrderSuccess().
    val orderSuccessCount: Int? = null,
    // OFFLINE-generated VietQR (Napas EMVCo) bank-transfer payload + display fields, surfaced from the
    // create-session / session-status responses for QR sessions. Null for cash sessions.
    val vietqrPayload: String? = null,
    val bankAccountNo: String? = null,
    val bankAccountName: String? = null,
    val bankNameOrBin: String? = null
) {
    val selectedCombo: UserComboDto?
        get() = combos.firstOrNull { it.id == selectedComboId }

    // Transfer memo the (fake) gateway expects, embedding the session id: e.g. "BUFFET42".
    val paymentMemo: String
        get() {
            val id = session?.id ?: createSessionResult?.session_id
            return if (id != null) "BUFFET$id" else "BUFFET"
        }

    val paidGuestCountValue: Int
        get() = paidGuestCount.toIntOrNull() ?: 0

    val freeChildCountValue: Int
        get() = freeChildCount.toIntOrNull() ?: 0

    val totalPreview: Double
        get() = (selectedCombo?.price_per_person?.toDoubleOrNull() ?: 0.0) * paidGuestCountValue

    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val isSessionExpired: Boolean
        get() = session?.status == "expired" ||
            session?.is_expired == true ||
            (liveRemainingSeconds != null && liveRemainingSeconds <= 0)

    val remainingMinutes: Int
        get() {
            val secs = liveRemainingSeconds
            if (secs != null) {
                return if (secs <= 0) 0 else (secs + 59) / 60
            }
            return session?.remaining_minutes ?: 0
        }

    // True when there is a session worth showing a countdown for (active or just expired).
    val hasCountdownSession: Boolean
        get() = session != null

    val categories: List<String>
        get() = menuItems
            .mapNotNull { it.category_name }
            .distinct()
            .sorted()

    val filteredMenuItems: List<MenuItemDto>
        get() {
            var items = menuItems

            if (selectedCategoryName != null) {
                items = items.filter { it.category_name == selectedCategoryName }
            }

            if (searchQuery.isNotEmpty()) {
                items = items.filter {
                    it.name.lowercase(Locale.ROOT).contains(searchQuery.lowercase(Locale.ROOT))
                }
            }

            return items
        }
}

class OrderViewModel : ViewModel() {

    private val repository = UserSessionRepository()
    private val orderRepository = OrderRepository()

    private val _uiState = MutableStateFlow(UserOrderUiState())
    val uiState: StateFlow<UserOrderUiState> = _uiState

    // Id of the logged-in user, set by MainActivity on login / cold-start restore. Sent on every
    // ownership-aware call (list/check/create) and used to auto-resume the user's active session.
    private var currentUserId: Int = 0

    /** Records the active user id so session calls are scoped to (and owned by) that user. */
    fun setCurrentUserId(userId: Int) {
        currentUserId = userId
    }

    // Countdown ticker state. We anchor on the remaining seconds reported by the server at the
    // moment a session is loaded, plus the device's elapsed-time reference, so the countdown stays
    // correct regardless of server/device clock differences.
    private var countdownJob: Job? = null
    private var anchorRemainingSeconds: Int = 0
    private var anchorElapsedMillis: Long = 0L

    /**
     * (Re)starts the live countdown for the given session. Reads the server-provided
     * remaining_seconds as the anchor and ticks down locally every second. Stops itself when the
     * session expires. Safe to call repeatedly; it cancels any previous ticker first.
     */
    private fun startCountdown(session: UserSessionDto?) {
        countdownJob?.cancel()
        countdownJob = null

        val alreadyExpired = session?.status == "expired" || session?.is_expired == true
        val remaining = session?.remaining_seconds ?: 0

        if (session == null || alreadyExpired || remaining <= 0) {
            // No active session to count down; surface 0 if a session exists but is expired.
            _uiState.value = _uiState.value.copy(
                liveRemainingSeconds = if (session == null) null else 0
            )
            return
        }

        anchorRemainingSeconds = remaining
        anchorElapsedMillis = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(liveRemainingSeconds = remaining)

        countdownJob = viewModelScope.launch {
            while (isActive) {
                val elapsedSecs = ((System.currentTimeMillis() - anchorElapsedMillis) / 1000L).toInt()
                val left = (anchorRemainingSeconds - elapsedSecs).coerceAtLeast(0)
                _uiState.value = _uiState.value.copy(liveRemainingSeconds = left)
                if (left <= 0) {
                    // Mark the session expired locally and refresh from the server for the
                    // authoritative status.
                    _uiState.value.session?.let { current ->
                        _uiState.value = _uiState.value.copy(
                            session = current.copy(status = "expired", is_expired = true)
                        )
                    }
                    refreshSessionStatus()
                    break
                }
                delay(1000L)
            }
        }
    }

    fun onTableCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(
            tableCode = value.uppercase(Locale.ROOT),
            errorMessage = ""
        )
    }

    fun loadTables() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTablesLoading = true, errorMessage = "")
            val result = repository.listTables(currentUserId)

            result.onSuccess { tables ->
                _uiState.value = _uiState.value.copy(
                    isTablesLoading = false,
                    availableTables = tables
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isTablesLoading = false,
                    errorMessage = error.message ?: "Khong lay duoc danh sach ban"
                )
            }
        }
    }

    fun checkTable() {
        val tableCode = _uiState.value.tableCode.trim()
        if (tableCode.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui long nhap ma ban")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = repository.checkTable(tableCode, currentUserId)

            result.onSuccess { response ->
                val session = response.session
                if (session == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        table = response.table,
                        session = null,
                        createSessionResult = null,
                        cartItems = emptyList(),
                        orderHistory = emptyList(),
                        step = UserOrderStep.COMBO_SETUP
                    )
                    startCountdown(null)
                    loadCombos()
                } else {
                    routeIntoSession(session, response.table)
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Kiem tra ban that bai"
                )
            }
        }
    }

    /**
     * Auto-resumes the current user's single active session (AC6). Call after login / cold-start
     * restore. If the user has an active session, routes straight into it (ACTIVE_MENU when
     * active/paid, WAITING_PAYMENT for an unpaid QR session); otherwise leaves the user on the
     * table picker (TABLE_ENTRY).
     */
    fun resumeActiveSession() {
        if (currentUserId <= 0) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = repository.getMyActiveSession(currentUserId)

            result.onSuccess { session ->
                if (session != null) {
                    val resumedTable = UserTableDto(
                        id = session.table_id,
                        table_code = session.table_code ?: "",
                        table_name = session.table_name ?: (session.table_code ?: ""),
                        status = "occupied",
                        is_mine = true
                    )
                    routeIntoSession(session, resumedTable)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }.onFailure {
                // No reachable session info — fall back to the table picker without surfacing an error.
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Shared resume routing used by both [checkTable] and [resumeActiveSession]: drops the user into
     * the screen matching the session's state and kicks off the matching data load + countdown.
     */
    private fun routeIntoSession(session: UserSessionDto, table: UserTableDto?) {
        when {
            session.status == "active" && session.is_expired != true && session.payment_status != "paid" -> {
                // Active QR session still awaiting payment — resume the waiting screen.
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    table = table,
                    session = session,
                    cartItems = emptyList(),
                    step = UserOrderStep.WAITING_PAYMENT,
                    vietqrPayload = session.vietqr_payload ?: _uiState.value.vietqrPayload,
                    bankAccountNo = session.bank_account_no ?: _uiState.value.bankAccountNo,
                    bankAccountName = session.bank_account_name ?: _uiState.value.bankAccountName,
                    bankNameOrBin = session.bank_name_or_bin ?: _uiState.value.bankNameOrBin
                )
            }

            session.status == "active" && session.is_expired != true -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    table = table,
                    session = session,
                    cartItems = emptyList(),
                    step = UserOrderStep.ACTIVE_MENU
                )
                startCountdown(session)
                loadMenu(session.combo_id)
            }

            else -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    table = table,
                    session = session,
                    cartItems = emptyList(),
                    step = UserOrderStep.ACTIVE_MENU,
                    errorMessage = "Phien buffet da het thoi gian"
                )
                startCountdown(session)
                loadOrderHistory(session.id)
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

    fun onSearchQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(searchQuery = value, errorMessage = "")
    }

    fun onCategorySelected(categoryName: String?) {
        _uiState.value = _uiState.value.copy(selectedCategoryName = categoryName, errorMessage = "")
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
                _uiState.value = state.copy(errorMessage = "So khach tinh tien phai lon hon 0")
                return
            }

            state.paymentMethod !in listOf("cash", "qr") -> {
                _uiState.value = state.copy(errorMessage = "Vui long chon phuong thuc thanh toan")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")
            val result = repository.createSession(
                tableCode = tableCode,
                comboId = comboId,
                paidGuestCount = paidGuests,
                freeChildCount = freeChildren,
                paymentMethod = state.paymentMethod,
                userId = currentUserId
            )

            result.onSuccess { created ->
                // Cash is collected by staff so it opens already paid; QR opens UNPAID and must wait
                // for the server-confirmed gateway callback before the menu unlocks.
                val isQr = state.paymentMethod == "qr"
                val resolvedPaymentStatus = created.session?.payment_status
                    ?: if (isQr) "unpaid" else "paid"
                val session = created.session ?: UserSessionDto(
                    id = created.session_id,
                    table_id = created.table_id,
                    combo_id = comboId,
                    paid_guest_count = paidGuests,
                    free_child_count = freeChildren,
                    payment_method = state.paymentMethod,
                    payment_status = resolvedPaymentStatus,
                    status = "active",
                    total_amount = created.total_amount,
                    start_time = null,
                    table_code = tableCode,
                    table_name = state.table?.table_name,
                    combo_name = state.selectedCombo?.name
                )

                if (session.payment_status == "paid") {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        session = session,
                        createSessionResult = created,
                        step = UserOrderStep.ACTIVE_MENU,
                        successMessage = "Đã thanh toán, bắt đầu phiên buffet 100 phút"
                    )
                    startCountdown(session)
                    loadMenu(session.combo_id)
                } else {
                    // QR: hold on the waiting screen until the server reports payment_status = 'paid'.
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        session = session,
                        createSessionResult = created,
                        step = UserOrderStep.WAITING_PAYMENT,
                        successMessage = "",
                        vietqrPayload = created.vietqr_payload ?: session.vietqr_payload,
                        bankAccountNo = created.bank_account_no ?: session.bank_account_no,
                        bankAccountName = created.bank_account_name ?: session.bank_account_name,
                        bankNameOrBin = created.bank_name_or_bin ?: session.bank_name_or_bin
                    )
                }
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
            val result = repository.getSessionStatus(sessionId)

            result.onSuccess { session ->
                val onWaitingScreen = _uiState.value.step == UserOrderStep.WAITING_PAYMENT
                val isPaid = session.payment_status == "paid"

                // While waiting on a QR payment, only the SERVER-confirmed paid status may advance
                // the customer into the active menu. An unpaid poll keeps us on the waiting screen.
                if (onWaitingScreen && !isPaid) {
                    _uiState.value = _uiState.value.copy(
                        session = session,
                        vietqrPayload = session.vietqr_payload ?: _uiState.value.vietqrPayload,
                        bankAccountNo = session.bank_account_no ?: _uiState.value.bankAccountNo,
                        bankAccountName = session.bank_account_name ?: _uiState.value.bankAccountName,
                        bankNameOrBin = session.bank_name_or_bin ?: _uiState.value.bankNameOrBin
                    )
                    return@onSuccess
                }

                _uiState.value = _uiState.value.copy(
                    session = session,
                    step = UserOrderStep.ACTIVE_MENU,
                    successMessage = if (onWaitingScreen && isPaid) "Đã xác nhận thanh toán" else _uiState.value.successMessage,
                    errorMessage = if (session.is_expired == true || session.status == "expired") {
                        "Phiên buffet đã hết thời gian"
                    } else {
                        ""
                    }
                )
                startCountdown(session)
                if (session.is_expired != true && session.status == "active") {
                    loadMenu(session.combo_id)
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Không cập nhật được trạng thái"
                )
            }
        }
    }

    /**
     * DEV/DEMO ONLY. Asks the mock payment gateway to report a payment for the waiting session.
     * The customer tapping this does NOT set paid status; the server validates and flips it, and the
     * waiting screen's poll of [refreshSessionStatus] then drives the navigation to the active menu.
     */
    fun simulatePayment() {
        val sessionId = _uiState.value.session?.id ?: _uiState.value.createSessionResult?.session_id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSimulatingPayment = true, errorMessage = "")
            val result = repository.simulatePayment(sessionId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSimulatingPayment = false)
                // Don't trust the local result for navigation — re-read the authoritative server status.
                refreshSessionStatus()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSimulatingPayment = false,
                    errorMessage = error.message ?: "Giả lập thanh toán thất bại"
                )
            }
        }
    }

    fun loadMenu(comboId: Int? = _uiState.value.session?.combo_id) {
        val id = comboId ?: return
        if (_uiState.value.isSessionExpired) {
            return
        }

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
        countdownJob?.cancel()
        countdownJob = null
        _uiState.value = UserOrderUiState()
    }

    fun addToCart(menuItem: MenuItemDto) {
        if (_uiState.value.isSessionExpired) {
            _uiState.value = _uiState.value.copy(errorMessage = "Da het thoi gian goi mon")
            return
        }

        val currentCart = _uiState.value.cartItems.toMutableList()
        val existingItemIndex = currentCart.indexOfFirst { it.menuItem.id == menuItem.id }

        if (existingItemIndex >= 0) {
            val existingItem = currentCart[existingItemIndex]
            currentCart[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentCart.add(CartItem(menuItem = menuItem, quantity = 1, note = ""))
        }

        _uiState.value = _uiState.value.copy(cartItems = currentCart, errorMessage = "")
    }

    fun removeFromCart(menuItem: MenuItemDto) {
        val currentCart = _uiState.value.cartItems.filter { it.menuItem.id != menuItem.id }
        _uiState.value = _uiState.value.copy(cartItems = currentCart)
    }

    fun updateCartItemQuantity(menuItem: MenuItemDto, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(menuItem)
            return
        }

        val currentCart = _uiState.value.cartItems.toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.menuItem.id == menuItem.id }

        if (itemIndex >= 0) {
            currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = newQuantity)
            _uiState.value = _uiState.value.copy(cartItems = currentCart)
        }
    }

    fun updateCartItemNote(menuItem: MenuItemDto, note: String) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.menuItem.id == menuItem.id }

        if (itemIndex >= 0) {
            currentCart[itemIndex] = currentCart[itemIndex].copy(note = note)
            _uiState.value = _uiState.value.copy(cartItems = currentCart)
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = "", successMessage = "")
    }

    /** Consumes the one-shot order-success signal so the Snackbar does not re-fire on recomposition. */
    fun consumeOrderSuccess() {
        if (_uiState.value.orderSuccessCount != null) {
            _uiState.value = _uiState.value.copy(orderSuccessCount = null)
        }
    }

    fun loadOrderHistory() {
        loadOrderHistory(_uiState.value.session?.id ?: return)
    }

    private fun loadOrderHistory(sessionId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingHistory = true, errorMessage = "")
            val result = repository.getOrderHistory(sessionId)

            result.onSuccess { history ->
                _uiState.value = _uiState.value.copy(
                    isLoadingHistory = false,
                    orderHistory = history
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoadingHistory = false,
                    errorMessage = error.message ?: "Khong lay duoc lich su goi mon"
                )
            }
        }
    }

    fun refreshOrderHistory() {
        loadOrderHistory()
    }

    fun submitOrder() {
        val currentState = _uiState.value

        if (currentState.isSessionExpired) {
            _uiState.value = currentState.copy(errorMessage = "Da het thoi gian dung bua, khong the goi them mon")
            return
        }

        if (currentState.cartItems.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "Gio hang trong")
            return
        }

        val sessionId = currentState.session?.id
        if (sessionId == null) {
            _uiState.value = currentState.copy(errorMessage = "Phien khong hop le")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSubmittingOrder = true,
                errorMessage = "",
                successMessage = ""
            )

            val orderItems = currentState.cartItems.map { cartItem ->
                CreateOrderItemRequest(
                    food_id = cartItem.menuItem.id,
                    quantity = cartItem.quantity,
                    note = cartItem.note
                )
            }

            val request = CreateOrderRequest(
                session_id = sessionId,
                items = orderItems,
                note = ""
            )

            val result = orderRepository.createOrder(request)

            result.onSuccess {
                val sentCount = currentState.cartItems.sumOf { it.quantity }
                _uiState.value = _uiState.value.copy(
                    isSubmittingOrder = false,
                    successMessage = "Da gui $sentCount mon thanh cong",
                    orderSuccessCount = sentCount,
                    cartItems = emptyList()
                )
                loadOrderHistory(sessionId)
                viewModelScope.launch {
                    delay(3000)
                    _uiState.value = _uiState.value.copy(successMessage = "")
                }
            }.onFailure { error ->
                val errorMsg = error.message ?: "Khong the goi mon"
                _uiState.value = _uiState.value.copy(
                    isSubmittingOrder = false,
                    errorMessage = errorMsg
                )
                if (errorMsg.contains("het thoi gian", ignoreCase = true)) {
                    refreshSessionStatus()
                }
            }
        }
    }
}
