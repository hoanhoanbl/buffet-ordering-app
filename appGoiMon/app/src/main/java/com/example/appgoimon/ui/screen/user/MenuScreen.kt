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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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

            // Search bar (search icon is built in; live-filters as you type)
            TextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tìm món ăn...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm", tint = OrangeAccent) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Xóa tìm kiếm",
                                tint = MutedBrown
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    // Drop the filled-field underline for a clean rounded pill.
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // The live remaining-time countdown now lives in the persistent top bar of UserMainScaffold.
        // Here we only surface a clear "session ended" CTA when expired.
        if (uiState.isSessionExpired) {
            SessionExpiredCard()
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
                    label = { Text("Tất cả") },
                    colors = brandChipColors()
                )
            }

            // Category chips
            items(uiState.categories) { category ->
                FilterChip(
                    selected = uiState.selectedCategoryName == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    colors = brandChipColors()
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
                        Text("Thử lại")
                    }
                }
            }
            return
        }

        // Empty category state
        if (uiState.filteredMenuItems.isEmpty() && uiState.selectedCategoryName != null && uiState.searchQuery.isEmpty()) {
            MenuEmptyState(
                title = "Chưa có món trong danh mục này",
                subtitle = "Hãy thử chọn danh mục khác ở thanh phía trên."
            )
            return
        }

        // Empty search results
        if (uiState.filteredMenuItems.isEmpty() && uiState.searchQuery.isNotEmpty()) {
            MenuEmptyState(
                title = "Không tìm thấy món ăn phù hợp",
                subtitle = "Thử từ khóa khác hoặc xóa ô tìm kiếm để xem tất cả món."
            )
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Image
            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp)),
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

            // Name — one line keeps every card the same height so the grid reads as a clean system.
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Bottom row: category tag on the left, add / quantity control pinned to the right.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryTag(
                    text = item.category_name ?: "Chưa phân loại",
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (cartItem == null) {
                    SmallFloatingActionButton(
                        onClick = onAddToCart,
                        containerColor = OrangeAccent,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, "Thêm vào giỏ", tint = Color.White)
                    }
                } else {
                    MenuQuantityStepper(
                        quantity = cartItem.quantity,
                        onDecrease = { onUpdateQuantity(cartItem.quantity - 1) },
                        onIncrease = { onUpdateQuantity(cartItem.quantity + 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTag(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
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
private fun MenuQuantityStepper(
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
            Text("-", fontWeight = FontWeight.Bold, color = InkBrown, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = InkBrown,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(22.dp)
        )
        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(32.dp)
        ) {
            Text("+", fontWeight = FontWeight.Bold, color = OrangeAccent, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun brandChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Color(0xFFFFE1D2),
    selectedLabelColor = OrangeAccent
)

@Composable
private fun MenuEmptyState(title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = InkBrown,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedBrown,
                textAlign = TextAlign.Center
            )
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
