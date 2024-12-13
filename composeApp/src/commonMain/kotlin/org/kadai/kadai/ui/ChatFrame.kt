package org.kadai.kadai.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatFrame(isOwner: Boolean, name: String?, content: String) {
    val boxAlign = if (isOwner) Alignment.TopStart else Alignment.TopEnd
    val commentAlign = if (isOwner) Alignment.Start else Alignment.End
    BoxWithConstraints(modifier = Modifier.fillMaxWidth(), contentAlignment = boxAlign) {
        val maxWidth = maxWidth
        Column(verticalArrangement = Arrangement.spacedBy(5.dp), horizontalAlignment = commentAlign) {
            Text(name ?: "Unknown", color = Color.Foreground)
            Box(modifier = Modifier.widthIn(max = maxWidth * 0.8f).border(2.dp, color = Color.Gray, RoundedCornerShape(15.dp))) {
                Text(content, color = Color.Foreground, modifier = Modifier.padding(10.dp))
            }
        }
    }
}