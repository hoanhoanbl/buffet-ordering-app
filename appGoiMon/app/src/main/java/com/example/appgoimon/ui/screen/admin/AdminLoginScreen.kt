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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminLoginViewModel

private val RedFg = Color(0xFFC23B22)
private val RedBg = Color(0xFFFFE0DC)

@Composable
fun AdminLoginScreen(
    onAuthSuccess: (AuthUserDto) -> Unit = {},
    viewModel: AdminLoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRegister = uiState.isRegisterMode

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
    ) {
        // Soft decorative blobs for depth behind the card.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .size(180.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.30f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 50.dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(OrangeAccent.copy(alpha = 0.12f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .statusBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SubcomposeAsyncImage(
                        model = "${RetrofitClient.BASE_URL}uploads/foods/logo.jpg",
                        contentDescription = "Logo",
                        modifier = Modifier
                            .width(180.dp)
                            .height(76.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = OrangeAccent, strokeWidth = 2.dp)
                            }
                        },
                        error = { Box(modifier = Modifier.fillMaxSize()) }
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = if (isRegister) "Tạo tài khoản" else "Đăng nhập",
                            style = MaterialTheme.typography.headlineMedium,
                            color = InkBrown,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isRegister) "Đăng ký để bắt đầu đặt món" else "Chào mừng bạn quay lại",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedBrown,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    if (isRegister) {
                        AuthField(
                            value = uiState.fullName,
                            onValueChange = viewModel::onFullNameChange,
                            label = "Họ tên",
                            leadingIcon = Icons.Default.Person,
                            enabled = !uiState.isLoading
                        )
                        AuthField(
                            value = uiState.phone,
                            onValueChange = viewModel::onPhoneChange,
                            label = "Số điện thoại",
                            leadingIcon = Icons.Default.Phone,
                            enabled = !uiState.isLoading,
                            keyboardType = KeyboardType.Phone
                        )
                    }

                    AuthField(
                        value = uiState.username,
                        onValueChange = viewModel::onUsernameChange,
                        label = "Tên đăng nhập",
                        leadingIcon = Icons.Default.AccountCircle,
                        enabled = !uiState.isLoading
                    )

                    AuthField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Mật khẩu",
                        leadingIcon = Icons.Default.Lock,
                        enabled = !uiState.isLoading,
                        isPassword = true
                    )

                    if (isRegister) {
                        AuthField(
                            value = uiState.confirmPassword,
                            onValueChange = viewModel::onConfirmPasswordChange,
                            label = "Nhập lại mật khẩu",
                            leadingIcon = Icons.Default.Lock,
                            enabled = !uiState.isLoading,
                            isPassword = true
                        )
                    }

                    if (uiState.errorMessage.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = RedBg
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = RedFg,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Button(
                        onClick = viewModel::submitAuth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeAccent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = if (isRegister) "Đăng ký" else "Đăng nhập",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isRegister) "Đã có tài khoản?" else "Chưa có tài khoản?",
                            color = MutedBrown
                        )
                        TextButton(
                            onClick = viewModel::toggleMode,
                            enabled = !uiState.isLoading
                        ) {
                            Text(
                                text = if (isRegister) "Đăng nhập" else "Đăng ký ngay",
                                color = OrangeAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Branded auth text field with a leading icon and, for password fields, a Hiện/Ẩn visibility toggle.
 * Each password field keeps its own visibility state.
 */
@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = enabled,
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = if (isPassword) {
            {
                TextButton(onClick = { visible = !visible }) {
                    Text(
                        text = if (visible) "Ẩn" else "Hiện",
                        color = OrangeAccent,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            null
        },
        visualTransformation = if (isPassword && !visible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrangeAccent,
            focusedLabelColor = OrangeAccent,
            focusedLeadingIconColor = OrangeAccent,
            cursorColor = OrangeAccent
        )
    )
}
