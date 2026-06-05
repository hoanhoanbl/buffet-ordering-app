package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.AdminUserDto
import com.example.appgoimon.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminLoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val adminUser: AdminUserDto? = null,
    val isLoginSuccess: Boolean = false
)

class AdminLoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState: StateFlow<AdminLoginUiState> = _uiState

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            username = value,
            errorMessage = ""
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            errorMessage = ""
        )
    }

    fun loginAdmin() {
        val username = _uiState.value.username.trim()
        val password = _uiState.value.password.trim()

        if (username.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Vui lòng nhập đầy đủ tài khoản và mật khẩu"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = ""
            )

            val result = authRepository.loginAdmin(username, password)

            result.onSuccess { admin ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    adminUser = admin,
                    isLoginSuccess = true,
                    errorMessage = ""
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Đăng nhập thất bại"
                )
            }
        }
    }

    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(
            isLoginSuccess = false
        )
    }
}