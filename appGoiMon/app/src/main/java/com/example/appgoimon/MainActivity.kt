package com.example.appgoimon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.ui.screen.admin.AdminDashboardScreen
import com.example.appgoimon.ui.screen.admin.AdminLoginScreen
import com.example.appgoimon.ui.screen.user.ComboAndGuestScreen
import com.example.appgoimon.ui.screen.user.SelectTableScreen
import com.example.appgoimon.ui.screen.user.UserMainScaffold
import com.example.appgoimon.viewmodel.OrderViewModel
import com.example.appgoimon.viewmodel.UserOrderStep
import java.util.Locale
import com.example.appgoimon.ui.theme.AppGoiMonTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppGoiMonTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentUser by remember { mutableStateOf<AuthUserDto?>(null) }
                    val currentRole = currentUser?.role?.lowercase(Locale.ROOT)
                    val orderViewModel: OrderViewModel = viewModel()
                    val orderUiState by orderViewModel.uiState.collectAsState()

                    fun logout() {
                        currentUser = null
                        orderViewModel.backToTableEntry()
                    }

                    when {
                        currentUser == null -> {
                            AdminLoginScreen(
                                onAuthSuccess = { user ->
                                    currentUser = user
                                    orderViewModel.backToTableEntry()
                                }
                            )
                        }

                        currentRole == "user" -> {
                            when (orderUiState.step) {
                                UserOrderStep.TABLE_ENTRY -> {
                                    SelectTableScreen(
                                        user = currentUser!!,
                                        uiState = orderUiState,
                                        onTableCodeChange = orderViewModel::onTableCodeChange,
                                        onSubmit = orderViewModel::checkTable,
                                        onLogout = ::logout
                                    )
                                }

                                UserOrderStep.COMBO_SETUP -> {
                                    ComboAndGuestScreen(
                                        uiState = orderUiState,
                                        onBack = orderViewModel::backToTableEntry,
                                        onLogout = ::logout,
                                        onRetryCombos = orderViewModel::loadCombos,
                                        onComboSelected = orderViewModel::selectCombo,
                                        onPaidGuestChange = orderViewModel::onPaidGuestCountChange,
                                        onFreeChildChange = orderViewModel::onFreeChildCountChange,
                                        onPaymentMethodChange = orderViewModel::onPaymentMethodChange,
                                        onCreateSession = orderViewModel::createSession
                                    )
                                }

                                UserOrderStep.ACTIVE_MENU -> {
                                    UserMainScaffold(
                                        user = currentUser!!,
                                        uiState = orderUiState,
                                        onBack = orderViewModel::backToTableEntry,
                                        onLogout = ::logout,
                                        onRetryMenu = { orderViewModel.loadMenu() },
                                        onAddToCart = orderViewModel::addToCart,
                                        onUpdateQuantity = orderViewModel::updateCartItemQuantity,
                                        onUpdateNote = orderViewModel::updateCartItemNote,
                                        onSubmitOrder = orderViewModel::submitOrder,
                                        onSearchQueryChange = orderViewModel::onSearchQueryChange,
                                        onCategorySelected = orderViewModel::onCategorySelected,
                                        onLoadOrderHistory = orderViewModel::loadOrderHistory,
                                        onRefreshOrderHistory = orderViewModel::refreshOrderHistory
                                    )
                                }
                            }
                        }

                        currentRole == "admin" -> {
                            AdminDashboardScreen(
                                onLogout = ::logout
                            )
                        }

                        else -> {
                            AdminLoginScreen(
                                onAuthSuccess = { user ->
                                    currentUser = user
                                    orderViewModel.backToTableEntry()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
