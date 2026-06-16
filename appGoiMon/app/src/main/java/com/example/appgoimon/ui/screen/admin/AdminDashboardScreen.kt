package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.data.remote.DashboardStatsDto
import com.example.appgoimon.data.remote.TableDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminDashboardViewModel
import com.example.appgoimon.viewmodel.AdminTableViewModel
import java.text.NumberFormat
import java.util.Locale

private data class AdminTab(
    val title: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AdminDashboardScreen(
    onTableClick: (Int) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val tabs = remember {
        listOf(
            AdminTab("Tổng quan", "Tổng quan", Icons.Default.Home),
            AdminTab("Bàn", "Bàn", Icons.Default.Place),
            AdminTab("Đơn", "Đơn", Icons.Default.List),
            AdminTab("Menu", "Menu", Icons.Default.Menu)
        )
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTableId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            selectedTableId = null
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OrangeAccent,
                            selectedTextColor = InkBrown,
                            indicatorColor = Color(0xFFFFE1D2),
                            unselectedIconColor = MutedBrown,
                            unselectedTextColor = MutedBrown
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                    )
                )
                .padding(innerPadding)
        ) {
            AdminHeader(
                title = tabs[selectedTab].title,
                onLogout = onLogout
            )

            when (selectedTab) {
                0 -> DashboardTab()
                1 -> {
                    val tableId = selectedTableId
                    if (tableId == null) {
                        TablesTab(
                            onTableClick = { id ->
                                selectedTableId = id
                                onTableClick(id)
                            }
                        )
                    } else {
                        ManageTableScreen(
                            tableId = tableId,
                            onBackClick = { selectedTableId = null }
                        )
                    }
                }
                2 -> ManageOrderScreen()
                3 -> ManageFoodScreen()
            }
        }
    }
}

@Composable
private fun AdminHeader(
    title: String,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Admin",
                style = MaterialTheme.typography.labelLarge,
                color = OrangeAccent,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(onClick = onLogout) {
            Text("Đăng xuất", color = OrangeAccent, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DashboardTab(
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStats()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (uiState.isLoading) {
            item {
                CircularProgressIndicator(color = AmberPrimaryDark)
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
                ErrorBlock(
                    message = uiState.errorMessage,
                    onRetry = viewModel::loadStats
                )
            }
        }

        uiState.stats?.let { stats ->
            item {
                RevenueCard(stats)
            }
            item {
                Text(
                    text = "Vận hành hôm nay",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                MetricGrid(
                    metrics = listOf(
                        "Bàn trống" to stats.tables.available.toString(),
                        "Đang dùng" to stats.tables.occupied.toString(),
                        "Chờ thanh toán" to stats.tables.waiting_payment.toString(),
                        "Phiên mở" to stats.active_sessions.toString()
                    )
                )
            }
            item {
                MetricGrid(
                    metrics = listOf(
                        "Món chờ duyệt" to stats.order_items.pending.toString(),
                        "Đang xử lý" to stats.order_items.processing.toString(),
                        "Đã phục vụ" to stats.order_items.served.toString(),
                        "Danh mục mở" to stats.categories.active.toString()
                    )
                )
            }
            item {
                MetricGrid(
                    metrics = listOf(
                        "Tổng món" to stats.menu_items.total.toString(),
                        "Còn bán" to stats.menu_items.available.toString(),
                        "Hết món" to stats.menu_items.out_of_stock.toString(),
                        "Đã ẩn" to stats.menu_items.hidden.toString()
                    )
                )
            }
        }
    }
}

@Composable
private fun RevenueCard(stats: DashboardStatsDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Doanh thu",
                style = MaterialTheme.typography.titleMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatCurrency(stats.total_revenue),
                style = MaterialTheme.typography.headlineMedium,
                color = AmberPrimaryDark,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Hôm nay: ${formatCurrency(stats.today_revenue)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedBrown
            )
        }
    }
}

@Composable
private fun MetricGrid(metrics: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        metrics.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { metric ->
                    MetricCard(
                        label = metric.first,
                        value = metric.second,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MutedBrown
            )
        }
    }
}

@Composable
private fun TablesTab(
    onTableClick: (Int) -> Unit,
    viewModel: AdminTableViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTables()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (uiState.isLoading) {
            item {
                CircularProgressIndicator(color = AmberPrimaryDark)
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
                ErrorBlock(
                    message = uiState.errorMessage,
                    onRetry = viewModel::loadTables
                )
            }
        }

        item {
            TableSummaryCard(tables = uiState.tables)
        }

        items(uiState.tables) { table ->
            TableCard(
                table = table,
                onClick = { onTableClick(table.id) }
            )
        }
    }
}

@Composable
private fun TableCard(
    table: TableDto,
    onClick: () -> Unit
) {
    val isReleased = table.is_expired == true || table.session_status == "expired"
    val isAvailable = table.session_id == null || isReleased || table.status == "available"
    val effectiveStatus = if (isAvailable) "available" else table.status

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFFFF0D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = table.table_code.takeLast(2),
                    color = AmberPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = table.table_name,
                            style = MaterialTheme.typography.titleMedium,
                            color = InkBrown,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = table.table_code,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedBrown
                        )
                    }
                    StatusBadge(effectiveStatus)
                }

                if (isAvailable) {
                    Text(
                        text = "Sẵn sàng nhận khách mới",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedBrown
                    )
                } else {
                    Text(
                        text = table.combo_name ?: "Buffet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatCurrency(table.total_amount ?: "0"),
                            color = OrangeAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = statusLabel(table.payment_status ?: "-"),
                            color = MutedBrown,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "Còn lại ${table.remaining_minutes ?: 0} phút",
                        color = MutedBrown,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun TableSummaryCard(tables: List<TableDto>) {
    val available = tables.count { table ->
        table.session_id == null || table.is_expired == true || table.session_status == "expired" || table.status == "available"
    }
    val occupied = tables.size - available

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFE7F6EC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF15803D))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${tables.size} bàn",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$available trống, $occupied đang dùng",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedBrown
                )
            }
        }
    }
}

@Composable
fun StatusBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = when (text) {
            "available", "active" -> Color(0xFFE7F6EC)
            "occupied", "processing" -> Color(0xFFFFF3D6)
            "waiting_payment", "pending", "pending_payment" -> Color(0xFFFFE4D6)
            "inactive", "hidden", "rejected" -> Color(0xFFF1F1F1)
            else -> Color(0xFFEFE7DC)
        }
    ) {
        Text(
            text = statusLabel(text),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = InkBrown,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ErrorBlock(
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE8))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            if (onRetry != null) {
                Button(onClick = onRetry) {
                    Text("Thử lại")
                }
            }
        }
    }
}

fun formatCurrency(value: String): String {
    val amount = value.toDoubleOrNull() ?: 0.0
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
}

fun statusLabel(value: String): String {
    return when (value) {
        "available" -> "Trống"
        "active" -> "Đang mở"
        "occupied" -> "Đang dùng"
        "processing" -> "Đang xử lý"
        "waiting_payment", "pending_payment" -> "Chờ thanh toán"
        "pending" -> "Chờ duyệt"
        "approved" -> "Đã duyệt"
        "served" -> "Đã phục vụ"
        "rejected" -> "Từ chối"
        "inactive" -> "Tạm tắt"
        "hidden" -> "Đã ẩn"
        "out_of_stock" -> "Hết món"
        "paid" -> "Đã thanh toán"
        "cash" -> "Tiền mặt"
        "qr" -> "Mã QR"
        "-" -> "-"
        else -> value
    }
}
