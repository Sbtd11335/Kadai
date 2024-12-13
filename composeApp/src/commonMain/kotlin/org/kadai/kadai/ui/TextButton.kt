package org.kadai.kadai.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun TextButton(text: String, textStyle: TextStyle = TextStyle.Default,
               modifier: Modifier = Modifier, onTapped: () -> Unit) {
    Box(modifier = modifier.border(2.dp, Color.Gray, shape = RoundedCornerShape(15.dp)).clickable {
        onTapped()
    },
        contentAlignment = Alignment.Center) {
        Text(text, fontSize = textStyle.fontSize, color = Color.Gray)
    }
}