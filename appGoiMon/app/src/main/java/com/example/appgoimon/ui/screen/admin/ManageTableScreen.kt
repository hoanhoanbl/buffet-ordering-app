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
                Text("Quay lai")
            }
        }

        item {
            Text(
                text = "Chi tiet ban",
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
                Text("Ban nay chua co phien dang mo", color = MutedBrown)
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

                        DetailLine("Ma phien", session.id.toString())
                        DetailLine("Combo", session.combo_name ?: "Khong co")
                        DetailLine("Khach tinh tien", session.paid_guest_count.toString())
                        DetailLine("Tre em mien phi", session.free_child_count.toString())
                        DetailLine("Thanh toan", session.payment_method)
                        DetailLine("Trang thai thanh toan", session.payment_status)
                        DetailLine("Tong tien", formatCurrency(session.total_amount))
                        DetailLine("Bat dau", session.start_time ?: "Chua co")
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            Text("Xac nhan thanh toan")
                        }
                    }

                    if (session.status == "active") {
                        OutlinedButton(
                            onClick = {
                                viewModel.closeTable(session.id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Dong ban")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Mon da goi",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
            }

            if (detail.order_items.isEmpty()) {
                item {
                    Text("Chua co mon nao duoc goi", color = MutedBrown)
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
                            Text("So luong: ${item.quantity}", color = MutedBrown)
                            Text("Ghi chu: ${item.note.orEmpty()}", color = MutedBrown)
                            Text("Trang thai: ${item.item_status}", color = OrangeAccent)
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
