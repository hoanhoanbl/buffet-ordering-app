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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Da goi", fontWeight = FontWeight.Bold, color = InkBrown)
                        Text(
                            "Cac luot goi mon trong phien ban",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedBrown
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoadingHistory,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF7E8), Color(0xFFFFE7BB))
                    )
                )
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
                                Text("Thu lai")
                            }
                        }
                    }
                }

                uiState.orderHistory.isEmpty() -> {
                    CenterBox {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "Chua co mon nao duoc goi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkBrown
                            )
                            Text(
                                "Mon sau khi bam goi se nam o day",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedBrown
                            )
                        }
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
                        text = "Goi luc ${formatOrderTime(order.created_at)}",
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

                QuantityPill(text = "$itemCount mon")
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
                    Text("Anh", style = MaterialTheme.typography.labelSmall, color = MutedBrown)
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
                    text = "Ghi chu: ${item.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        QuantityPill(text = "x${item.quantity}")
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
