package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.AdminComboDto
import com.example.appgoimon.data.remote.CategoryDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.repository.CategoryRepository
import com.example.appgoimon.data.repository.ComboRepository
import com.example.appgoimon.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminComboUiState(
    val isLoading: Boolean = false,
    val combos: List<AdminComboDto> = emptyList(),
    val menuItems: List<MenuItemDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val editingComboId: Int? = null,
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val status: String = "active",
    val selectedFoodIds: Set<Int> = emptySet(),
    val foodSearch: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)

class AdminComboViewModel : ViewModel() {

    private val comboRepository = ComboRepository()
    private val foodRepository = FoodRepository()
    private val categoryRepository = CategoryRepository()

    private val _uiState = MutableStateFlow(AdminComboUiState())
    val uiState: StateFlow<AdminComboUiState> = _uiState

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            val comboResult = comboRepository.getCombos()
            val foodResult = foodRepository.getMenuItems()
            val categoryResult = categoryRepository.getCategories()

            val combos = comboResult.getOrNull()
            if (combos != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    combos = combos,
                    menuItems = foodResult.getOrNull() ?: _uiState.value.menuItems,
                    categories = categoryResult.getOrNull() ?: _uiState.value.categories
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = comboResult.exceptionOrNull()?.message ?: "Không lấy được combo"
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = "")
    }

    fun onPriceChange(value: String) {
        // Keep digits only so price_per_person stays a clean integer.
        _uiState.value = _uiState.value.copy(price = value.filter { it.isDigit() }, errorMessage = "")
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value, errorMessage = "")
    }

    fun onStatusChange(value: String) {
        _uiState.value = _uiState.value.copy(status = value, errorMessage = "")
    }

    fun onFoodSearchChange(value: String) {
        _uiState.value = _uiState.value.copy(foodSearch = value)
    }

    fun toggleFood(foodId: Int) {
        val current = _uiState.value.selectedFoodIds
        _uiState.value = _uiState.value.copy(
            selectedFoodIds = if (foodId in current) current - foodId else current + foodId,
            errorMessage = ""
        )
    }

    fun startCreate() {
        _uiState.value = _uiState.value.copy(
            editingComboId = null,
            name = "",
            price = "",
            description = "",
            status = "active",
            selectedFoodIds = emptySet(),
            foodSearch = "",
            errorMessage = "",
            successMessage = ""
        )
    }

    fun startEdit(combo: AdminComboDto) {
        val priceInt = (combo.price_per_person.toDoubleOrNull() ?: 0.0).toInt()
        _uiState.value = _uiState.value.copy(
            editingComboId = combo.id,
            name = combo.combo_name,
            price = priceInt.toString(),
            description = combo.description.orEmpty(),
            status = combo.status,
            selectedFoodIds = emptySet(),
            foodSearch = "",
            errorMessage = "",
            successMessage = ""
        )
        // Pre-check the combo's currently assigned dishes.
        viewModelScope.launch {
            comboRepository.getComboFoodIds(combo.id).onSuccess { ids ->
                if (_uiState.value.editingComboId == combo.id) {
                    _uiState.value = _uiState.value.copy(selectedFoodIds = ids.toSet())
                }
            }
        }
    }

    fun saveCombo() {
        val name = _uiState.value.name.trim()
        val price = _uiState.value.price.toIntOrNull() ?: 0
        val description = _uiState.value.description.trim()
        val status = _uiState.value.status
        val foodIds = _uiState.value.selectedFoodIds.toList()
        val editingId = _uiState.value.editingComboId

        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng nhập tên combo")
            return
        }
        if (price <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Giá combo phải lớn hơn 0")
            return
        }
        if (foodIds.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Combo cần ít nhất 1 món")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = "")
            val result = if (editingId == null) {
                comboRepository.createCombo(name, price, description, status, foodIds)
            } else {
                comboRepository.updateCombo(editingId, name, price, description, status, foodIds)
            }
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    editingComboId = null,
                    name = "",
                    price = "",
                    description = "",
                    status = "active",
                    selectedFoodIds = emptySet(),
                    foodSearch = "",
                    successMessage = "Đã lưu combo"
                )
                loadData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = error.message ?: "Lưu combo thất bại"
                )
            }
        }
    }

    fun deleteCombo(comboId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            comboRepository.deleteCombo(comboId).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Đã xóa combo")
                loadData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Xóa combo thất bại"
                )
            }
        }
    }
}
