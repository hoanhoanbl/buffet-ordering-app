package com.example.appgoimon.ui.screen.user

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.data.remote.UserComboDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.UserOrderUiState
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay

@Composable
fun ComboAndGuestScreen(
    uiState: UserOrderUiState,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onRetryCombos: () -> Unit,
    onComboSelected: (Int) -> Unit,
    onPaidGuestChange: (String) -> Unit,
    onFreeChildChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onCreateSession: () -> Unit
) {
    val combos = uiState.combos
    val selectedIndex = combos.indexOfFirst { it.id == uiState.selectedComboId }.coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = { combos.size }
    )

    LaunchedEffect(combos) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            combos.getOrNull(page)?.id?.let { comboId ->
                if (comboId != uiState.selectedComboId) {
                    onComboSelected(comboId)
                }
            }
        }
    }

    LaunchedEffect(uiState.selectedComboId, combos.size) {
        if (combos.isNotEmpty() && selectedIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(selectedIndex)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                )
            )
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ComboHeader(
                tableName = uiState.table?.table_name ?: "Bàn ${uiState.tableCode}",
                onBack = onBack,
                onLogout = onLogout
            )
        }

        if (uiState.isLoading && combos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AmberPrimaryDark)
                }
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
                ErrorCard(
                    message = uiState.errorMessage,
                    canRetry = combos.isEmpty(),
                    onRetry = onRetryCombos
                )
            }
        }

        if (!uiState.isLoading && combos.isEmpty()) {
            item {
                EmptyComboCard()
            }
        }

        if (combos.isNotEmpty()) {
            item {
                ComboCarousel(
                    combos = combos,
                    selectedComboId = uiState.selectedComboId,
                    pagerState = pagerState,
                    onComboSelected = onComboSelected
                )
            }

            item {
                GuestPaymentPanel(
                    uiState = uiState,
                    onPaidGuestChange = onPaidGuestChange,
                    onFreeChildChange = onFreeChildChange,
                    onPaymentMethodChange = onPaymentMethodChange,
                    onCreateSession = onCreateSession
                )
            }
        }
    }
}

@Composable
private fun ComboHeader(
    tableName: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tableName,
                style = MaterialTheme.typography.headlineMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                Text("Chọn bàn khác", color = AmberPrimaryDark, fontWeight = FontWeight.SemiBold)
            }
        }
        TextButton(onClick = onLogout) {
            Text("Đăng xuất", color = OrangeAccent, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ComboCarousel(
    combos: List<UserComboDto>,
    selectedComboId: Int?,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onComboSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 8.dp),
            pageSpacing = 14.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
        ) { page ->
            val combo = combos[page]
            val selected = selectedComboId == combo.id
            val pageOffset = (
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            ).absoluteValue
            val scale = 1f - (pageOffset.coerceIn(0f, 1f) * 0.08f)
            val alpha = 1f - (pageOffset.coerceIn(0f, 1f) * 0.22f)

            ComboHeroCard(
                combo = combo,
                selected = selected,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
                onClick = { onComboSelected(combo.id) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            combos.forEachIndexed { index, combo ->
                val selected = index == pagerState.currentPage
                val width = if (selected) 22.dp else 8.dp
                val color by animateColorAsState(
                    targetValue = if (selected) OrangeAccent else Color(0xFFE7D3B1),
                    animationSpec = tween(260),
                    label = "comboIndicatorColor"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onComboSelected(combo.id) }
                )
            }
        }
    }
}

