package org.kadai.kadai.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun TextField(text: MutableState<String>, label: String = "",
              textStyle: TextStyle = TextStyle.Default,
              singleLine: Boolean = true,
              modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .border(2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.CenterStart) {
        BasicTextField(text.value, {
            text.value = it
        }, singleLine = singleLine, textStyle = textStyle, cursorBrush = SolidColor(textStyle.color),
            modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val align = if (singleLine) Alignment.CenterStart else Alignment.TopStart
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = align) {
                if (text.value.isEmpty())
                    Text(label, color = Color.Gray, fontSize = textStyle.fontSize)
                else
                    it()
            }
        }
    }
}