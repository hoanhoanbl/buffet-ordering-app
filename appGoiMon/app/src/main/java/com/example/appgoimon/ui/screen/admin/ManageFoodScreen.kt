package com.example.appgoimon.ui.screen.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

private val GreenFg = Color(0xFF15803D)
private val GreenBg = Color(0xFFE7F6EC)
private val RedFg = Color(0xFFC23B22)
private val RedBg = Color(0xFFFFE0DC)
private val NeutralFg = Color(0xFF6D5A45)
private val NeutralBg = Color(0xFFF1ECE3)

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
            item {
                FilterChip(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Combo") },
                    leadingIcon = if (selectedTab == 2) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else {
                        null
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE1D2))
                )
            }
        }

        when (selectedTab) {
            0 -> MenuItemsScreen()
            1 -> ManageCategoryScreen()
            else -> ManageComboScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuItemsScreen(
    viewModel: AdminFoodViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.loadMenuData()
    }

    val filtered = remember(uiState.menuItems, search) {
        if (search.isBlank()) {
            uiState.menuItems
        } else {
            uiState.menuItems.filter {
                it.name.contains(search, ignoreCase = true) ||
                    (it.category_name?.contains(search, ignoreCase = true) == true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MenuSummaryCard(
                total = uiState.menuItems.size,
                onAdd = {
                    viewModel.startCreate()
                    showSheet = true
                }
            )
        }

        item {
            MenuSearchField(query = search, onQueryChange = { search = it })
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

        if (uiState.isLoading && uiState.menuItems.isEmpty()) {
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

        if (!uiState.isLoading && filtered.isEmpty()) {
            item {
                Text(
                    text = if (search.isBlank()) "Chưa có món nào" else "Không tìm thấy món phù hợp",
                    color = MutedBrown,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        }

        items(filtered) { item ->
            MenuItemCard(
                item = item,
                onEdit = {
                    viewModel.startEdit(item)
                    showSheet = true
                },
                onStatus = { status -> viewModel.setMenuItemStatus(item.id, status) },
                onDelete = { viewModel.deleteMenuItem(item.id) }
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            MenuItemForm(
                title = if (uiState.editingFoodId == null) "Thêm món mới" else "Sửa món",
                categories = uiState.categories,
                selectedCategoryId = uiState.categoryId,
                name = uiState.name,
                image = uiState.image,
                description = uiState.description,
                status = uiState.status,
                isLoading = uiState.isLoading,
                isUploadingImage = uiState.isUploadingImage,
                onCategoryChange = viewModel::onCategoryChange,
                onNameChange = viewModel::onNameChange,
                onImagePicked = viewModel::uploadImage,
                onDescriptionChange = viewModel::onDescriptionChange,
                onStatusChange = viewModel::onStatusChange,
                onSave = {
                    viewModel.saveMenuItem()
                    showSheet = false
                },
                onCancel = { showSheet = false }
            )
        }
    }
}

@Composable
private fun MenuSummaryCard(total: Int, onAdd: () -> Unit) {
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
                Text("Thêm món mới", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MenuSearchField(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Tìm món theo tên hoặc danh mục...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangeAccent) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Xóa", tint = MutedBrown)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
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
    isUploadingImage: Boolean,
    onCategoryChange: (Int) -> Unit,
    onNameChange: (String) -> Unit,
    onImagePicked: (ByteArray, String, String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OrangeAccent,
        focusedLabelColor = OrangeAccent
    )

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

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Danh mục",
                style = MaterialTheme.typography.labelLarge,
                color = MutedBrown,
                fontWeight = FontWeight.SemiBold
            )
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
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFE1D2),
                            selectedLabelColor = OrangeAccent
                        )
                    )
                }
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tên món") },
            singleLine = true,
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors
        )
        ImagePickerField(
            image = image,
            isUploading = isUploadingImage,
            enabled = !isLoading,
            onImagePicked = onImagePicked
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mô tả") },
            enabled = !isLoading,
            minLines = 2,
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Trạng thái bán",
                style = MaterialTheme.typography.labelLarge,
                color = MutedBrown,
                fontWeight = FontWeight.SemiBold
            )
            StatusSelector(
                options = listOf("available", "out_of_stock", "hidden"),
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
                enabled = !isLoading && !isUploadingImage,
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
                    Text("Lưu món", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Tap-to-pick food image using the system photo picker (no storage permission needed). The picked
 * bytes are uploaded immediately via [onImagePicked]; the stored image is previewed once available.
 */
@Composable
private fun ImagePickerField(
    image: String,
    isUploading: Boolean,
    enabled: Boolean,
    onImagePicked: (ByteArray, String, String) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
            val bytes = runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()
            if (bytes != null) {
                val ext = mime.substringAfter('/', "jpg")
                onImagePicked(bytes, mime, "upload.$ext")
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Ảnh món",
            style = MaterialTheme.typography.labelLarge,
            color = MutedBrown,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF3D8))
                .border(1.dp, OrangeAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .clickable(enabled = enabled && !isUploading) {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                isUploading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(color = OrangeAccent, strokeWidth = 2.dp)
                        Text("Đang tải ảnh...", style = MaterialTheme.typography.bodySmall, color = MutedBrown)
                    }
                }

                image.isNotBlank() -> {
                    SubcomposeAsyncImage(
                        model = resolveFoodImageUrl(image),
                        contentDescription = "Ảnh món",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = { ImageFallbackBox() },
                        error = { ImageFallbackBox() }
                    )
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE1D2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = OrangeAccent)
                        }
                        Text(
                            "Chạm để chọn ảnh từ thư viện",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedBrown
                        )
                    }
                }
            }
        }
        if (image.isNotBlank() && !isUploading) {
            Text(
                text = "Chạm vào ảnh để đổi ảnh khác",
                style = MaterialTheme.typography.bodySmall,
                color = MutedBrown
            )
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
    val (accentFg, _) = menuAccent(item.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Status accent stripe painted via drawBehind — works at any height without intrinsic
        // measurement (SubcomposeAsyncImage doesn't support intrinsics, which would mis-size a Row).
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(color = accentFg, size = Size(width = 5.dp.toPx(), height = size.height))
                }
                .padding(start = 17.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
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

            // Quick status switch — change availability without opening the editor.
            StatusSelector(
                options = listOf("available", "out_of_stock", "hidden"),
                selected = item.status,
                onSelected = onStatus
            )

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
                    Text("Xóa", color = RedFg)
                }
            }
        }
    }
}

/** Maps a menu-item status to (foreground, soft background) for the card accent stripe. */
private fun menuAccent(status: String): Pair<Color, Color> = when (status) {
    "available" -> GreenFg to GreenBg
    "out_of_stock" -> RedFg to RedBg
    else -> NeutralFg to NeutralBg
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
