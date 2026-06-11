package com.example.appgoimon.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appgoimon.data.remote.UserComboDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.UserOrderUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ComboAndGuestScreen(
    uiState: UserOrderUiState,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onRetryCombos: () -> Unit,
    onComboSelected: (Int) -> Unit,
    onPaidGuestChange: (String) -> Unit,
    onFreeChildChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onCreateSession: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                )
            ),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = uiState.table?.table_name ?: "Ban ${uiState.tableCode}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Chon combo va so khach", color = MutedBrown)
                }
                TextButton(onClick = onLogout) {
                    Text("Dang xuat", color = OrangeAccent)
                }
            }
        }

        item {
            TextButton(onClick = onBack) {
                Text("Chon ban khac")
            }
        }

        if (uiState.isLoading && uiState.combos.isEmpty()) {
            item {
                CircularProgressIndicator(color = AmberPrimaryDark)
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE8))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                        if (uiState.combos.isEmpty()) {
                            Button(onClick = onRetryCombos) {
                                Text("Thu lai")
                            }
                        }
                    }
                }
            }
        }

        if (!uiState.isLoading && uiState.combos.isEmpty()) {
            item {
                Text("Chua co combo dang hoat dong", color = MutedBrown)
            }
        }

        items(uiState.combos) { combo ->
            ComboCard(
                combo = combo,
                selected = uiState.selectedComboId == combo.id,
                onClick = { onComboSelected(combo.id) }
            )
        }

        item {
            GuestForm(
                uiState = uiState,
                onPaidGuestChange = onPaidGuestChange,
                onFreeChildChange = onFreeChildChange,
                onPaymentMethodChange = onPaymentMethodChange,
                onCreateSession = onCreateSession
            )
        }
    }
}

@Composable
private fun ComboCard(
    combo: UserComboDto,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFFFF1D0) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = combo.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatUserCurrency(combo.price_per_person),
                    color = AmberPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!combo.description.isNullOrBlank()) {
                Text(combo.description, color = MutedBrown)
            }
            Text(if (selected) "Dang chon" else "Cham de chon", color = OrangeAccent)
        }
    }
}

@Composable
private fun GuestForm(
    uiState: UserOrderUiState,
    onPaidGuestChange: (String) -> Unit,
    onFreeChildChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onCreateSession: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Thong tin khach",
                style = MaterialTheme.typography.titleMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.paidGuestCount,
                    onValueChange = onPaidGuestChange,
                    label = { Text("Khach tra phi") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = uiState.freeChildCount,
                    onValueChange = onFreeChildChange,
                    label = { Text("Tre em mien phi") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.paymentMethod == "cash",
                    onClick = { onPaymentMethodChange("cash") },
                    label = { Text("Tien mat") }
                )
                FilterChip(
                    selected = uiState.paymentMethod == "qr",
                    onClick = { onPaymentMethodChange("qr") },
                    label = { Text("QR") }
                )
            }

            Text(
                text = "Tam tinh: ${formatUserCurrency(uiState.totalPreview.toString())}",
                style = MaterialTheme.typography.titleMedium,
                color = AmberPrimaryDark,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onCreateSession,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.selectedComboId != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberPrimaryDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Tao phien")
                }
            }
        }
    }
}

fun formatUserCurrency(value: String): String {
    val amount = value.toDoubleOrNull() ?: 0.0
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
}
