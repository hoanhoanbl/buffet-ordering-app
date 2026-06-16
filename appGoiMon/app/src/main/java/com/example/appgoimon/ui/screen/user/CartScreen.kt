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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    isSubmitting: Boolean,
    isExpired: Boolean,
    onUpdateQuantity: (MenuItemDto, Int) -> Unit,
    onUpdateNote: (MenuItemDto, String) -> Unit,
    onSubmit: () -> Unit
) {
    val totalQuantity = cartItems.sumOf { it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gio mon", fontWeight = FontWeight.Bold, color = InkBrown)
                        Text(
                            if (totalQuantity > 0) "$totalQuantity mon dang chon" else "Chon mon tu thuc don",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedBrown
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF7E8), Color(0xFFFFE7BB))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartState()
            } else {
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
                            onUpdateNote = { note -> onUpdateNote(cartItem.menuItem, note) }
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
}

@Composable
private fun EmptyCartState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Gio mon dang trong",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkBrown
                )
                Text(
                    "Hay chon mon tu tab Menu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedBrown
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
                        "San sang goi mon",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = InkBrown
                    )
                    Text(
                        "$totalQuantity mon se duoc gui cho bep",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown
                    )
                }
                QuantityBadge("x$totalQuantity")
            }

            if (isExpired) {
                Text(
                    "Da het thoi gian dung bua, khong the goi them mon",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
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
                    if (isSubmitting) "Dang goi mon..." else "Goi mon ngay",
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
    onUpdateNote: (String) -> Unit
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
                verticalAlignment = Alignment.CenterVertically
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
                            Text("Anh", style = MaterialTheme.typography.labelSmall, color = MutedBrown)
                        }
                    }
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = cartItem.menuItem.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = InkBrown,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    cartItem.menuItem.category_name?.let { categoryName ->
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.bodySmall,
                            color = OrangeAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                QuantityStepper(
                    quantity = cartItem.quantity,
                    onDecrease = { onUpdateQuantity(cartItem.quantity - 1) },
                    onIncrease = { onUpdateQuantity(cartItem.quantity + 1) }
                )
            }

            if (isNoteExpanded || cartItem.note.isNotEmpty()) {
                OutlinedTextField(
                    value = cartItem.note,
                    onValueChange = onUpdateNote,
                    label = { Text("Ghi chu rieng") },
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
                TextButton(
                    onClick = { isNoteExpanded = true },
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp)
                ) {
                    Text(
                        "Them ghi chu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown
                    )
                }
            }
        }
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
