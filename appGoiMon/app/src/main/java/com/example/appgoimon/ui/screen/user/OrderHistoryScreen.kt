package com.example.appgoimon.ui.screen.user

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.appgoimon.data.remote.OrderHistoryDto
import com.example.appgoimon.data.remote.OrderHistoryItemDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.UserOrderUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    uiState: UserOrderUiState,
    onLoadHistory: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) {
    LaunchedEffect(Unit) {
        onLoadHistory()
    }

    // No app bar here: the persistent UserMainScaffold top bar is the single header. We only add an
    // in-content title so it no longer stacks into a doubled header band.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF7E8), Color(0xFFFFE7BB))
                )
            )
    ) {
        SectionHeader(
            title = "Đã gọi",
            subtitle = "Các lượt gọi món trong phiên bàn"
        )
        PullToRefreshBox(
            isRefreshing = uiState.isLoadingHistory,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                uiState.isLoadingHistory && uiState.orderHistory.isEmpty() -> {
                    CenterBox {
                        CircularProgressIndicator(color = AmberPrimaryDark)
                    }
                }

                uiState.errorMessage.isNotEmpty() && uiState.orderHistory.isEmpty() -> {
                    CenterBox {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                            Button(onClick = onRetry) {
                                Text("Thử lại")
                            }
                        }
                    }
                }

                uiState.orderHistory.isEmpty() -> {
                    CenterBox {
                        EmptyHistoryState()
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(uiState.orderHistory) { order ->
                            OrderGroupCard(order = order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = InkBrown
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedBrown
        )
    }
}

@Composable
private fun EmptyHistoryState() {
    Column(
        modifier = Modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Soft concentric badge — keeps the empty state on-brand and intentional, matching the cart.
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
            "Chưa có món nào được gọi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = InkBrown,
            textAlign = TextAlign.Center
        )
        Text(
            "Các món bạn gửi cho bếp sẽ hiện ở đây cùng trạng thái chế biến.",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedBrown,
            textAlign = TextAlign.Center
        )

        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Text(
                text = "Chọn món ở Giỏ rồi bấm Gọi món",
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = OrangeAccent
            )
        }
    }
}

@Composable
private fun CenterBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun OrderGroupCard(order: OrderHistoryDto) {
    val itemCount = order.items.sumOf { it.quantity }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Gọi lúc ${formatOrderTime(order.created_at)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkBrown
                    )
                    Text(
                        text = order.created_at,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                QuantityPill(text = "$itemCount món")
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                order.items.forEach { item ->
                    OrderedItemRow(item = item)
                }
            }
        }
    }
}

@Composable
private fun OrderedItemRow(item: OrderHistoryItemDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageUrl = resolveFoodImageUrl(item.image)
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = item.food_name,
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,
            loading = {
                ImageFallbackBox {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = AmberPrimaryDark
                    )
                }
            },
            error = {
                ImageFallbackBox {
                    Text("Ảnh", style = MaterialTheme.typography.labelSmall, color = MutedBrown)
                }
            }
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = item.food_name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = InkBrown,
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
            OrderItemStatusChip(status = item.status)
        }

        QuantityPill(text = "x${item.quantity}")
    }
}

/**
 * Small per-item status chip mapping the order-item status enum returned by the backend
 * (pending / approved / processing / served / rejected) to a Vietnamese label and color.
 */
@Composable
private fun OrderItemStatusChip(status: String) {
    val (label, background, foreground) = when (status) {
        "pending" -> Triple("Chờ duyệt", Color(0xFFFFF3D6), Color(0xFFB7791F))
        "approved" -> Triple("Đã duyệt", Color(0xFFE3F0FF), Color(0xFF1D4ED8))
        "processing" -> Triple("Đang nấu", Color(0xFFFFE7CC), OrangeAccent)
        "served" -> Triple("Đã phục vụ", Color(0xFFDCF5E3), Color(0xFF15803D))
        "rejected" -> Triple("Từ chối", Color(0xFFFFE0DC), Color(0xFFC23B22))
        else -> Triple(status, Color(0xFFF1F1F1), MutedBrown)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = foreground
        )
    }
}

@Composable
private fun QuantityPill(text: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFFFFF0D6))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = OrangeAccent
        )
    }
}

@Composable
private fun ImageFallbackBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3D8)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
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

private fun formatOrderTime(createdAt: String): String {
    val timePart = createdAt.substringAfter(' ', createdAt)
    return timePart.take(5).ifBlank { createdAt }
}
