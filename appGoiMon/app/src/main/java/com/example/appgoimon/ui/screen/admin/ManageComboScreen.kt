package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.appgoimon.data.remote.AdminComboDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminComboViewModel

private val GreenFg = Color(0xFF15803D)
private val GreenBg = Color(0xFFE7F6EC)
private val NeutralFg = Color(0xFF6D5A45)
private val NeutralBg = Color(0xFFF1ECE3)
private val RedFg = Color(0xFFC23B22)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageComboScreen(
    viewModel: AdminComboViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) { viewModel.loadData() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ComboSummaryCard(
                total = uiState.combos.size,
                onAdd = {
                    viewModel.startCreate()
                    showSheet = true
                }
            )
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item { ErrorBlock(message = uiState.errorMessage, onRetry = viewModel::loadData) }
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

        if (uiState.isLoading && uiState.combos.isEmpty()) {
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

        if (!uiState.isLoading && uiState.combos.isEmpty()) {
            item { EmptyComboState() }
        }

        items(uiState.combos) { combo ->
            ComboCard(
                combo = combo,
                onEdit = {
                    viewModel.startEdit(combo)
                    showSheet = true
                },
                onDelete = { viewModel.deleteCombo(combo.id) }
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            ComboForm(
                uiState = uiState,
                viewModel = viewModel,
                onSaved = { showSheet = false },
                onCancel = { showSheet = false }
            )
        }
    }
}

@Composable
private fun ComboSummaryCard(total: Int, onAdd: () -> Unit) {
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
                    Icon(Icons.Default.Star, contentDescription = null, tint = OrangeAccent)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$total combo buffet",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Đặt giá theo người và chọn món có trong combo",
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
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm combo mới", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ComboCard(
    combo: AdminComboDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isActive = combo.status == "active"
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
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = accentFg, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = combo.combo_name,
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatCurrency(combo.price_per_person) + " / người",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OrangeAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                StatusBadge(combo.status)
            }

            if (combo.item_count > 0) {
                ComboDishThumbs(comboId = combo.id)
            }

            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF0D6)) {
                Text(
                    text = "${combo.item_count} món trong combo",
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
private fun ComboForm(
    uiState: com.example.appgoimon.viewmodel.AdminComboUiState,
    viewModel: AdminComboViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangeAccent,
        focusedLabelColor = OrangeAccent,
        cursorColor = OrangeAccent
    )

    // Group menu items by category, filtered by the search box.
    val query = uiState.foodSearch.trim()
    val grouped = uiState.menuItems
        .filter { query.isEmpty() || it.name.contains(query, ignoreCase = true) }
        .groupBy { it.category_name ?: "Khác" }

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
            text = if (uiState.editingComboId == null) "Thêm combo mới" else "Sửa combo",
            style = MaterialTheme.typography.titleLarge,
            color = InkBrown,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tên combo") },
            placeholder = { Text("VD: Combo tự do 209") },
            singleLine = true,
            enabled = !uiState.isSaving,
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors
        )
        OutlinedTextField(
            value = uiState.price,
            onValueChange = viewModel::onPriceChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Giá / người") },
            placeholder = { Text("VD: 209000") },
            supportingText = { Text("Đơn vị: đồng / người") },
            singleLine = true,
            enabled = !uiState.isSaving,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors
        )
        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mô tả") },
            enabled = !uiState.isSaving,
            minLines = 2,
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors
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
                selected = uiState.status,
                onSelected = viewModel::onStatusChange
            )
        }

        Text(
            text = "Món trong combo (${uiState.selectedFoodIds.size} đã chọn)",
            style = MaterialTheme.typography.labelLarge,
            color = MutedBrown,
            fontWeight = FontWeight.SemiBold
        )
        TextField(
            value = uiState.foodSearch,
            onValueChange = viewModel::onFoodSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Tìm món để thêm...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFF7E8),
                unfocusedContainerColor = Color(0xFFFFF7E8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        if (grouped.isEmpty()) {
            Text(
                text = "Không có món phù hợp",
                style = MaterialTheme.typography.bodySmall,
                color = MutedBrown
            )
        } else {
            grouped.forEach { (categoryName, items) ->
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleSmall,
                    color = OrangeAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
                items.forEach { item ->
                    val checked = item.id in uiState.selectedFoodIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (checked) Color(0xFFFFF0D6) else Color(0xFFFAF6EF))
                            .clickable { viewModel.toggleFood(item.id) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SubcomposeAsyncImage(
                            model = resolveFoodImageUrl(item.image),
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                            loading = { ComboThumbFallback() },
                            error = { ComboThumbFallback() }
                        )
                        Text(
                            text = item.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBrown,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { viewModel.toggleFood(item.id) },
                            colors = CheckboxDefaults.colors(checkedColor = OrangeAccent)
                        )
                    }
                }
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            Text(uiState.errorMessage, color = RedFg, style = MaterialTheme.typography.bodyMedium)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onCancel,
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hủy")
            }
            Button(
                onClick = {
                    viewModel.saveCombo()
                    onSaved()
                },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
            ) {
                Text("Lưu combo", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Horizontal strip of the combo's dish thumbnails (first few) — gives each combo card real imagery. */
@Composable
private fun ComboDishThumbs(comboId: Int) {
    val itemsState = produceState<List<MenuItemDto>?>(initialValue = null, comboId) {
        value = runCatching {
            val response = RetrofitClient.apiService.getMenuByCombo(comboId)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) body.data else emptyList()
        }.getOrDefault(emptyList())
    }
    val items = itemsState.value ?: return
    if (items.isEmpty()) return

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items.take(5).forEach { item ->
            SubcomposeAsyncImage(
                model = resolveFoodImageUrl(item.image),
                contentDescription = item.name,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                loading = { ComboThumbFallback() },
                error = { ComboThumbFallback() }
            )
        }
        if (items.size > 5) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF0D6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${items.size - 5}",
                    style = MaterialTheme.typography.labelLarge,
                    color = AmberPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ComboThumbFallback() {
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
private fun EmptyComboState() {
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
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "Chưa có combo",
                style = MaterialTheme.typography.titleMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tạo combo buffet và chọn các món có trong combo đó.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}
