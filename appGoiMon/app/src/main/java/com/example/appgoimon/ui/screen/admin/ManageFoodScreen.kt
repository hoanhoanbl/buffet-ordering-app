package com.example.appgoimon.ui.screen.admin

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.appgoimon.data.remote.CategoryDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminFoodViewModel

@Composable
fun ManageFoodScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Menu") },
                    leadingIcon = if (selectedTab == 0) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else {
                        null
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE1D2))
                )
            }
            item {
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Danh mục") },
                    leadingIcon = if (selectedTab == 1) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else {
                        null
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE1D2))
                )
            }
        }

        if (selectedTab == 0) {
            MenuItemsScreen()
        } else {
            ManageCategoryScreen()
        }
    }
}

@Composable
private fun MenuItemsScreen(
    viewModel: AdminFoodViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMenuData()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MenuSummaryCard(total = uiState.menuItems.size)
        }

        item {
            MenuItemForm(
                title = if (uiState.editingFoodId == null) "Thêm món" else "Sửa món",
                categories = uiState.categories,
                selectedCategoryId = uiState.categoryId,
                name = uiState.name,
                image = uiState.image,
                description = uiState.description,
                status = uiState.status,
                isLoading = uiState.isLoading,
                onCategoryChange = viewModel::onCategoryChange,
                onNameChange = viewModel::onNameChange,
                onImageChange = viewModel::onImageChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onStatusChange = viewModel::onStatusChange,
                onSave = viewModel::saveMenuItem,
                onNew = viewModel::startCreate
            )
        }

        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AmberPrimaryDark)
                }
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
                ErrorBlock(
                    message = uiState.errorMessage,
                    onRetry = viewModel::loadMenuData
                )
            }
        }

        if (uiState.successMessage.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE7F6EC)
                ) {
                    Text(
                        text = uiState.successMessage,
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFF166534),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        items(uiState.menuItems) { item ->
            MenuItemCard(
                item = item,
                onEdit = { viewModel.startEdit(item) },
                onStatus = { status -> viewModel.setMenuItemStatus(item.id, status) },
                onDelete = { viewModel.deleteMenuItem(item.id) }
            )
        }
    }
}

@Composable
private fun MenuSummaryCard(total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFFFE1D2), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = OrangeAccent)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$total món trong menu",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Quản lý món, ảnh, danh mục và trạng thái bán",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedBrown
                )
            }
        }
    }
}

@Composable
private fun MenuItemForm(
    title: String,
    categories: List<CategoryDto>,
    selectedCategoryId: Int?,
    name: String,
    image: String,
    description: String,
    status: String,
    isLoading: Boolean,
    onCategoryChange: (Int) -> Unit,
    onNameChange: (String) -> Unit,
    onImageChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onSave: () -> Unit,
    onNew: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status)
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories.filter { it.status == "active" }) { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { onCategoryChange(category.id) },
                        label = {
                            Text(
                                text = category.category_name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE1D2))
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tên món") },
                singleLine = true,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = image,
                onValueChange = onImageChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ảnh món") },
                singleLine = true,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mô tả") },
                enabled = !isLoading,
                minLines = 2
            )

            StatusSelector(
                options = listOf("available", "out_of_stock", "hidden"),
                selected = status,
                onSelected = onStatusChange
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSave,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimaryDark)
                ) {
                    Text("Lưu")
                }
                OutlinedButton(
                    onClick = onNew,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Mới")
                }
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItemDto,
    onEdit: () -> Unit,
    onStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                SubcomposeAsyncImage(
                    model = resolveFoodImageUrl(item.image),
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    loading = { ImageFallbackBox() },
                    error = { ImageFallbackBox() }
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = item.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleSmall,
                            color = InkBrown,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(item.status)
                    }
                    Text(
                        text = item.category_name ?: "Không có danh mục",
                        style = MaterialTheme.typography.bodySmall,
                        color = OrangeAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!item.description.isNullOrBlank()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedBrown,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                ) {
                    Text("Sửa")
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Xóa")
                }
            }

            StatusSelector(
                options = listOf("available", "out_of_stock", "hidden"),
                selected = item.status,
                onSelected = onStatus
            )
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
        Text("Ảnh", style = MaterialTheme.typography.labelSmall, color = MutedBrown)
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
