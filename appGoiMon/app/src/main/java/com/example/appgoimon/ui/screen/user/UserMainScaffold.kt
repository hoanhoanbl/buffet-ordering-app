package com.example.appgoimon.ui.screen.user

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.UserOrderUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScaffold(
    user: AuthUserDto,
    uiState: UserOrderUiState,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onRetryMenu: () -> Unit,
    onAddToCart: (MenuItemDto) -> Unit,
    onUpdateQuantity: (MenuItemDto, Int) -> Unit,
    onUpdateNote: (MenuItemDto, String) -> Unit,
    onRemoveItem: (MenuItemDto) -> Unit,
    onSubmitOrder: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onLoadOrderHistory: () -> Unit,
    onRefreshOrderHistory: () -> Unit,
    onOrderSuccessConsumed: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show a one-shot success Snackbar after an order is sent, then consume the signal so it does
    // not re-fire on recomposition.
    LaunchedEffect(uiState.orderSuccessCount) {
        val count = uiState.orderSuccessCount
        if (count != null) {
            snackbarHostState.showSnackbar("Đã gửi $count món cho bếp")
            onOrderSuccessConsumed()
        }
    }

    Scaffold(
        topBar = {
            UserTopBar(uiState = uiState, onLogout = onLogout)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Menu") },
                    label = { Text("Menu") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )

                NavigationBarItem(
                    icon = {
                        if (uiState.cartItemCount > 0) {
                            BadgedBox(
                                badge = { Badge { Text(uiState.cartItemCount.toString()) } }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng")
                            }
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng")
                        }
                    },
                    label = { Text("Giỏ") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Đã gọi") },
                    label = { Text("Đã gọi") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> MenuScreen(
                    uiState = uiState,
                    onSearchQueryChange = onSearchQueryChange,
                    onCategorySelected = onCategorySelected,
                    onRetryMenu = onRetryMenu,
                    onAddToCart = onAddToCart,
                    onUpdateQuantity = onUpdateQuantity
                )

                1 -> CartScreen(
                    cartItems = uiState.cartItems,
                    isSubmitting = uiState.isSubmittingOrder,
                    isExpired = uiState.isSessionExpired,
                    onUpdateQuantity = onUpdateQuantity,
                    onUpdateNote = onUpdateNote,
                    onRemoveItem = onRemoveItem,
                    onSubmit = onSubmitOrder
                )

                2 -> OrderHistoryScreen(
                    uiState = uiState,
                    onLoadHistory = onLoadOrderHistory,
                    onRefresh = onRefreshOrderHistory,
                    onRetry = onLoadOrderHistory
                )
            }
        }
    }
}

/**
 * Persistent top bar shown across all user tabs. Always renders so the LOGOUT action is reachable
 * even while inside an active session. When a session is running it also shows the live countdown,
 * shifting from normal to a warning palette at <=10 minutes and to an error palette when expired.
 */
@Composable
private fun UserTopBar(uiState: UserOrderUiState, onLogout: () -> Unit) {
    val hasSession = uiState.hasCountdownSession
    val expired = uiState.isSessionExpired
    val minutes = uiState.remainingMinutes
    val isWarning = hasSession && !expired && minutes in 1..10

    val containerColor = when {
        hasSession && expired -> Color(0xFFFFECE8)
        isWarning -> Color(0xFFFFF3D6)
        else -> Color(0xFFFFF7E8)
    }
    val contentColor = if (expired || isWarning) {
        MaterialTheme.colorScheme.error
    } else {
        OrangeAccent
    }
    // Compact, glanceable time: "1g 34p" for >= 1h, "34 phút" otherwise.
    val timeText = when {
        expired -> "Hết giờ"
        minutes <= 1 -> "dưới 1 phút"
        minutes >= 60 -> "${minutes / 60}g ${minutes % 60}p"
        else -> "$minutes phút"
    }

    // Surface gives a soft shadow so the bar reads as a real app bar above the scrolling content.
    Surface(color = containerColor, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // enableEdgeToEdge() lets content draw under the system status bar; without this the
                // logout button and countdown sit behind it and the OS swallows their taps.
                .statusBarsPadding()
                .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Table identity chip: a tinted pin + name + a small status subtitle.
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = if (hasSession) (uiState.session?.table_name ?: "Phiên buffet") else "Buffet",
                        color = InkBrown,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(
                        text = when {
                            !hasSession -> "Nhà hàng buffet"
                            expired -> "Phiên đã kết thúc"
                            else -> "Phiên đang mở"
                        },
                        color = MutedBrown,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }

            if (hasSession) {
                CountdownChip(timeText = timeText, contentColor = contentColor, expired = expired)
            }

            Surface(shape = CircleShape, color = Color.White) {
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Đăng xuất",
                        tint = OrangeAccent
                    )
                }
            }
        }
    }
}

/**
 * Pill-shaped buffet countdown. A clock glyph plus the remaining time, on a white pill outlined in
 * the active state color (normal / warning / expired) so it reads at a glance without text noise.
 */
@Composable
private fun CountdownChip(timeText: String, contentColor: Color, expired: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .border(1.dp, contentColor.copy(alpha = 0.35f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ClockGlyph(color = contentColor, modifier = Modifier.size(16.dp))
        Text(
            text = timeText,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
    }
}

/** Minimal dependency-free clock glyph (circle + two hands) drawn in the given color. */
@Composable
private fun ClockGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.1f
        val radius = size.minDimension / 2f - stroke
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(color = color, radius = radius, center = center, style = Stroke(width = stroke))
        // Minute hand (points up).
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, center.y - radius * 0.6f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Hour hand (points right).
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x + radius * 0.45f, center.y),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}
