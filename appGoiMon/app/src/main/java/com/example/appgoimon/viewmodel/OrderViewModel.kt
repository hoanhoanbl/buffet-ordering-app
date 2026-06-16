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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

enum class UserOrderStep {
    TABLE_ENTRY,
    COMBO_SETUP,
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

    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val isSessionExpired: Boolean
        get() = session?.is_expired == true || session?.status == "expired"

    val remainingMinutes: Int
        get() = session?.remaining_minutes ?: 0

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

    fun onTableCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(
            tableCode = value.uppercase(Locale.ROOT),
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
                            cartItems = emptyList(),
                            orderHistory = emptyList(),
                            step = UserOrderStep.COMBO_SETUP
                        )
                        loadCombos()
                    }

                    session.status == "active" && session.is_expired != true -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            table = response.table,
                            session = session,
                            cartItems = emptyList(),
                            step = UserOrderStep.ACTIVE_MENU
                        )
                        loadMenu(session.combo_id)
                    }

                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            table = response.table,
                            session = session,
                            cartItems = emptyList(),
                            step = UserOrderStep.ACTIVE_MENU,
                            errorMessage = "Phien buffet da het thoi gian"
                        )
                        loadOrderHistory(session.id)
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
                paymentMethod = state.paymentMethod
            )

            result.onSuccess { created ->
                val session = created.session ?: UserSessionDto(
                    id = created.session_id,
                    table_id = created.table_id,
                    combo_id = comboId,
                    paid_guest_count = paidGuests,
                    free_child_count = freeChildren,
                    payment_method = state.paymentMethod,
                    payment_status = "paid",
                    status = "active",
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
                    step = UserOrderStep.ACTIVE_MENU,
                    successMessage = "Da thanh toan, bat dau phien buffet 100 phut"
                )
                loadMenu(session.combo_id)
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
                _uiState.value = _uiState.value.copy(
                    session = session,
                    step = UserOrderStep.ACTIVE_MENU,
                    errorMessage = if (session.is_expired == true || session.status == "expired") {
                        "Phien buffet da het thoi gian"
                    } else {
                        ""
                    }
                )
                if (session.is_expired != true && session.status == "active") {
                    loadMenu(session.combo_id)
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Khong cap nhat duoc trang thai"
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
                _uiState.value = _uiState.value.copy(
                    isSubmittingOrder = false,
                    successMessage = "Da gui ${currentState.cartItems.sumOf { it.quantity }} mon thanh cong",
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
