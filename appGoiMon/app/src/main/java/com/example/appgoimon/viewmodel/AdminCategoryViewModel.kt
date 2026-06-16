package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.CategoryDto
import com.example.appgoimon.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminCategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryDto> = emptyList(),
    val editingCategoryId: Int? = null,
    val name: String = "",
    val status: String = "active",
    val errorMessage: String = "",
    val successMessage: String = ""
)

class AdminCategoryViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _uiState = MutableStateFlow(AdminCategoryUiState())
    val uiState: StateFlow<AdminCategoryUiState> = _uiState

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            val result = repository.getCategories()
            result.onSuccess { categories ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    categories = categories
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Không lấy được danh mục"
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = "")
    }

    fun onStatusChange(value: String) {
        _uiState.value = _uiState.value.copy(status = value, errorMessage = "")
    }

    fun startCreate() {
        _uiState.value = _uiState.value.copy(
            editingCategoryId = null,
            name = "",
            status = "active",
            errorMessage = "",
            successMessage = ""
        )
    }

    fun startEdit(category: CategoryDto) {
        _uiState.value = _uiState.value.copy(
            editingCategoryId = category.id,
            name = category.category_name,
            status = category.status,
            errorMessage = "",
            successMessage = ""
        )
    }

    fun saveCategory() {
        val name = _uiState.value.name.trim()
        val status = _uiState.value.status
        val editingId = _uiState.value.editingCategoryId

        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng nhập tên danh mục")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = if (editingId == null) {
                repository.createCategory(name, status)
            } else {
                repository.updateCategory(editingId, name, status)
            }

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    editingCategoryId = null,
                    name = "",
                    status = "active",
                    successMessage = "Đã lưu danh mục"
                )
                loadCategories()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Lưu danh mục thất bại"
                )
            }
        }
    }

    fun deleteCategory(categoryId: Int) {
        updateCategory(categoryId, "Đã xóa danh mục") {
            repository.deleteCategory(categoryId)
        }
    }

    fun setCategoryStatus(categoryId: Int, status: String) {
        updateCategory(categoryId, "Đã cập nhật trạng thái") {
            repository.setCategoryStatus(categoryId, status)
        }
    }

    private fun updateCategory(
        categoryId: Int,
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
                loadCategories()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Cập nhật danh mục thất bại"
                )
            }
        }
    }
}
