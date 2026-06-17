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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.appgoimon.viewmodel.AdminCategoryViewModel

private val GreenFg = Color(0xFF15803D)
private val GreenBg = Color(0xFFE7F6EC)
private val NeutralFg = Color(0xFF6D5A45)
private val NeutralBg = Color(0xFFF1ECE3)
private val RedFg = Color(0xFFC23B22)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoryScreen(
    viewModel: AdminCategoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CategorySummaryCard(
                total = uiState.categories.size,
                onAdd = {
                    viewModel.startCreate()
                    showSheet = true
                }
            )
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
                ErrorBlock(
                    message = uiState.errorMessage,
                    onRetry = viewModel::loadCategories
                )
            }
        }

        if (uiState.successMessage.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = GreenBg
                ) {
                    Text(
                        text = uiState.successMessage,
                        modifier = Modifier.padding(12.dp),
                        color = GreenFg,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (uiState.isLoading && uiState.categories.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AmberPrimaryDark)
                }
            }
        }

        if (!uiState.isLoading && uiState.categories.isEmpty()) {
            item { EmptyCategoryState() }
        }

        items(uiState.categories) { category ->
            CategoryCard(
                category = category,
                dishes = uiState.menuItems.filter { it.category_id == category.id },
                onEdit = {
                    viewModel.startEdit(category)
                    showSheet = true
                },
                onToggleStatus = {
                    viewModel.setCategoryStatus(
                        category.id,
                        if (category.status == "active") "inactive" else "active"
                    )
                },
                onDelete = { viewModel.deleteCategory(category.id) }
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            CategoryForm(
                title = if (uiState.editingCategoryId == null) "Thêm danh mục mới" else "Sửa danh mục",
                name = uiState.name,
                status = uiState.status,
                isLoading = uiState.isLoading,
                onNameChange = viewModel::onNameChange,
                onStatusChange = viewModel::onStatusChange,
                onSave = {
                    viewModel.saveCategory()
                    showSheet = false
                },
                onCancel = { showSheet = false }
            )
        }
    }
}

@Composable
private fun CategorySummaryCard(total: Int, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE1D2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.List, contentDescription = null, tint = OrangeAccent)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$total danh mục",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nhóm món để khách dễ tìm trên thực đơn",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedBrown
                    )
                }
            }

            Button(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeAccent,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm danh mục mới", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CategoryForm(
    title: String,
    name: String,
    status: String,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = InkBrown,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tên danh mục") },
            placeholder = { Text("VD: Món lẩu, Đồ uống...") },
            singleLine = true,
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangeAccent,
                focusedLabelColor = OrangeAccent,
                cursorColor = OrangeAccent
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Trạng thái",
                style = MaterialTheme.typography.labelLarge,
                color = MutedBrown,
                fontWeight = FontWeight.SemiBold
            )
            StatusSelector(
                options = listOf("active", "inactive"),
                selected = status,
                onSelected = onStatusChange
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onCancel,
                enabled = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hủy")
            }
            Button(
                onClick = onSave,
                enabled = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Lưu danh mục", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryDto,
    dishes: List<MenuItemDto>,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    val isActive = category.status == "active"
    val accentFg = if (isActive) GreenFg else NeutralFg
    val accentBg = if (isActive) GreenBg else NeutralBg

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with the category's initial — gives each row a quick visual anchor.
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.category_name.trim().take(1).uppercase(),
                        color = accentFg,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = category.category_name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                StatusBadge(category.status)
            }

            if (dishes.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    dishes.take(5).forEach { dish ->
                        SubcomposeAsyncImage(
                            model = resolveFoodImageUrl(dish.image),
                            contentDescription = dish.name,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop,
                            loading = { CategoryThumbFallback() },
                            error = { CategoryThumbFallback() }
                        )
                    }
                    if (dishes.size > 5) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF0D6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${dishes.size - 5}",
                                style = MaterialTheme.typography.labelLarge,
                                color = AmberPrimaryDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF0D6)) {
                Text(
                    text = if (dishes.isEmpty()) "Chưa có món" else "${dishes.size} món",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = AmberPrimaryDark,
                    fontWeight = FontWeight.SemiBold
                )
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
                    onClick = onToggleStatus,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberPrimaryDark)
                ) {
                    Text(if (isActive) "Tắt" else "Bật")
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedFg)
                ) {
                    Text("Xóa")
                }
            }
        }
    }
}

@Composable
private fun CategoryThumbFallback() {
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
    if (value.isEmpty()) return null
    if (value.startsWith("http://") || value.startsWith("https://")) return value
    if (!value.contains('/')) return RetrofitClient.BASE_URL + "uploads/foods/$value"
    return RetrofitClient.BASE_URL + value.trimStart('/')
}

@Composable
private fun EmptyCategoryState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF0D6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "Chưa có danh mục",
                style = MaterialTheme.typography.titleMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tạo danh mục để nhóm các món trên thực đơn.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusSelector(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = { Text(statusLabel(option)) },
                leadingIcon = if (selected == option) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFE1D2),
                    selectedLabelColor = OrangeAccent,
                    selectedLeadingIconColor = OrangeAccent
                )
            )
        }
    }
}
