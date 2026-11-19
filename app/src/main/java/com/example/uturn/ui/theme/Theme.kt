package com.example.uturn.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



private val UturnColorScheme = lightColorScheme(
    primary = UturnGreen,
    onPrimary = Color.White,
    primaryContainer = UturnGreenLight,
    onPrimaryContainer = UturnGreenDark,
    secondary = UturnGreenDark,
    background = UturnBackground,
    surface = UturnBackground,
    onBackground = UturnTextDark,
    onSurface = UturnTextDark,
    outline = UturnCardBorder
)

@Composable
fun UturnCoffeeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = UturnColorScheme,
        typography = Typography,
        content = content
    )
}