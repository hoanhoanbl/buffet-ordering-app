package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.CategoryDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.repository.CategoryRepository
import com.example.appgoimon.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminFoodUiState(
    val isLoading: Boolean = false,
    val menuItems: List<MenuItemDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val editingFoodId: Int? = null,
    val categoryId: Int? = null,
    val name: String = "",
    val image: String = "",
    val description: String = "",
    val status: String = "available",
    val errorMessage: String = "",
    val successMessage: String = ""
)

class AdminFoodViewModel : ViewModel() {

    private val foodRepository = FoodRepository()
    private val categoryRepository = CategoryRepository()

    private val _uiState = MutableStateFlow(AdminFoodUiState())
    val uiState: StateFlow<AdminFoodUiState> = _uiState

    fun loadMenuData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            val foodResult = foodRepository.getMenuItems()
            val categoryResult = categoryRepository.getCategories()

            val foods = foodResult.getOrNull()
            val categories = categoryResult.getOrNull()

            if (foods != null && categories != null) {
                val currentCategory = _uiState.value.categoryId
                val firstActiveCategory = categories.firstOrNull { it.status == "active" }?.id
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    menuItems = foods,
                    categories = categories,
                    categoryId = currentCategory ?: firstActiveCategory
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = foodResult.exceptionOrNull()?.message
                        ?: categoryResult.exceptionOrNull()?.message
                        ?: "Khong lay duoc du lieu menu"
                )
            }
        }
    }

    fun onCategoryChange(value: Int) {
        _uiState.value = _uiState.value.copy(categoryId = value, errorMessage = "")
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = "")
    }

    fun onImageChange(value: String) {
        _uiState.value = _uiState.value.copy(image = value, errorMessage = "")
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value, errorMessage = "")
    }

    fun onStatusChange(value: String) {
        _uiState.value = _uiState.value.copy(status = value, errorMessage = "")
    }

    fun startCreate() {
        _uiState.value = _uiState.value.copy(
            editingFoodId = null,
            categoryId = _uiState.value.categories.firstOrNull { it.status == "active" }?.id,
            name = "",
            image = "",
            description = "",
            status = "available",
            errorMessage = "",
            successMessage = ""
        )
    }

    fun startEdit(item: MenuItemDto) {
        _uiState.value = _uiState.value.copy(
            editingFoodId = item.id,
            categoryId = item.category_id,
            name = item.name,
            image = item.image.orEmpty(),
            description = item.description.orEmpty(),
            status = item.status,
            errorMessage = "",
            successMessage = ""
        )
    }

    fun saveMenuItem() {
        val categoryId = _uiState.value.categoryId
        val name = _uiState.value.name.trim()
        val image = _uiState.value.image.trim()
        val description = _uiState.value.description.trim()
        val status = _uiState.value.status
        val editingId = _uiState.value.editingFoodId

        if (categoryId == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui long chon danh muc")
            return
        }

        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui long nhap ten mon")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = if (editingId == null) {
                foodRepository.createMenuItem(categoryId, name, image, description, status)
            } else {
                foodRepository.updateMenuItem(editingId, categoryId, name, image, description, status)
            }

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    editingFoodId = null,
                    name = "",
                    image = "",
                    description = "",
                    status = "available",
                    successMessage = "Da luu mon"
                )
                loadMenuData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Luu mon that bai"
                )
            }
        }
    }

    fun deleteMenuItem(foodId: Int) {
        updateMenuItem("Da an mon") {
            foodRepository.deleteMenuItem(foodId)
        }
    }

    fun setMenuItemStatus(foodId: Int, status: String) {
        updateMenuItem("Da cap nhat trang thai mon") {
            foodRepository.setMenuItemStatus(foodId, status)
        }
    }

    private fun updateMenuItem(
        successMessage: String,
        action: suspend () -> Result<*>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = action()
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = successMessage
                )
                loadMenuData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Cap nhat mon that bai"
                )
            }
        }
    }
}
