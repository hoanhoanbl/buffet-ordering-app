package com.example.appgoimon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.appgoimon.ui.screen.admin.AdminDashboardScreen
import com.example.appgoimon.ui.screen.admin.AdminLoginScreen
import com.example.appgoimon.ui.screen.admin.ManageTableScreen
import com.example.appgoimon.ui.theme.AppGoiMonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppGoiMonTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                var selectedTableId by remember { mutableStateOf<Int?>(null) }

                when {
                    !isLoggedIn -> {
                        AdminLoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                                selectedTableId = null
                            }
                        )
                    }

                    selectedTableId == null -> {
                        AdminDashboardScreen(
                            onTableClick = { tableId ->
                                selectedTableId = tableId
                            }
                        )
                    }

                    else -> {
                        ManageTableScreen(
                            tableId = selectedTableId!!,
                            onBackClick = {
                                selectedTableId = null
                            }
                        )
                    }
                }
            }
        }
    }
}