@Composable
private fun ComboHeroCard(
    combo: UserComboDto,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val imageUrls = remember(combo.id, combo.image, combo.images) {
        val images = if (combo.images.isNotEmpty()) combo.images else listOfNotNull(combo.image)
        images.mapNotNull(::resolveComboImageUrl)
    }
    var activeImageIndex by remember(combo.id, imageUrls.size) { mutableIntStateOf(0) }
    val cardColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFFFF0D6) else Color.White,
        animationSpec = tween(280),
        label = "comboCardColor"
    )
    val borderScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.96f,
        animationSpec = tween(280),
        label = "comboSelectedScale"
    )

    LaunchedEffect(combo.id, imageUrls.size) {
        activeImageIndex = 0
        if (imageUrls.size > 1) {
            while (true) {
                delay(3200)
                activeImageIndex = (activeImageIndex + 1) % imageUrls.size
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX *= borderScale
                scaleY *= borderScale
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 8.dp else 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFFFFFAF0))
            ) {
                Crossfade(
                    targetState = imageUrls.getOrNull(activeImageIndex),
                    animationSpec = tween(520),
                    label = "comboImageCrossfade"
                ) { imageUrl ->
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = combo.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        loading = { ImageFallbackBox() },
                        error = { ImageFallbackBox() }
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.94f)
                ) {
                    Text(
                        text = formatUserCurrency(combo.price_per_person),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = AmberPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (imageUrls.size > 1) {
                    ImageArrowButton(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 10.dp),
                        direction = ImageArrowDirection.Left,
                        onClick = {
                            activeImageIndex = if (activeImageIndex == 0) {
                                imageUrls.lastIndex
                            } else {
                                activeImageIndex - 1
                            }
                        }
                    )
                    ImageArrowButton(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp),
                        direction = ImageArrowDirection.Right,
                        onClick = {
                            activeImageIndex = (activeImageIndex + 1) % imageUrls.size
                        }
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        imageUrls.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == activeImageIndex) 7.dp else 5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == activeImageIndex) OrangeAccent else Color(0xFFE7D3B1)
                                    )
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(
                    text = combo.name,
                    color = InkBrown,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = combo.description ?: "Combo buffet nướng, lẩu và món ăn chọn lọc.",
                    color = MutedBrown,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) OrangeAccent else AmberPrimaryDark,
                        contentColor = Color.White
                    )
                ) {
                    if (selected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Chọn combo", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private enum class ImageArrowDirection {
    Left,
    Right
}

@Composable
private fun ImageArrowButton(
    modifier: Modifier = Modifier,
    direction: ImageArrowDirection,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.size(42.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.92f),
        shadowElevation = 4.dp
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = if (direction == ImageArrowDirection.Left) {
                    Icons.Default.KeyboardArrowLeft
                } else {
                    Icons.Default.KeyboardArrowRight
                },
                contentDescription = if (direction == ImageArrowDirection.Left) {
                    "Ảnh trước"
                } else {
                    "Ảnh sau"
                },
                tint = InkBrown
            )
        }
    }
}

