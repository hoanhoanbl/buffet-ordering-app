package com.example.appgoimon.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
fun WaitingPaymentScreen(
    user: AuthUserDto,
    uiState: UserOrderUiState,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Cho xac nhan",
                    style = MaterialTheme.typography.headlineSmall,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(user.full_name ?: user.username, color = MutedBrown)
            }
            TextButton(onClick = onLogout) {
                Text("Dang xuat", color = OrangeAccent)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val session = uiState.session
                Text(
                    text = uiState.table?.table_name ?: "Ban ${uiState.tableCode}",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text("Combo: ${session?.combo_name ?: uiState.selectedCombo?.name ?: "-"}", color = MutedBrown)
                Text("Khach tra phi: ${session?.paid_guest_count ?: uiState.paidGuestCount}", color = MutedBrown)
                Text("Tre em mien phi: ${session?.free_child_count ?: uiState.freeChildCount}", color = MutedBrown)
                Text("Tong tien: ${formatUserCurrency(session?.total_amount ?: uiState.createSessionResult?.total_amount ?: uiState.totalPreview.toString())}", color = AmberPrimaryDark)
                Text("Trang thai: ${session?.status ?: uiState.createSessionResult?.status ?: "pending_payment"}", color = OrangeAccent)
                Text("Vui long cho admin xac nhan thanh toan.", color = MutedBrown)
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
        }
        if (uiState.successMessage.isNotEmpty()) {
            Text(uiState.successMessage, color = AmberPrimaryDark)
        }

        Button(
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = AmberPrimaryDark,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Lam moi trang thai")
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Quay lai")
        }
    }
}
