package org.kadai.kadai.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Color.Companion.Background: Color
    @Composable
    get() = MaterialTheme.colors.background
val Color.Companion.Foreground: Color
    @Composable
    get() = MaterialTheme.colors.onBackground
fun Color.Companion.hexString(hex: String): Color {
    val red = hex.substring(0..<2).toInt(16)
    val green = hex.substring(2..<4).toInt(16)
    val blue = hex.substring(4..<6).toInt(16)
    return Color(red, green, blue)
}