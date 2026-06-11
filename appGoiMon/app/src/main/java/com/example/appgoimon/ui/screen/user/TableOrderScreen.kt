package com.example.appgoimon.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.data.remote.RetrofitClient
import com.example.appgoimon.ui.theme.AmberPrimaryDark
import com.example.appgoimon.ui.theme.InkBrown
import com.example.appgoimon.ui.theme.MutedBrown
import com.example.appgoimon.ui.theme.OrangeAccent
import com.example.appgoimon.viewmodel.UserOrderUiState

@Composable
fun TableOrderScreen(
    user: AuthUserDto,
    uiState: UserOrderUiState,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onRetryMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF7E8), Color(0xFFFFE2AA))
                )
            ),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = uiState.session?.table_name
                            ?: uiState.table?.table_name
                            ?: "Ban ${uiState.tableCode}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = InkBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.session?.combo_name ?: uiState.selectedCombo?.name ?: user.username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedBrown
                    )
                }

                TextButton(onClick = onLogout) {
                    Text("Dang xuat", color = OrangeAccent)
                }
            }
        }

        item {
            TextButton(onClick = onBack) {
                Text("Chon ban khac")
            }
        }

        if (uiState.isMenuLoading) {
            item {
                CircularProgressIndicator(color = AmberPrimaryDark)
            }
        }

        if (uiState.errorMessage.isNotEmpty()) {
            item {
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
                        Button(onClick = onRetryMenu) {
                            Text("Thu lai")
                        }
                    }
                }
            }
        }

        if (!uiState.isMenuLoading && uiState.menuItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Combo nay chua co mon kha dung",
                        modifier = Modifier.padding(16.dp),
                        color = MutedBrown
                    )
                }
            }
        }

        items(uiState.menuItems) { item ->
            MenuFoodCard(item = item)
        }
    }
}

@Composable
private fun MenuFoodCard(item: MenuItemDto) {
    val imageUrl = resolveFoodImageUrl(item.image)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AmberPrimaryDark)
                        }
                    },
                    error = {
                        ImageFallbackText("Khong tai duoc anh")
                    }
                )
            } else {
                ImageFallbackText("Chua co anh mon")
            }
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = InkBrown,
                fontWeight = FontWeight.Bold
            )
            Text(item.category_name ?: "Khong co danh muc", color = OrangeAccent)
            if (!item.description.isNullOrBlank()) {
                Text(item.description, color = MutedBrown)
            }
            Text("Trang thai: ${item.status}", color = MutedBrown)
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
            .height(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFF3D8)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = MutedBrown)
    }
}
