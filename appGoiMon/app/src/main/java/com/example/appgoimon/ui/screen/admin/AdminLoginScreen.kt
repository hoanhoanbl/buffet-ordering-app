package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.ui.theme.AmberPrimary
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminLoginViewModel

@Composable
fun AdminLoginScreen(
    onAuthSuccess: (AuthUserDto) -> Unit = {},
    viewModel: AdminLoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthSuccess, uiState.currentUser) {
        val user = uiState.currentUser
        if (uiState.isAuthSuccess && user != null) {
            onAuthSuccess(user)
            viewModel.resetAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE6B8),
                        Color(0xFFFFF7E8),
                        Color(0xFFFFD08A)
                    )
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "KichiKichi",
                        style = MaterialTheme.typography.labelLarge,
                        color = OrangeAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (uiState.isRegisterMode) "Dang ky" else "Dang nhap",
                        style = MaterialTheme.typography.headlineMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.isRegisterMode) {
                    OutlinedTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::onFullNameChange,
                        label = { Text("Ho ten") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !uiState.isLoading
                    )

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = { Text("So dien thoai") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }

                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = viewModel::onUsernameChange,
                    label = { Text("Ten dang nhap") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Mat khau") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    visualTransformation = PasswordVisualTransformation()
                )

                if (uiState.isRegisterMode) {
                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = { Text("Nhap lai mat khau") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

                if (uiState.errorMessage.isNotEmpty()) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = viewModel::submitAuth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberPrimaryDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(if (uiState.isRegisterMode) "Dang ky" else "Dang nhap")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.isRegisterMode) "Da co tai khoan?" else "Chua co tai khoan?",
                        color = MutedBrown
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = viewModel::toggleMode,
                        enabled = !uiState.isLoading
                    ) {
                        Text(
                            text = if (uiState.isRegisterMode) "Dang nhap" else "Dang ky",
                            color = AmberPrimary
                        )
                    }
                }
            }
        }
    }
}
