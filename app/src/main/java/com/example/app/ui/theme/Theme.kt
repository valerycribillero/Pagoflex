package com.example.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primario,
    onPrimary = Color.White,
    primaryContainer = PrimarioOscuro,
    onPrimaryContainer = PrimarioClaro,
    secondary = Secundario,
    onSecondary = Color.White,
    secondaryContainer = Secundario,
    onSecondaryContainer = SecundarioClaro,
    tertiary = Secundario,
    onTertiary = Color.White,
    background = Color(0xFF14131A),
    onBackground = Color(0xFFECEAF4),
    surface = Color(0xFF1F1D27),
    onSurface = Color(0xFFECEAF4)
)

private val LightColorScheme = lightColorScheme(
    primary = Primario,
    onPrimary = Color.White,
    primaryContainer = PrimarioClaro,
    onPrimaryContainer = PrimarioOscuro,
    secondary = Secundario,
    onSecondary = Color.White,
    secondaryContainer = SecundarioClaro,
    onSecondaryContainer = Secundario,
    tertiary = Secundario,
    onTertiary = Color.White,
    background = FondoClaro,
    surface = Color.White,
    onBackground = Color(0xFF1C1B22),
    onSurface = Color(0xFF1C1B22)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

@Composable
fun AppThemePreview(content: @Composable () -> Unit) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            content()
        }
    }
}
