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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.CartItem

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    isSubmitting: Boolean,
    isExpired: Boolean,
    onUpdateQuantity: (MenuItemDto, Int) -> Unit,
    onUpdateNote: (MenuItemDto, String) -> Unit,
    onRemoveItem: (MenuItemDto) -> Unit,
    onSubmit: () -> Unit
) {
    val totalQuantity = cartItems.sumOf { it.quantity }

    // No app bar here: the persistent UserMainScaffold top bar is the single header. This screen
    // only contributes an in-content section title so the two no longer stack into a doubled header.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF7E8), Color(0xFFFFE7BB))
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionHeader(
            title = "Giỏ món",
            subtitle = if (totalQuantity > 0) "$totalQuantity món đang chọn" else "Chọn món từ thực đơn"
        )

        if (isExpired) {
            SessionExpiredCard()
        }

        if (cartItems.isEmpty()) {
            EmptyCartState()
        } else {
            LeftoverFeeNotice()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 2.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onUpdateQuantity = { newQty -> onUpdateQuantity(cartItem.menuItem, newQty) },
                        onUpdateNote = { note -> onUpdateNote(cartItem.menuItem, note) },
                        onRemove = { onRemoveItem(cartItem.menuItem) }
                    )
                }
            }

            CartSubmitPanel(
                totalQuantity = totalQuantity,
                isSubmitting = isSubmitting,
                isExpired = isExpired,
                onSubmit = onSubmit
            )
        }
    }
}

/**
 * Buffet leftover-food surcharge notice. Nudges guests to order sensible amounts so the kitchen
 * isn't wasted (and they avoid the 100k / 100g fee).
 */
@Composable
private fun LeftoverFeeNotice() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF3D6))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = Color(0xFFB7791F),
            modifier = Modifier.size(20.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Gọi lượng vừa đủ nhé!",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = InkBrown
            )
            Text(
                text = "Phụ thu 100.000đ / 100g đồ ăn thừa để tránh lãng phí.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedBrown
            )
        }
    }
}

/** Lightweight in-content page title (not an app bar) so it doesn't stack with the scaffold header. */
@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)) {
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
private fun EmptyCartState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Soft concentric badge so the empty state reads as intentional, not unfinished.
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
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Text(
                "Giỏ món đang trống",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = InkBrown,
                textAlign = TextAlign.Center
            )
            Text(
                "Hãy chọn món yêu thích từ thực đơn để bắt đầu gọi cho bàn của bạn.",
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
                    text = "Mở tab Menu để thêm món",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = OrangeAccent
                )
            }
        }
    }
}

@Composable
private fun CartSubmitPanel(
    totalQuantity: Int,
    isSubmitting: Boolean,
    isExpired: Boolean,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Sẵn sàng gọi món",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = InkBrown
                    )
                    Text(
                        "$totalQuantity món sẽ được gửi cho bếp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown
                    )
                }
                QuantityBadge("x$totalQuantity")
            }

            Button(
                onClick = onSubmit,
                enabled = !isSubmitting && totalQuantity > 0 && !isExpired,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isSubmitting) "Đang gọi món..." else "Gọi món ngay",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onUpdateNote: (String) -> Unit,
    onRemove: () -> Unit
) {
    var isNoteExpanded by remember { mutableStateOf(cartItem.note.isNotEmpty()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                val imageUrl = resolveFoodImageUrl(cartItem.menuItem.image)
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = cartItem.menuItem.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        ImageFallbackBox {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = AmberPrimaryDark,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        ImageFallbackBox {
                            Text("Ảnh", style = MaterialTheme.typography.labelSmall, color = MutedBrown)
                        }
                    }
                )

                // Everything about the item lives in this column: name + delete on top, the category
                // tag, then the quantity stepper pinned right — instead of trash stacked on the stepper.
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = cartItem.menuItem.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = InkBrown,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa món",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    cartItem.menuItem.category_name?.takeIf { it.isNotBlank() }?.let { categoryName ->
                        CategoryTag(categoryName)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        QuantityStepper(
                            quantity = cartItem.quantity,
                            onDecrease = { onUpdateQuantity(cartItem.quantity - 1) },
                            onIncrease = { onUpdateQuantity(cartItem.quantity + 1) }
                        )
                    }
                }
            }

            if (isNoteExpanded || cartItem.note.isNotEmpty()) {
                OutlinedTextField(
                    value = cartItem.note,
                    onValueChange = onUpdateNote,
                    label = { Text("Ghi chú riêng") },
                    placeholder = { Text("VD: ít cay, không hành...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangeAccent,
                        focusedLabelColor = OrangeAccent
                    )
                )
            } else {
                Surface(
                    onClick = { isNoteExpanded = true },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF7E8)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = OrangeAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Thêm ghi chú",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedBrown,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFF0D6))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = OrangeAccent,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFFFFF0D6))
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        IconButton(
            onClick = onDecrease,
            modifier = Modifier.size(32.dp)
        ) {
            Text("-", fontWeight = FontWeight.Bold, color = InkBrown)
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = InkBrown,
            modifier = Modifier.width(22.dp)
        )
        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(32.dp)
        ) {
            Text("+", fontWeight = FontWeight.Bold, color = InkBrown)
        }
    }
}

@Composable
private fun QuantityBadge(text: String) {
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
