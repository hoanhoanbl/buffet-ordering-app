package com.example.appgoimon.ui.screen.admin

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminTableViewModel

@Composable
fun ManageTableScreen(
    tableId: Int,
    onBackClick: () -> Unit,
    viewModel: AdminTableViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tableId) {
        viewModel.loadTableSession(tableId)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            TextButton(onClick = onBackClick) {
                Text("Quay lại")
            }
        }

        item {
            Text(
                text = "Chi tiết bàn",
                style = MaterialTheme.typography.titleLarge,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.isLoading) {
            item {
                CircularProgressIndicator(color = AmberPrimaryDark)
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
                ErrorBlock(
                    message = uiState.errorMessage,
                    onRetry = { viewModel.loadTableSession(tableId) }
                )
            }
        }

        if (uiState.successMessage.isNotEmpty()) {
            item {
                Text(
                    text = uiState.successMessage,
                    color = AmberPrimaryDark,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        val detail = uiState.selectedTableSession
        if (detail == null && !uiState.isLoading) {
            item {
                Text("Bàn này chưa có phiên đang mở", color = MutedBrown)
            }
        }

        if (detail != null) {
            val session = detail.session
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${session.table_name} (${session.table_code})",
                                style = MaterialTheme.typography.titleMedium,
                                color = InkBrown,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(session.status)
                        }

                        DetailLine("Mã phiên", session.id.toString())
                        DetailLine("Combo", session.combo_name ?: "Không có")
                        DetailLine("Khách tính tiền", session.paid_guest_count.toString())
                        DetailLine("Trẻ em miễn phí", session.free_child_count.toString())
                        DetailLine("Thanh toán", statusLabel(session.payment_method ?: "-"))
                        DetailLine("Trạng thái thanh toán", statusLabel(session.payment_status ?: "-"))
                        DetailLine("Tổng tiền", formatCurrency(session.total_amount))
                        DetailLine("Bắt đầu", session.start_time ?: "Chưa có")
                        DetailLine("Kết thúc", session.end_time ?: "Chưa có")
                        DetailLine(
                            "Thời gian còn lại",
                            if (session.is_expired == true) "Hết giờ" else "${session.remaining_minutes ?: 0} phút"
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (detail.has_unfinished_items == true) {
                        Text(
                            "Bàn còn ${detail.unfinished_item_count ?: 0} món chưa phục vụ hoặc chưa từ chối",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (session.status == "active" || session.status == "expired") {
                        OutlinedButton(
                            onClick = {
                                viewModel.closeTable(session.id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Đóng bàn")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Món đã gọi",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
            }

            if (detail.order_items.isEmpty()) {
                item {
                    Text("Chưa có món nào được gọi", color = MutedBrown)
                }
            } else {
                items(detail.order_items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.food_name,
                                style = MaterialTheme.typography.titleMedium,
                                color = InkBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("Số lượng: ${item.quantity}", color = MutedBrown)
                            Text("Ghi chú: ${item.note.orEmpty()}", color = MutedBrown)
                            Text("Trạng thái: ${statusLabel(item.item_status)}", color = OrangeAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MutedBrown)
        Text(text = value, color = InkBrown, fontWeight = FontWeight.SemiBold)
    }
}
