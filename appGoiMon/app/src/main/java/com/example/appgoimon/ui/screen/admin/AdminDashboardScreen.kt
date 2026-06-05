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
fun AdminDashboardScreen(
    onTableClick: (Int) -> Unit,
    viewModel: AdminTableViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTables()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard bàn",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Danh sách bàn và trạng thái",
            style = MaterialTheme.typography.bodyLarge
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.tables) { table ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onTableClick(table.id)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${table.table_name} (${table.table_code})",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = "Trạng thái: ${table.status}")

                        if (table.session_id != null) {
                            Text(text = "Mã phiên: ${table.session_id}")
                            Text(text = "Combo: ${table.combo_name ?: "Chưa có"}")
                            Text(text = "Tổng tiền: ${table.total_amount ?: "0"}")
                        } else {
                            Text(text = "Chưa có phiên bàn")
                        }
                    }
                }
            }
        }
    }
}