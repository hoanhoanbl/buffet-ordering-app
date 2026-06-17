package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
    var manualRefreshTick by remember { mutableIntStateOf(0) }

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
                onLogout = onLogout,
                onRefresh = { manualRefreshTick++ }
            )

            when (selectedTab) {
                0 -> DashboardTab(manualRefreshTick = manualRefreshTick)
                1 -> {
                    val tableId = selectedTableId
                    if (tableId == null) {
                        TablesTab(
                            onTableClick = { id ->
                                selectedTableId = id
                                onTableClick(id)
                            },
                            manualRefreshTick = manualRefreshTick
                        )
                    } else {
                        ManageTableScreen(
                            tableId = tableId,
                            onBackClick = { selectedTableId = null }
                        )
                    }
                }
                2 -> ManageOrderScreen(manualRefreshTick = manualRefreshTick)
                3 -> ManageFoodScreen()
            }
        }
    }
}

@Composable
private fun AdminHeader(
    title: String,
    onLogout: () -> Unit,
    onRefresh: (() -> Unit)? = null
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
            if (onRefresh != null) {
                Text(
                    text = "Tự động cập nhật",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedBrown
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (onRefresh != null) {
                Surface(shape = CircleShape, color = Color(0xFFFFE1D2)) {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Làm mới",
                            tint = OrangeAccent
                        )
                    }
                }
            }
            TextButton(onClick = onLogout) {
                Text("Đăng xuất", color = OrangeAccent, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private const val POLL_INTERVAL_MS = 9_000L

@Composable
private fun DashboardTab(
    viewModel: AdminDashboardViewModel = viewModel(),
    manualRefreshTick: Int = 0
) {
    val uiState by viewModel.uiState.collectAsState()

    // Initial load + auto-polling every 9 seconds while this tab is visible.
    // Keyed on Unit so the coroutine starts once and cancels only on dispose.
    LaunchedEffect(Unit) {
        viewModel.loadStats()
        while (true) {
            delay(POLL_INTERVAL_MS)
            viewModel.loadStats()
        }
    }

    // Manual refresh via the header button increments the tick.
    LaunchedEffect(manualRefreshTick) {
        if (manualRefreshTick > 0) viewModel.loadStats()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Show spinner only during the very first load (list is still empty).
        if (uiState.isLoading && uiState.isFirstLoad) {
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
            item { RevenueHeroCard(stats) }
            item { AttentionCard(stats) }

            item { SectionHeader("Tình trạng bàn") }
            item { TableOccupancyCard(stats.tables) }

            item { SectionHeader("Đơn món hôm nay") }
            item {
                DonutChartCard(
                    centerLabel = "món",
                    slices = listOf(
                        ChartSlice("Chờ duyệt", stats.order_items.pending, OrangeAccent),
                        ChartSlice("Đang xử lý", stats.order_items.processing, AmberFg),
                        ChartSlice("Đã phục vụ", stats.order_items.served, GreenFg),
                        ChartSlice("Từ chối", stats.order_items.rejected, RedFg)
                    ),
                    emptyText = "Chưa có món nào được gọi hôm nay"
                )
            }

            item { SectionHeader("Thực đơn") }
            item {
                DonutChartCard(
                    centerLabel = "món",
                    slices = listOf(
                        ChartSlice("Còn bán", stats.menu_items.available, GreenFg),
                        ChartSlice("Hết món", stats.menu_items.out_of_stock, RedFg),
                        ChartSlice("Đã ẩn", stats.menu_items.hidden, NeutralFg)
                    ),
                    emptyText = "Chưa có món trong thực đơn",
                    footnote = "Danh mục đang mở: ${stats.categories.active}"
                )
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
        }
    }
}

// Semantic palette shared by the dashboard tiles so a glance maps color -> meaning.
private val GreenFg = Color(0xFF15803D)
private val GreenBg = Color(0xFFE7F6EC)
private val AmberFg = Color(0xFFB7791F)
private val AmberBg = Color(0xFFFFF3D6)
private val RedFg = Color(0xFFC23B22)
private val RedBg = Color(0xFFFFE0DC)
private val NeutralFg = Color(0xFF6D5A45)
private val NeutralBg = Color(0xFFF1ECE3)

private data class ChartSlice(
    val label: String,
    val value: Int,
    val color: Color
)

@Composable
private fun SectionHeader(text: String) {
    Row(
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(OrangeAccent)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = InkBrown,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Hero card: total revenue is the headline number; today's revenue and open sessions sit below. */
@Composable
private fun RevenueHeroCard(stats: DashboardStatsDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFF8A3D), Color(0xFFEF6321))
                    )
                )
        ) {
            // Decorative translucent orb for depth (clipped by the card's rounded corners).
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 36.dp, y = (-36).dp)
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tổng doanh thu",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.20f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(Color.White, CircleShape)
                            )
                            Text(
                                text = "Tự động cập nhật",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = formatCurrency(stats.total_revenue),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HeroSubStat(
                        label = "Hôm nay",
                        value = formatCurrency(stats.today_revenue),
                        modifier = Modifier.weight(1f)
                    )
                    HeroSubStat(
                        label = "Phiên đang mở",
                        value = "${stats.active_sessions}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSubStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.85f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Surfaces the only two numbers an admin must act on; collapses to a calm "all clear" otherwise. */
@Composable
private fun AttentionCard(stats: DashboardStatsDto) {
    val pending = stats.order_items.pending
    val waitingPayment = stats.tables.waiting_payment
    val allClear = pending == 0 && waitingPayment == 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allClear) GreenBg else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (allClear) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(GreenFg, CircleShape)
                    )
                    Text(
                        text = "Mọi thứ ổn định — không có việc cần xử lý ngay.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreenFg,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Text(
                    text = "Cần xử lý",
                    style = MaterialTheme.typography.titleSmall,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                if (pending > 0) {
                    AttentionRow(label = "Món chờ duyệt", count = pending)
                }
                if (waitingPayment > 0) {
                    AttentionRow(label = "Bàn chờ thanh toán", count = waitingPayment)
                }
            }
        }
    }
}