@Composable
private fun GuestPaymentPanel(
    uiState: UserOrderUiState,
    onPaidGuestChange: (String) -> Unit,
    onFreeChildChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onCreateSession: () -> Unit
) {
    val paidGuests = uiState.paidGuestCount.toIntOrNull() ?: 1
    val freeChildren = uiState.freeChildCount.toIntOrNull() ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                Column {
                    Text(
                        text = "Số khách",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Tính tiền theo người lớn", color = MutedBrown, style = MaterialTheme.typography.bodySmall)
                }
                Surface(shape = CircleShape, color = Color(0xFFFFF0D6)) {
                    Text(
                        text = "${paidGuests + freeChildren} khách",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = AmberPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            GuestStepper(
                title = "Vé trả phí",
                subtitle = "Chiều cao trên 1m sẽ trả phí",
                value = paidGuests,
                minValue = 1,
                onChange = { onPaidGuestChange(it.toString()) }
            )
            GuestStepper(
                title = "Vé trẻ em",
                subtitle = "Em bé duới 1m sẽ miễn phí",
                value = freeChildren,
                minValue = 0,
                onChange = { onFreeChildChange(it.toString()) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Thanh toán",
                    style = MaterialTheme.typography.titleSmall,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = uiState.paymentMethod == "cash",
                        onClick = { onPaymentMethodChange("cash") },
                        label = { Text("Tiền mặt") },
                        leadingIcon = if (uiState.paymentMethod == "cash") {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE1D2))
                    )
                    FilterChip(
                        selected = uiState.paymentMethod == "qr",
                        onClick = { onPaymentMethodChange("qr") },
                        label = { Text("Mã QR") },
                        leadingIcon = if (uiState.paymentMethod == "qr") {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else {
                            null
                        },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE1D2))
                    )
                }
            }

            PaymentPreview(uiState = uiState)

            Button(
                onClick = onCreateSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !uiState.isLoading && uiState.selectedComboId != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (uiState.paymentMethod == "qr") "Tạo mã thanh toán QR" else "Xác nhận đã thu tiền mặt",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GuestStepper(
    title: String,
    subtitle: String,
    value: Int,
    minValue: Int,
    onChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFAF0))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = InkBrown, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MutedBrown, style = MaterialTheme.typography.bodySmall)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { onChange((value - 1).coerceAtLeast(minValue)) },
                enabled = value > minValue,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp),
                shape = CircleShape
            ) {
                Text("-", fontWeight = FontWeight.Bold)
            }
            Text(
                text = value.toString(),
                modifier = Modifier.width(28.dp),
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = { onChange(value + 1) },
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimaryDark)
            ) {
                Text("+", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PaymentPreview(uiState: UserOrderUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (uiState.paymentMethod == "qr") Color(0xFFFFF0D6) else Color(0xFFFFFAF0)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tạm tính", color = MutedBrown)
                Text(
                    text = formatUserCurrency(uiState.totalPreview.toString()),
                    color = AmberPrimaryDark,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (uiState.paymentMethod == "cash") {
                Text(
                    text = "Thu tiền mặt tại quầy trước khi mở phiên buffet 100 phút.",
                    color = MutedBrown,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, OrangeAccent.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        QrGlyph(color = InkBrown, modifier = Modifier.fillMaxSize())
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Thanh toán VietQR",
                            color = InkBrown,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Nội dung: ${uiState.table?.table_code ?: uiState.tableCode}-BUFFET",
                            color = MutedBrown,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Mã QR thật sẽ hiện ở bước kế tiếp.",
                            color = AmberPrimaryDark,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Decorative QR-style glyph (three finder patterns plus a deterministic module fill). Stands in for
 * a real QR in previews — not scannable, purely to read as "this is a QR payment".
 */
@Composable
private fun QrGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val n = 21
        val cell = size.minDimension / n

        fun module(r: Int, c: Int) {
            drawRect(color = color, topLeft = Offset(c * cell, r * cell), size = Size(cell, cell))
        }

        fun finder(r0: Int, c0: Int) {
            for (r in 0..6) for (c in 0..6) {
                val edge = r == 0 || r == 6 || c == 0 || c == 6
                val center = r in 2..4 && c in 2..4
                if (edge || center) module(r0 + r, c0 + c)
            }
        }

        finder(0, 0)
        finder(0, 14)
        finder(14, 0)

        for (r in 0 until n) for (c in 0 until n) {
            val inFinder = (r < 8 && c < 8) || (r < 8 && c > 12) || (r > 12 && c < 8)
            if (!inFinder && (r * c + r + c) % 3 == 0) module(r, c)
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE8))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(message, color = MaterialTheme.colorScheme.error)
            if (canRetry) {
                Button(onClick = onRetry) {
                    Text("Thử lại")
                }
            }
        }
    }
}

@Composable
private fun EmptyComboCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Chưa có combo đang hoạt động", color = InkBrown, fontWeight = FontWeight.Bold)
            Text("Vui lòng quay lại sau hoặc liên hệ nhân viên.", color = MutedBrown)
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
        Text("Combo", color = MutedBrown, fontWeight = FontWeight.SemiBold)
    }
}

private fun resolveComboImageUrl(image: String?): String? {
    val value = image?.trim().orEmpty()
    if (value.isEmpty()) {
        return null
    }
    if (value.startsWith("http://") || value.startsWith("https://")) {
        return value
    }
    return RetrofitClient.BASE_URL + value.trimStart('/')
}

fun formatUserCurrency(value: String): String {
    val amount = value.toDoubleOrNull() ?: 0.0
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
}
