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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.data.remote.PendingOrderItemDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminOrderViewModel

@Composable
fun ManageOrderScreen(
    viewModel: AdminOrderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPendingOrders()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mon cho xu ly",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.pendingItems.size} mon",
                    color = MutedBrown
                )
            }
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
                    onRetry = viewModel::loadPendingOrders
                )
            }
        }

        if (uiState.successMessage.isNotEmpty()) {
            item {
                Text(uiState.successMessage, color = AmberPrimaryDark)
            }
        }

        if (!uiState.isLoading && uiState.pendingItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Khong co mon dang cho duyet",
                        modifier = Modifier.padding(16.dp),
                        color = MutedBrown
                    )
                }
            }
        }

        items(uiState.pendingItems) { item ->
            PendingOrderCard(
                item = item,
                isBusy = uiState.actionItemId == item.order_item_id,
                onApprove = { viewModel.approveOrderItem(item.order_item_id) },
                onReject = { viewModel.rejectOrderItem(item.order_item_id) },
                onServed = { viewModel.markItemServed(item.order_item_id) }
            )
        }
    }
}

@Composable
private fun PendingOrderCard(
    item: PendingOrderItemDto,
    isBusy: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onServed: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.food_name,
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.table_name} (${item.table_code})",
                        color = MutedBrown
                    )
                }
                StatusBadge(item.status)
            }

            Text("So luong: ${item.quantity}", color = MutedBrown)
            if (!item.note.isNullOrBlank()) {
                Text("Ghi chu: ${item.note}", color = OrangeAccent)
            }
            Text("Tao luc: ${item.created_at ?: "-"}", color = MutedBrown)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    enabled = !isBusy,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Duyet")
                }
                OutlinedButton(
                    onClick = onReject,
                    enabled = !isBusy,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tu choi")
                }
            }

            OutlinedButton(
                onClick = onServed,
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Da phuc vu")
            }
        }
    }
}
