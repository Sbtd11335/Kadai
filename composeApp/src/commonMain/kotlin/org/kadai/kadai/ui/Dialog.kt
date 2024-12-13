package org.kadai.kadai.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun Dialog(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    val theme = if (isSystemInDarkTheme()) darkColors() else lightColors()
    val showDialog = rememberSaveable { mutableStateOf(true) }
    MaterialTheme(theme) {
        androidx.compose.ui.window.Dialog(onDismissRequest = {
            showDialog.value = false
        }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxSize(),
                color = Color.Background) {
                Scaffold(topBar = {
                    TopAppBar {
                        Box {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                                Text("戻る", color = Color.White, modifier = Modifier.clickable {
                                    onBack()
                                    showDialog.value = false
                                })
                            }
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(title, color = Color.White)
                            }
                        }
                    }
                }) { paddingValues ->
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding() + 10.dp),
                        contentAlignment = Alignment.TopCenter) {
                        content()
                    }
                }
            }
        }
    }
}
