package org.kadai.kadai.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun Alert(title: String, text: String, onOK: () -> Unit) {
    val showAlert = rememberSaveable { mutableStateOf(true) }

    AlertDialog(onDismissRequest = { showAlert.value = false }, buttons = {
        androidx.compose.material.TextButton(onClick = {
            onOK()
            showAlert.value = false
        }) {
            Text("OK")
        }
    }, title = {
        Text(title)
    }, text = {
        Text(text)
    })
}