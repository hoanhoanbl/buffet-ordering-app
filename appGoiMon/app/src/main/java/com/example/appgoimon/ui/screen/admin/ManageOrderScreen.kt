package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.data.remote.PendingOrderItemDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminOrderGroup
import com.example.appgoimon.viewmodel.AdminOrderViewModel

private val orderStatuses = listOf(
    "pending" to "Chờ duyệt",
    "approved" to "Đã duyệt",
    "served" to "Đã phục vụ",
    "rejected" to "Từ chối"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageOrderScreen(
    viewModel: AdminOrderViewModel = viewModel(),
    manualRefreshTick: Int = 0
) {
    val uiState by viewModel.uiState.collectAsState()
    val groups = uiState.orderGroups
    var showDatePicker by remember { mutableStateOf(false) }

    // Initial load + auto-polling every 9 seconds while this tab is visible.
    LaunchedEffect(Unit) {
        viewModel.loadPendingOrders()
        while (true) {
            delay(9_000L)
            viewModel.loadPendingOrders()
        }
    }

    // Manual refresh via the header button.
    LaunchedEffect(manualRefreshTick) {
        if (manualRefreshTick > 0) viewModel.loadPendingOrders()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OrderScreenSummary(
                groupCount = groups.size,
                itemCount = uiState.items.size,
                totalQuantity = uiState.items.sumOf { it.quantity }
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(orderStatuses) { (status, label) ->
                    FilterChip(
                        selected = uiState.selectedStatus == status,
                        onClick = { viewModel.selectStatus(status) },
                        label = {
                            Text(
                                text = label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = if (uiState.selectedStatus == status) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFE1D2),
                            selectedLabelColor = InkBrown
                        )
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF0D6)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = OrangeAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = dateChipLabel(uiState.selectedDate),
                            color = InkBrown,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                if (uiState.selectedDate != null) {
                    TextButton(onClick = { viewModel.selectDate(null) }) {
                        Text("Tất cả ngày", color = OrangeAccent)
                    }
                } else {
                    TextButton(onClick = { viewModel.selectDate(todayHcm()) }) {
                        Text("Về hôm nay", color = OrangeAccent)
                    }
                }
            }
        }

        // Show spinner only during the very first load (list is still empty).
        if (uiState.isLoading && uiState.isFirstLoad) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AmberPrimaryDark)
                }
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE7F6EC)
                ) {
                    Text(
                        text = uiState.successMessage,
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFF166534),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (!uiState.isLoading && groups.isEmpty()) {
            item {
                EmptyOrderState(label = currentStatusLabel(uiState.selectedStatus))
            }
        }

        items(groups) { group ->
            OrderGroupCard(
                group = group,
                isBusy = uiState.actionOrderId == group.orderId,
                actionInProgress = uiState.actionOrderId != null,
                onApprove = { viewModel.approveOrder(group.orderId) },
                onReject = { viewModel.rejectOrder(group.orderId) },
                onServed = { viewModel.markOrderServed(group.orderId) },
                onRejectItem = { orderItemId -> viewModel.rejectOrderItem(orderItemId) }
            )
        }
    }

    if (showDatePicker) {
        val initialMillis = uiState.selectedDate?.let { iso ->
            runCatching {
                LocalDate.parse(iso).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }.getOrNull()
        }
        val dateState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { viewModel.selectDate(millisToIsoDate(it)) }
                    showDatePicker = false
                }) {
                    Text("Chọn", color = OrangeAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy", color = MutedBrown)
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}

private fun todayHcm(): String = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).toString()

/** Convert the picker's UTC-midnight millis to the calendar day "yyyy-MM-dd" the user tapped. */
private fun millisToIsoDate(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()

/** Human label for the active date filter: "Tất cả ngày" / "Hôm nay" / "dd/MM/yyyy". */
private fun dateChipLabel(date: String?): String {
    if (date == null) return "Tất cả ngày"
    if (date == todayHcm()) return "Hôm nay"
    val parts = date.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else date
}

@Composable
private fun OrderScreenSummary(
    groupCount: Int,
    itemCount: Int,
    totalQuantity: Int
) {
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
                    .clip(CircleShape)
                    .background(Color(0xFFFFE1D2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.List,
                    contentDescription = null,
                    tint = OrangeAccent
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$groupCount lượt gọi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkBrown
                )
                Text(
                    text = "$itemCount dòng món, tổng $totalQuantity phần",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedBrown
                )
            }
        }
    }
}

