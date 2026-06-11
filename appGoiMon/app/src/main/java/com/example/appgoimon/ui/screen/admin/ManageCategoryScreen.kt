package com.example.appgoimon.ui.screen.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.data.remote.CategoryDto
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.viewmodel.AdminCategoryViewModel

@Composable
fun ManageCategoryScreen(
    viewModel: AdminCategoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            CategoryForm(
                title = if (uiState.editingCategoryId == null) "Them danh muc" else "Sua danh muc",
                name = uiState.name,
                status = uiState.status,
                isLoading = uiState.isLoading,
                onNameChange = viewModel::onNameChange,
                onStatusChange = viewModel::onStatusChange,
                onSave = viewModel::saveCategory,
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
                    onRetry = viewModel::loadCategories
                )
            }
        }

        if (uiState.successMessage.isNotEmpty()) {
            item {
                Text(uiState.successMessage, color = AmberPrimaryDark)
            }
        }

        items(uiState.categories) { category ->
            CategoryCard(
                category = category,
                onEdit = { viewModel.startEdit(category) },
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
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ten danh muc") },
                singleLine = true,
                enabled = !isLoading
            )
            StatusSelector(
                options = listOf("active", "inactive"),
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
private fun CategoryCard(
    category: CategoryDto,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
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
                Text(
                    text = category.category_name,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(category.status)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Text("Sua")
                }
                OutlinedButton(onClick = onToggleStatus, modifier = Modifier.weight(1f)) {
                    Text(if (category.status == "active") "Tat" else "Bat")
                }
                OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Text("Xoa")
                }
            }
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
                label = { Text(option) }
            )
        }
    }
}
