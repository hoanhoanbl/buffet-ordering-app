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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.example.appgoimon.viewmodel.UserOrderUiState

@Composable
fun MenuScreen(
    uiState: UserOrderUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onRetryMenu: () -> Unit,
    onAddToCart: (MenuItemDto) -> Unit,
    onUpdateQuantity: (MenuItemDto, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                )
            )
            .padding(16.dp)
    ) {
        // Header Row with Logo + Search + Search Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            SubcomposeAsyncImage(
                model = "${RetrofitClient.BASE_URL}uploads/foods/logo_tron.jpg",
                contentDescription = "Logo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                },
                error = {
                    Box(modifier = Modifier.fillMaxSize())
                }
            )

            // Search bar
            TextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tìm món ăn...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Search button
            IconButton(
                onClick = { /* Focus search field or leave empty */ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        uiState.session?.let { session ->
            val remainingText = if (uiState.isSessionExpired) {
                "Da het thoi gian dung bua"
            } else {
                "Con lai ${uiState.remainingMinutes} phut"
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        uiState.isSessionExpired -> Color(0xFFFFECE8)
                        uiState.remainingMinutes in 1..10 -> Color(0xFFFFF3D6)
                        else -> Color.White
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${session.table_name ?: "Ban"} - ${session.combo_name ?: "Buffet"}",
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = remainingText,
                        color = if (uiState.isSessionExpired) MaterialTheme.colorScheme.error else OrangeAccent
                    )
                    if (!uiState.isSessionExpired && uiState.remainingMinutes in 1..10) {
                        Text("Sap het gio goi mon", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Category chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
        ) {
            // "Tất cả" chip
            item {
                FilterChip(
                    selected = uiState.selectedCategoryName == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("Tất cả") }
                )
            }

            // Category chips
            items(uiState.categories) { category ->
                FilterChip(
                    selected = uiState.selectedCategoryName == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading state
        if (uiState.isMenuLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AmberPrimaryDark)
            }
            return
        }

        // Error state
        if (uiState.errorMessage.isNotEmpty() && !uiState.isLoading && !uiState.isSubmittingOrder) {
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
                    androidx.compose.material3.Button(onClick = onRetryMenu) {
                        Text("Thu lai")
                    }
                }
            }
            return
        }

        // Empty category state
        if (uiState.filteredMenuItems.isEmpty() && uiState.selectedCategoryName != null && uiState.searchQuery.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chưa có món trong danh mục này",
                    color = MutedBrown
                )
            }
            return
        }

        // Empty search results
        if (uiState.filteredMenuItems.isEmpty() && uiState.searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không tìm thấy món ăn phù hợp",
                    color = MutedBrown
                )
            }
            return
        }

        // Menu grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.filteredMenuItems) { item ->
                MenuItemCard(
                    item = item,
                    cartItem = uiState.cartItems.firstOrNull { it.menuItem.id == item.id },
                    onAddToCart = { onAddToCart(item) },
                    onUpdateQuantity = { qty -> onUpdateQuantity(item, qty) }
                )
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItemDto,
    cartItem: com.example.appgoimon.viewmodel.CartItem?,
    onAddToCart: () -> Unit,
    onUpdateQuantity: (Int) -> Unit
) {
    val imageUrl = resolveFoodImageUrl(item.image)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Image
            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(20.dp),
                                color = AmberPrimaryDark
                            )
                        }
                    },
                    error = {
                        ImageFallbackText("Không tải được ảnh")
                    }
                )
            } else {
                ImageFallbackText("Chưa có ảnh món")
            }

            // Name
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Category
            Text(
                text = item.category_name ?: "Không có danh mục",
                style = MaterialTheme.typography.bodySmall,
                color = OrangeAccent
            )

            // Action buttons
            if (cartItem == null) {
                // Show add button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    SmallFloatingActionButton(
                        onClick = onAddToCart,
                        containerColor = OrangeAccent,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, "Thêm vào giỏ", tint = Color.White)
                    }
                }
            } else {
                // Show quantity controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onUpdateQuantity(cartItem.quantity - 1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("-", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = cartItem.quantity.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { onUpdateQuantity(cartItem.quantity + 1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("+", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
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
            .height(100.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFFFF3D8)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = MutedBrown, style = MaterialTheme.typography.bodySmall)
    }
}
