package org.kadai.kadai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun IconButton(painter: Painter, modifier: Modifier = Modifier,
               color: Color = Color.Foreground, onTapped: () -> Unit) {
    Image(painter = painter, "", modifier = modifier.clickable {
        onTapped()
    }, colorFilter = ColorFilter.tint(color))
}