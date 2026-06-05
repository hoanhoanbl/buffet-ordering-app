package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBackClick) {
            Text("← Quay lại")
        }

        Text(
            text = "Chi tiết bàn",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.errorMessage.isNotEmpty()) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.successMessage.isNotEmpty()) {
            Text(
                text = uiState.successMessage,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val detail = uiState.selectedTableSession

        if (detail != null) {
            val session = detail.session

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${session.table_name} (${session.table_code})",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Mã phiên: ${session.id}")
                    Text("Combo: ${session.combo_name ?: "Không có"}")
                    Text("Số khách tính tiền: ${session.paid_guest_count}")
                    Text("Trẻ em miễn phí: ${session.free_child_count}")
                    Text("Hình thức thanh toán: ${session.payment_method}")
                    Text("Trạng thái thanh toán: ${session.payment_status}")
                    Text("Trạng thái phiên: ${session.status}")
                    Text("Tổng tiền: ${session.total_amount}")
                    Text("Thời gian bắt đầu: ${session.start_time ?: "Chưa có"}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (session.payment_status != "paid" || session.status != "active") {
                Button(
                    onClick = {
                        viewModel.confirmPayment(
                            sessionId = session.id,
                            tableId = tableId
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Xác nhận thanh toán / Mở bàn")
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "Danh sách món đã gọi",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (detail.order_items.isEmpty()) {
                Text("Chưa có món nào được gọi")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(detail.order_items) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = item.food_name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Số lượng: ${item.quantity}")
                                Text("Ghi chú: ${item.note ?: ""}")
                                Text("Trạng thái món: ${item.item_status}")
                            }
                        }
                    }
                }
            }
        }
    }
}