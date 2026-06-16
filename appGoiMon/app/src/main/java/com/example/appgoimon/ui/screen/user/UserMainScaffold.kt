package com.example.appgoimon.ui.screen.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.data.remote.MenuItemDto
import com.example.appgoimon.viewmodel.UserOrderUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScaffold(
    user: AuthUserDto,
    uiState: UserOrderUiState,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onRetryMenu: () -> Unit,
    onAddToCart: (MenuItemDto) -> Unit,
    onUpdateQuantity: (MenuItemDto, Int) -> Unit,
    onUpdateNote: (MenuItemDto, String) -> Unit,
    onSubmitOrder: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onLoadOrderHistory: () -> Unit,
    onRefreshOrderHistory: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Menu") },
                    label = { Text("Menu") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )

                NavigationBarItem(
                    icon = {
                        if (uiState.cartItemCount > 0) {
                            BadgedBox(
                                badge = { Badge { Text(uiState.cartItemCount.toString()) } }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Gio hang")
                            }
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Gio hang")
                        }
                    },
                    label = { Text("Gio") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Da goi") },
                    label = { Text("Da goi") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> MenuScreen(
                    uiState = uiState,
                    onSearchQueryChange = onSearchQueryChange,
                    onCategorySelected = onCategorySelected,
                    onRetryMenu = onRetryMenu,
                    onAddToCart = onAddToCart,
                    onUpdateQuantity = onUpdateQuantity
                )

                1 -> CartScreen(
                    cartItems = uiState.cartItems,
                    isSubmitting = uiState.isSubmittingOrder,
                    isExpired = uiState.isSessionExpired,
                    onUpdateQuantity = onUpdateQuantity,
                    onUpdateNote = onUpdateNote,
                    onSubmit = onSubmitOrder
                )

                2 -> OrderHistoryScreen(
                    uiState = uiState,
                    onLoadHistory = onLoadOrderHistory,
                    onRefresh = onRefreshOrderHistory,
                    onRetry = onLoadOrderHistory
                )
            }
        }
    }
}