@Composable
private fun AttentionRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = InkBrown
        )
        Surface(shape = CircleShape, color = OrangeAccent) {
            Text(
                text = count.toString(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Table capacity at a glance: a single stacked bar split into available / occupied / waiting. */
@Composable
private fun TableOccupancyCard(tables: com.example.appgoimon.data.remote.DashboardTableStatsDto) {
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
                Text(
                    text = "Sức chứa bàn",
                    style = MaterialTheme.typography.titleSmall,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${tables.total} bàn",
                    style = MaterialTheme.typography.titleMedium,
                    color = OrangeAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(NeutralBg)
            ) {
                StackSegment(tables.available, GreenFg)
                StackSegment(tables.occupied, AmberFg)
                StackSegment(tables.waiting_payment, OrangeAccent)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Legend(GreenFg, "Trống", tables.available, Modifier.weight(1f))
                Legend(AmberFg, "Đang dùng", tables.occupied, Modifier.weight(1f))
                Legend(OrangeAccent, "Chờ TT", tables.waiting_payment, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RowScope.StackSegment(count: Int, color: Color) {
    if (count > 0) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(count.toFloat())
                .background(color)
        )
    }
}

@Composable
private fun Legend(color: Color, label: String, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Column {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MutedBrown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Composition card: a donut chart (drawn with Canvas — no chart library) plus a legend that keeps
 * every label + count + share, so it reads visually without losing the exact numbers.
 */
@Composable
private fun DonutChartCard(
    slices: List<ChartSlice>,
    centerLabel: String,
    emptyText: String,
    footnote: String? = null
) {
    val total = slices.sumOf { it.value }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (total == 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DonutChart(
                        slices = slices,
                        total = 0,
                        centerValue = "0",
                        centerLabel = centerLabel,
                        modifier = Modifier.size(110.dp)
                    )
                    Text(
                        text = emptyText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedBrown,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DonutChart(
                        slices = slices,
                        total = total,
                        centerValue = total.toString(),
                        centerLabel = centerLabel,
                        modifier = Modifier.size(110.dp)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        slices.forEach { slice ->
                            LegendRow(slice = slice, total = total)
                        }
                    }
                }
            }

            if (footnote != null) {
                HorizontalDivider(color = Color(0xFFF0E6D6))
                Text(
                    text = footnote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedBrown,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DonutChart(
    slices: List<ChartSlice>,
    total: Int,
    centerValue: String,
    centerLabel: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val thickness = size.minDimension * 0.18f
            val diameter = size.minDimension - thickness
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)
            val stroke = Stroke(width = thickness)

            if (total <= 0) {
                drawArc(
                    color = NeutralBg,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
            } else {
                var start = -90f
                slices.forEach { slice ->
                    if (slice.value > 0) {
                        val sweep = 360f * slice.value / total
                        drawArc(
                            color = slice.color,
                            startAngle = start,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = stroke
                        )
                        start += sweep
                    }
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerValue,
                style = MaterialTheme.typography.titleLarge,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MutedBrown
            )
        }
    }
}

@Composable
private fun LegendRow(slice: ChartSlice, total: Int) {
    val percent = if (total > 0) (slice.value * 100 / total) else 0
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(slice.color, CircleShape)
        )
        Text(
            text = slice.label,
            style = MaterialTheme.typography.bodyMedium,
            color = InkBrown,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${slice.value}",
            style = MaterialTheme.typography.bodyMedium,
            color = InkBrown,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "·  $percent%",
            style = MaterialTheme.typography.labelMedium,
            color = MutedBrown
        )
    }
}

@Composable
private fun TablesTab(
    onTableClick: (Int) -> Unit,
    viewModel: AdminTableViewModel = viewModel(),
    manualRefreshTick: Int = 0
) {
    val uiState by viewModel.uiState.collectAsState()

    // Initial load + auto-polling every 9 seconds while this tab is visible.
    LaunchedEffect(Unit) {
        viewModel.loadTables()
        while (true) {
            delay(POLL_INTERVAL_MS)
            viewModel.loadTables()
        }
    }

    // Manual refresh via the header button.
    LaunchedEffect(manualRefreshTick) {
        if (manualRefreshTick > 0) viewModel.loadTables()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Show spinner only during the very first load (list is still empty).
        if (uiState.isLoading && uiState.isFirstLoad) {
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
    val (accentFg, accentBg) = tableAccent(effectiveStatus)
    // Tables awaiting payment need attention — give the whole card a faint warm wash.
    val needsAttention = effectiveStatus == "waiting_payment" || effectiveStatus == "pending_payment"
    val cardBg = if (needsAttention) Color(0xFFFFF8F2) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Status accent stripe — lets the eye scan the column and spot active tables instantly.
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accentFg)
            )

            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(accentBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = table.table_code.takeLast(2),
                        color = accentFg,
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatCurrency(table.total_amount ?: "0"),
                                color = OrangeAccent,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = statusLabel(table.payment_status ?: "-"),
                                color = MutedBrown,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        // Remaining-time pill so the most time-sensitive number reads at a glance.
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = accentBg
                        ) {
                            Text(
                                text = "Còn lại ${table.remaining_minutes ?: 0} phút",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = accentFg,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Maps a table status to (foreground, soft background) accent colors, reusing the dashboard palette. */
private fun tableAccent(status: String): Pair<Color, Color> = when (status) {
    "available" -> GreenFg to GreenBg
    "occupied" -> AmberFg to AmberBg
    "waiting_payment", "pending_payment", "pending" -> OrangeAccent to Color(0xFFFFE4D6)
    else -> NeutralFg to NeutralBg
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(GreenBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = GreenFg)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${tables.size} bàn",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tổng quan khu vực",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown
                    )
                }
            }

            if (tables.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(50))
                        .background(NeutralBg)
                ) {
                    StackSegment(available, GreenFg)
                    StackSegment(occupied, AmberFg)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Legend(GreenFg, "Trống", available, Modifier.weight(1f))
                    Legend(AmberFg, "Đang dùng", occupied, Modifier.weight(1f))
                }
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
