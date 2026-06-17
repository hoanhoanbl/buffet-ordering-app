package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.data.remote.OrderItemDto
import com.example.appgoimon.data.remote.TableSessionDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminTableViewModel

private val GreenFg = Color(0xFF15803D)
private val GreenBg = Color(0xFFE7F6EC)
private val RedFg = Color(0xFFC23B22)
private val RedBg = Color(0xFFFFE0DC)
private val AmberBg = Color(0xFFFFF0D6)

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TextButton(
                onClick = onBackClick,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Quay lại", color = OrangeAccent, fontWeight = FontWeight.SemiBold)
            }
        }

        if (uiState.isLoading) {
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
                    onRetry = { viewModel.loadTableSession(tableId) }
                )
            }
        }

        if (uiState.successMessage.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = GreenBg
                ) {
                    Text(
                        text = uiState.successMessage,
                        modifier = Modifier.padding(12.dp),
                        color = GreenFg,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        val detail = uiState.selectedTableSession
        if (detail == null && !uiState.isLoading) {
            item { EmptyTableDetail() }
        }

        if (detail != null) {
            val session = detail.session

            item { TableHeaderCard(session) }

            if (detail.has_unfinished_items == true) {
                item {
                    AttentionCallout(
                        "Bàn còn ${detail.unfinished_item_count ?: 0} món chưa phục vụ hoặc chưa từ chối."
                    )
                }
            }

            item { SessionInfoCard(session) }

            if (session.status == "active" || session.status == "expired") {
                // A table may only be closed once every item is served/rejected. Gate the button so
                // the rule is obvious here; the backend rejects it too as a safety net.
                val hasUnfinished = detail.has_unfinished_items == true
                item {
                    Button(
                        onClick = { viewModel.closeTable(session.id) },
                        enabled = !hasUnfinished && !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeAccent,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (hasUnfinished) "Phục vụ xong hết món mới đóng được" else "Đóng bàn",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Món đã gọi",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (detail.order_items.isEmpty()) {
                item {
                    Text(
                        text = "Chưa có món nào được gọi",
                        color = MutedBrown,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(detail.order_items) { item ->
                    OrderedItemCard(item)
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCard(session: TableSessionDto) {
    val expired = session.is_expired == true
    val paid = session.payment_status == "paid"
    val (timeFg, timeBg) = if (expired) RedFg to RedBg else GreenFg to GreenBg
    val (payFg, payBg) = if (paid) GreenFg to GreenBg else OrangeAccent to Color(0xFFFFE4D6)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.table_name,
                        style = MaterialTheme.typography.titleLarge,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Mã bàn ${session.table_code} · Phiên #${session.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown
                    )
                }
                StatusBadge(session.status)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HighlightTile(
                    label = "Tổng tiền",
                    value = formatCurrency(session.total_amount),
                    fg = OrangeAccent,
                    bg = AmberBg,
                    modifier = Modifier.weight(1f)
                )
                HighlightTile(
                    label = "Còn lại",
                    value = if (expired) "Hết giờ" else "${session.remaining_minutes ?: 0} phút",
                    fg = timeFg,
                    bg = timeBg,
                    modifier = Modifier.weight(1f)
                )
                HighlightTile(
                    label = "Thanh toán",
                    value = statusLabel(session.payment_status ?: "-"),
                    fg = payFg,
                    bg = payBg,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HighlightTile(
    label: String,
    value: String,
    fg: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MutedBrown
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = fg,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SessionInfoCard(session: TableSessionDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Thông tin phiên",
                style = MaterialTheme.typography.titleSmall,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            DetailLine("Combo", session.combo_name ?: "Không có")
            DetailLine("Khách tính tiền", "${session.paid_guest_count} khách")
            DetailLine("Trẻ em miễn phí", "${session.free_child_count} bé")
            DetailLine("Hình thức thanh toán", statusLabel(session.payment_method ?: "-"))
            DetailLine("Bắt đầu", formatDateTime(session.start_time))
            DetailLine("Kết thúc", formatDateTime(session.end_time))
        }
    }
}

@Composable
private fun AttentionCallout(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFF3E6))
            .border(1.dp, OrangeAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(OrangeAccent, CircleShape)
        )
        Text(
            text = message,
            color = AmberPrimaryDark,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OrderedItemCard(item: OrderItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.food_name,
                    style = MaterialTheme.typography.titleSmall,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
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
                StatusBadge(item.item_status)
            }

            Surface(shape = CircleShape, color = AmberBg) {
                Text(
                    text = "x${item.quantity}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = OrangeAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyTableDetail() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(GreenBg),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(GreenFg, CircleShape)
                )
            }
            Text(
                text = "Bàn đang trống",
                style = MaterialTheme.typography.titleMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bàn này chưa có phiên buffet đang mở.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedBrown
            )
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = MutedBrown,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            color = InkBrown,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

private fun formatDateTime(value: String?): String {
    if (value.isNullOrBlank()) return "Chưa có"
    val parts = value.split(" ")
    val datePart = parts.getOrNull(0) ?: return value
    val timePart = parts.getOrNull(1)?.take(5).orEmpty()
    val d = datePart.split("-")
    val shortDate = if (d.size == 3) "${d[2]}/${d[1]}" else datePart
    return if (timePart.isNotEmpty()) "$timePart · $shortDate" else shortDate
}