@Composable
private fun EmptyOrderState(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF0D6)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE2AA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Text(
                text = "Không có lượt gọi $label",
                style = MaterialTheme.typography.titleLarge,
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Khi khách gửi món, mỗi lượt gọi sẽ hiển thị thành một khung riêng tại đây.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OrderGroupCard(
    group: AdminOrderGroup,
    isBusy: Boolean,
    actionInProgress: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onServed: () -> Unit,
    onRejectItem: (Int) -> Unit
) {
    val (accentFg, _) = orderAccent(group.status)
    // Pending orders are the actionable ones — give them a faint warm wash to pull the eye.
    val cardBg = if (group.status == "pending") Color(0xFFFFF8F2) else Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Status accent stripe via drawBehind — avoids IntrinsicSize, which mis-sizes around the
        // SubcomposeAsyncImage thumbnails (Coil doesn't support intrinsic measurement).
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(color = accentFg, size = Size(width = 5.dp.toPx(), height = size.height))
                }
                .padding(start = 19.dp, top = 14.dp, end = 14.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${group.tableName} (${group.tableCode})",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gọi lúc ${formatShortTime(group.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown
                    )
                }
                StatusBadge(group.status)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoPill("${group.itemCount} món")
                InfoPill("${group.totalQuantity} phần")
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                group.items.forEach { item ->
                    OrderItemLine(
                        item = item,
                        // Only a pending item can be rejected per-dish (e.g. it's out of stock);
                        // the rest of the order can still be approved with "Duyệt lượt".
                        showReject = group.status == "pending",
                        enabled = !actionInProgress,
                        onReject = { onRejectItem(item.order_item_id) }
                    )
                }
            }

            when (group.status) {
                "pending" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onApprove,
                            enabled = !isBusy,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                        ) {
                            if (isBusy) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text("Duyệt lượt")
                        }
                        OutlinedButton(
                            onClick = onReject,
                            enabled = !isBusy,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Từ chối")
                        }
                    }
                }

                "approved" -> {
                    Button(
                        onClick = onServed,
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimaryDark)
                    ) {
                        if (isBusy) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text("Đánh dấu đã phục vụ")
                    }
                }
            }
        }
    }
}

/** Maps an order status to (foreground, soft background) accent colors for the card stripe. */
private fun orderAccent(status: String): Pair<Color, Color> = when (status) {
    "pending" -> OrangeAccent to Color(0xFFFFE4D6)
    "approved" -> Color(0xFFB7791F) to Color(0xFFFFF3D6)
    "served" -> Color(0xFF15803D) to Color(0xFFE7F6EC)
    "rejected" -> Color(0xFFC23B22) to Color(0xFFFFE0DC)
    else -> Color(0xFF6D5A45) to Color(0xFFF1ECE3)
}

@Composable
private fun OrderItemLine(
    item: PendingOrderItemDto,
    showReject: Boolean,
    enabled: Boolean,
    onReject: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFAF0))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = resolveFoodImageUrl(item.image),
                contentDescription = item.food_name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                loading = { ImageFallbackBox() },
                error = { ImageFallbackBox() }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.food_name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!item.note.isNullOrBlank()) {
                    Text(
                        text = "Ghi chú: ${item.note}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFE1D2)
            ) {
                Text(
                    text = "x${item.quantity}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = OrangeAccent
                )
            }
        }

        // Per-item reject — for when this single dish is sold out. Approving the rest still works.
        if (showReject) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onReject,
                    enabled = enabled,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hết hàng · Từ chối món",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageFallbackBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3D8)),
        contentAlignment = Alignment.Center
    ) {
        Text("Ảnh", style = MaterialTheme.typography.labelSmall, color = MutedBrown)
    }
}

@Composable
private fun InfoPill(text: String) {
    Surface(
        shape = CircleShape,
        color = Color(0xFFFFF0D6)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = InkBrown,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun currentStatusLabel(status: String): String {
    return orderStatuses.firstOrNull { it.first == status }?.second?.lowercase() ?: ""
}

private fun formatShortTime(value: String?): String {
    if (value.isNullOrBlank()) return "-"
    val timePart = value.substringAfter(' ', value)
    return timePart.take(5).ifBlank { value }
}

private fun resolveFoodImageUrl(image: String?): String? {
    val value = image?.trim().orEmpty()
    if (value.isEmpty()) {
        return null
    }
    if (value.startsWith("http://") || value.startsWith("https://")) {
        return value
    }
    if (!value.contains('/')) {
        return RetrofitClient.BASE_URL + "uploads/foods/$value"
    }
    return RetrofitClient.BASE_URL + value.trimStart('/')
}
