package com.losad.fridgegenius.ui.components

import androidx.compose.runtime.*
import androidx.compose.material3.Text
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    fullText: String,
    speed: Long = 18L
) {
    var visibleText by remember { mutableStateOf("") }

    LaunchedEffect(fullText) {
        visibleText = ""
        fullText.forEach { char ->
            visibleText += char
            delay(speed)
        }
    }

    Text(text = visibleText)
}
