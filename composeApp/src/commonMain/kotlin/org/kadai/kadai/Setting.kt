package org.kadai.kadai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.kadai.kadai.ui.Foreground
import org.kadai.kadai.ui.TextField

data class AppSetting(val token: MutableState<String>)
val appSettingSaver = mapSaver(save = {
    mapOf("token" to it.token)
}, restore = {
    AppSetting(it["token"] as MutableState<String>)
})

@Composable
fun Setting(appSetting: MutableState<AppSetting>) {
    val settingTextStyle = TextStyle(color = Color.Foreground, fontSize = TextUnit(24f, TextUnitType.Sp))
    val boxTextStyle = TextStyle(color = Color.Foreground)
    val clipBoard = LocalClipboardManager.current

    Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("Githubトークン", color = settingTextStyle.color, fontSize = settingTextStyle.fontSize)
                Text("ペースト", color = settingTextStyle.color, modifier = Modifier.clickable {
                    clipBoard.getText()?.let {
                        appSetting.value.token.value = it.text
                    }
                })
            }
            TextField(appSetting.value.token, "トークン", boxTextStyle, modifier = Modifier.fillMaxWidth().height(50.dp))
        }
    }
}