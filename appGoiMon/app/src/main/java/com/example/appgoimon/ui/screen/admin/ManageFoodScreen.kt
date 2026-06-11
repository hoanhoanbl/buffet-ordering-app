package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.data.remote.CategoryDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.AdminFoodViewModel

@Composable
fun ManageFoodScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text("Mon") }
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text("Danh muc") }
            )
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            MenuItemForm(
                title = if (uiState.editingFoodId == null) "Them mon" else "Sua mon",
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
                CircularProgressIndicator(color = AmberPrimaryDark)
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
                Text(uiState.successMessage, color = AmberPrimaryDark)
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories.filter { it.status == "active" }) { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { onCategoryChange(category.id) },
                        label = { Text(category.category_name) }
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ten mon") },
                singleLine = true,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = image,
                onValueChange = onImageChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Anh") },
                singleLine = true,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mo ta") },
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Luu")
                }
                OutlinedButton(
                    onClick = onNew,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Moi")
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(item.category_name ?: "Khong co danh muc", color = MutedBrown)
                }
                StatusBadge(item.status)
            }

            if (!item.description.isNullOrBlank()) {
                Text(item.description, color = MutedBrown)
            }
            if (!item.image.isNullOrBlank()) {
                Text("Anh: ${item.image}", color = OrangeAccent)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Text("Sua")
                }
                OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Text("Xoa")
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
