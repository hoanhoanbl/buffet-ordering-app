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
import com.example.appgoimon.data.local.AuthPreferences
import com.example.appgoimon.data.remote.AuthUserDto
import com.example.appgoimon.ui.screen.admin.AdminDashboardScreen
import com.example.appgoimon.ui.screen.admin.AdminLoginScreen
import com.example.appgoimon.ui.screen.user.ComboAndGuestScreen
import com.example.appgoimon.ui.screen.user.SelectTableScreen
import com.example.appgoimon.ui.screen.user.UserMainScaffold
import com.example.appgoimon.ui.screen.user.WaitingPaymentScreen
import com.example.appgoimon.viewmodel.OrderViewModel
import com.example.appgoimon.viewmodel.UserOrderStep
import java.util.Locale
import com.example.appgoimon.ui.theme.AppGoiMonTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

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
                    val context = LocalContext.current
                    val authPreferences = remember { AuthPreferences(context) }
                    // Restore the saved user synchronously on cold start so a logged-in user never sees
                    // the login screen flash (AC2). The read is a tiny SharedPreferences lookup.
                    var currentUser by remember { mutableStateOf(authPreferences.loadUser()) }
                    val currentRole = currentUser?.role?.lowercase(Locale.ROOT)
                    val orderViewModel: OrderViewModel = viewModel()
                    val orderUiState by orderViewModel.uiState.collectAsState()

                    // On cold start with a restored "user" account, scope the ViewModel to that user and
                    // auto-resume their single active session (AC6). Runs once per restored user.
                    LaunchedEffect(Unit) {
                        currentUser?.let { restored ->
                            orderViewModel.setCurrentUserId(restored.id)
                            if (restored.role.lowercase(Locale.ROOT) == "user") {
                                orderViewModel.resumeActiveSession()
                            }
                        }
                    }

                    fun logout() {
                        // Clear the saved account only; the running table session is NOT closed (AC3).
                        authPreferences.clear()
                        currentUser = null
                        orderViewModel.setCurrentUserId(0)
                        orderViewModel.backToTableEntry()
                    }

                    fun onAuthSuccess(user: AuthUserDto) {
                        authPreferences.saveUser(user)
                        currentUser = user
                        orderViewModel.setCurrentUserId(user.id)
                        orderViewModel.backToTableEntry()
                        if (user.role.lowercase(Locale.ROOT) == "user") {
                            orderViewModel.resumeActiveSession()
                        }
                    }

                    when {
                        currentUser == null -> {
                            AdminLoginScreen(
                                onAuthSuccess = ::onAuthSuccess
                            )
                        }

                        currentRole == "user" -> {
                            when (orderUiState.step) {
                                UserOrderStep.TABLE_ENTRY -> {
                                    LaunchedEffect(currentUser) {
                                        orderViewModel.loadTables()
                                    }
                                    SelectTableScreen(
                                        user = currentUser!!,
                                        uiState = orderUiState,
                                        onTableCodeChange = orderViewModel::onTableCodeChange,
                                        onSubmit = orderViewModel::checkTable,
                                        onTableSelected = { code ->
                                            orderViewModel.onTableCodeChange(code)
                                            orderViewModel.checkTable()
                                        },
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

                                UserOrderStep.WAITING_PAYMENT -> {
                                    WaitingPaymentScreen(
                                        user = currentUser!!,
                                        uiState = orderUiState,
                                        onRefresh = orderViewModel::refreshSessionStatus,
                                        onSimulatePayment = orderViewModel::simulatePayment,
                                        onBack = orderViewModel::backToTableEntry,
                                        onLogout = ::logout
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
                                        onRemoveItem = orderViewModel::removeFromCart,
                                        onSubmitOrder = orderViewModel::submitOrder,
                                        onSearchQueryChange = orderViewModel::onSearchQueryChange,
                                        onCategorySelected = orderViewModel::onCategorySelected,
                                        onLoadOrderHistory = orderViewModel::loadOrderHistory,
                                        onRefreshOrderHistory = orderViewModel::refreshOrderHistory,
                                        onOrderSuccessConsumed = orderViewModel::consumeOrderSuccess
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
                                onAuthSuccess = ::onAuthSuccess
                            )
                        }
                    }
                }
            }
        }
    }
}
