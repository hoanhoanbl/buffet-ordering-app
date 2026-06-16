package com.example.appgoimon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isRegisterMode: Boolean = false,
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val currentUser: AuthUserDto? = null,
    val isAuthSuccess: Boolean = false
)

class AdminLoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isRegisterMode = !_uiState.value.isRegisterMode,
            password = "",
            confirmPassword = "",
            errorMessage = ""
        )
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value, errorMessage = "")
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = "")
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = "")
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, errorMessage = "")
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value, errorMessage = "")
    }

    fun submitAuth() {
        if (_uiState.value.isRegisterMode) {
            registerUser()
        } else {
            login()
        }
    }

    private fun login() {
        val username = _uiState.value.username.trim()
        val password = _uiState.value.password.trim()

        if (username.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Vui lòng nhập tài khoản và mật khẩu"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = authRepository.login(username, password)

            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = user,
                    isAuthSuccess = true,
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

    private fun registerUser() {
        val username = _uiState.value.username.trim()
        val password = _uiState.value.password.trim()
        val confirmPassword = _uiState.value.confirmPassword.trim()
        val fullName = _uiState.value.fullName.trim()
        val phone = _uiState.value.phone.trim()

        if (fullName.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Vui lòng nhập đầy đủ thông tin đăng ký"
            )
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
            )
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Mật khẩu xác nhận không khớp"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
            val result = authRepository.register(username, password, fullName, phone)

            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = user,
                    isAuthSuccess = true,
                    errorMessage = ""
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Đăng ký thất bại"
                )
            }
        }
    }

    fun resetAuthSuccess() {
        _uiState.value = _uiState.value.copy(isAuthSuccess = false)
    }
}
