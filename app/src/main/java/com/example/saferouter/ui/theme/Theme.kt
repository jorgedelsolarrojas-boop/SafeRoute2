package com.example.saferouter.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Color(0xFF4A55D2),      // primary
    primaryVariant = Color(0xFF303AB3), // primary_dark
    secondary = Color(0xFFFF6B35),    // secondary
    background = Color(0xFFF5F5F5),   // background
    surface = Color(0xFFFFFFFF),      // surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF212121), // text_primary
    onSurface = Color(0xFF212121)     // text_primary
)

@Composable
fun SafeRouterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightColorPalette,
        // typography = Typography,    // Comenta si no existe
        // shapes = Shapes,           // Comenta si no existe
        content = content
    )
}