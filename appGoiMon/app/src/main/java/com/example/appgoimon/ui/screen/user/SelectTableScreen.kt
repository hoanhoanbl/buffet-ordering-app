package com.example.appgoimon.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.UserOrderUiState

@Composable
fun SelectTableScreen(
    user: AuthUserDto,
    uiState: UserOrderUiState,
    onTableCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                )
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Xin chao, ${user.full_name ?: user.username}",
                    style = MaterialTheme.typography.titleLarge,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nhap ma ban de bat dau",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedBrown
                )
            }

            TextButton(onClick = onLogout) {
                Text("Dang xuat", color = OrangeAccent)
            }
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ma ban",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = uiState.tableCode,
                    onValueChange = onTableCodeChange,
                    label = { Text("Vi du B01") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                if (uiState.errorMessage.isNotEmpty()) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.tableCode.isNotBlank() && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberPrimaryDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Kiem tra ban")
                    }
                }
            }
        }
    }
}
