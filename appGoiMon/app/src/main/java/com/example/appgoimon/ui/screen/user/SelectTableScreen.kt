package com.example.appgoimon.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.data.remote.UserTableDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.UserOrderUiState

private val AvailableGreen = Color(0xFF2E7D32)
private val AvailableBadgeBg = Color(0xFFE7F6EC)
private val OccupiedAmber = Color(0xFFB8860B)
private val OccupiedBadgeBg = Color(0xFFFFF3D6)

@Composable
fun SelectTableScreen(
    user: AuthUserDto,
    uiState: UserOrderUiState,
    onTableCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onTableSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    var manualEntryExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Xin chào, ${user.full_name ?: user.username}",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Chọn bàn để bắt đầu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedBrown
                    )
                }

                TextButton(onClick = onLogout) {
                    Text("Đăng xuất", color = OrangeAccent)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TableLegend()

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.errorMessage.isNotEmpty()) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            when {
                uiState.isTablesLoading && uiState.availableTables.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AmberPrimaryDark)
                    }
                }

                uiState.availableTables.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chưa có bàn nào. Vui lòng nhập mã bàn thủ công.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedBrown
                        )
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.availableTables,
                            key = { it.id }
                        ) { table ->
                            TableCard(
                                table = table,
                                enabled = !uiState.isLoading,
                                onClick = { onTableSelected(table.table_code) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ManualEntrySection(
                expanded = manualEntryExpanded,
                onToggle = { manualEntryExpanded = !manualEntryExpanded },
                tableCode = uiState.tableCode,
                isLoading = uiState.isLoading,
                onTableCodeChange = onTableCodeChange,
                onSubmit = onSubmit
            )
        }
    }
}

@Composable
private fun TableLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        LegendItem(color = AvailableGreen, label = "Trống")
        LegendItem(color = OccupiedAmber, label = "Đang dùng")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = InkBrown
        )
    }
}

@Composable
private fun TableCard(
    table: UserTableDto,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val isAvailable = table.status == "available"
    // A table whose active session belongs to this user is tappable to resume (AC7), even though
    // it is "occupied". Tables occupied by other guests stay locked.
    val isMine = table.is_mine
    val tappable = (isAvailable || isMine) && enabled
    val cardModifier = if (tappable) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(cardModifier),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isAvailable || isMine) Color.White else Color(0xFFF1ECE2))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = table.table_name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isAvailable || isMine) InkBrown else MutedBrown,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = table.table_code,
                style = MaterialTheme.typography.bodySmall,
                color = MutedBrown
            )
            TableStatusBadge(isAvailable = isAvailable, isMine = isMine)
        }
    }
}

@Composable
private fun TableStatusBadge(isAvailable: Boolean, isMine: Boolean) {
    val (label, badgeColor, textColor) = when {
        isMine -> Triple("Bàn của bạn — Tiếp tục", AvailableBadgeBg, AvailableGreen)
        isAvailable -> Triple("Trống", AvailableBadgeBg, AvailableGreen)
        else -> Triple("Đang dùng", OccupiedBadgeBg, InkBrown)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = badgeColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ManualEntrySection(
    expanded: Boolean,
    onToggle: () -> Unit,
    tableCode: String,
    isLoading: Boolean,
    onTableCodeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onToggle) {
            Text(
                text = if (expanded) "Ẩn nhập mã bàn thủ công" else "Nhập mã bàn thủ công",
                color = OrangeAccent
            )
        }

        if (expanded) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Mã bàn",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = tableCode,
                        onValueChange = onTableCodeChange,
                        label = { Text("Ví dụ B01") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = tableCode.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimaryDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text("Kiểm tra bàn")
                        }
                    }
                }
            }
        }
    }
}
