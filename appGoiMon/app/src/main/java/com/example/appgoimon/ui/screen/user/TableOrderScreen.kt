package com.example.appgoimon.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.CartItem
import com.example.appgoimon.viewmodel.UserOrderUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableOrderScreen(
    user: AuthUserDto,
    uiState: UserOrderUiState,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onRetryMenu: () -> Unit,
    onAddToCart: (MenuItemDto) -> Unit,
    onRemoveFromCart: (MenuItemDto) -> Unit,
    onUpdateQuantity: (MenuItemDto, Int) -> Unit,
    onUpdateNote: (MenuItemDto, String) -> Unit,
    onSubmitOrder: () -> Unit,
    onClearMessages: () -> Unit = {}
) {
    var showCartSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            if (uiState.cartItemCount > 0) {
                FloatingActionButton(
                    onClick = { showCartSheet = true },
                    containerColor = OrangeAccent
                ) {
                    BadgedBox(
                        badge = {
                            Badge { Text(uiState.cartItemCount.toString()) }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, "Giỏ hàng")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                    )
                ),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = uiState.session?.table_name
                            ?: uiState.table?.table_name
                            ?: "Ban ${uiState.tableCode}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.session?.combo_name ?: uiState.selectedCombo?.name ?: user.username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedBrown
                    )
                }

                TextButton(onClick = onLogout) {
                    Text("Dang xuat", color = OrangeAccent)
                }
            }
        }

        item {
            TextButton(onClick = onBack) {
                Text("Chon ban khac")
            }
        }

        if (uiState.isMenuLoading) {
            item {
                CircularProgressIndicator(color = AmberPrimaryDark)
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE8))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRetryMenu) {
                            Text("Thu lai")
                        }
                    }
                }
            }
        }

        if (!uiState.isMenuLoading && uiState.menuItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Combo nay chua co mon kha dung",
                        modifier = Modifier.padding(16.dp),
                        color = MutedBrown
                    )
                }
            }
        }

        items(uiState.menuItems) { item ->
            MenuFoodCard(
                item = item,
                onAddToCart = { onAddToCart(item) }
            )
        }
    }

    // Cart Bottom Sheet
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            sheetState = sheetState
        ) {
            CartBottomSheet(
                cartItems = uiState.cartItems,
                isSubmitting = uiState.isSubmittingOrder,
                onUpdateQuantity = onUpdateQuantity,
                onUpdateNote = onUpdateNote,
                onSubmit = {
                    onSubmitOrder()
                }
            )
        }
    }

    // Auto-close bottom sheet on success
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage.isNotEmpty() && showCartSheet) {
            scope.launch {
                sheetState.hide()
                showCartSheet = false
            }
        }
    }

    // Success Dialog
    if (uiState.successMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = onClearMessages,
            title = { Text("Thành công", fontWeight = FontWeight.Bold) },
            text = { Text(uiState.successMessage) },
            confirmButton = {
                Button(onClick = onClearMessages) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog (only for order submission errors, not menu loading)
    if (uiState.errorMessage.isNotEmpty() && !uiState.isLoading && !uiState.isMenuLoading && !uiState.isSubmittingOrder) {
        AlertDialog(
            onDismissRequest = { /* Keep showing until dismissed */ },
            title = { Text("Lỗi", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = { Text(uiState.errorMessage) },
            confirmButton = {
                Button(onClick = onRetryMenu) {
                    Text("Thử lại")
                }
            },
            dismissButton = {
                TextButton(onClick = onClearMessages) {
                    Text("Đóng")
                }
            }
        )
    }
    }
}

@Composable
private fun MenuFoodCard(
    item: MenuItemDto,
    onAddToCart: () -> Unit
) {
    val imageUrl = resolveFoodImageUrl(item.image)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AmberPrimaryDark)
                        }
                    },
                    error = {
                        ImageFallbackText("Khong tai duoc anh")
                    }
                )
            } else {
                ImageFallbackText("Chua co anh mon")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(item.category_name ?: "Khong co danh muc", color = OrangeAccent)
                }

                SmallFloatingActionButton(
                    onClick = onAddToCart,
                    containerColor = OrangeAccent,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Thêm vào giỏ", tint = Color.White)
                }
            }

            if (!item.description.isNullOrBlank()) {
                Text(item.description, color = MutedBrown)
            }
            Text("Trang thai: ${item.status}", color = MutedBrown)
        }
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

@Composable
private fun ImageFallbackText(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFF3D8)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = MutedBrown)
    }
}

@Composable
private fun CartBottomSheet(
    cartItems: List<CartItem>,
    isSubmitting: Boolean,
    onUpdateQuantity: (MenuItemDto, Int) -> Unit,
    onUpdateNote: (MenuItemDto, String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Text(
            text = "Giỏ hàng (${cartItems.sumOf { it.quantity }} món)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = InkBrown
        )

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Giỏ hàng trống", color = MutedBrown)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onUpdateQuantity = { newQty -> onUpdateQuantity(cartItem.menuItem, newQty) },
                        onUpdateNote = { note -> onUpdateNote(cartItem.menuItem, note) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSubmit,
                enabled = !isSubmitting && cartItems.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isSubmitting) "Đang gọi món..." else "Gọi món - ${cartItems.size} món")
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onUpdateNote: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF0))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cartItem.menuItem.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = InkBrown,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { onUpdateQuantity(cartItem.quantity - 1) }) {
                        Text("-", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = cartItem.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onUpdateQuantity(cartItem.quantity + 1) }) {
                        Text("+", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            TextField(
                value = cartItem.note,
                onValueChange = onUpdateNote,
                label = { Text("Ghi chú (tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
