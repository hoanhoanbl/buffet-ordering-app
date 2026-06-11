package com.example.appgoimon.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AmberPrimarySoft,
    secondary = OrangeAccent,
    tertiary = AmberPrimary,
    background = DarkChocolate,
    surface = DarkSurface,
    onPrimary = DarkChocolate,
    onSecondary = WarmSurface,
    onTertiary = DarkChocolate,
    onBackground = WarmCream,
    onSurface = WarmCream
)

private val LightColorScheme = lightColorScheme(
    primary = AmberPrimaryDark,
    secondary = OrangeAccent,
    tertiary = AmberPrimary,
    background = WarmCream,
    surface = WarmSurface,
    onPrimary = WarmSurface,
    onSecondary = WarmSurface,
    onTertiary = InkBrown,
    onBackground = InkBrown,
    onSurface = InkBrown
)

@Composable
fun AppGoiMonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
