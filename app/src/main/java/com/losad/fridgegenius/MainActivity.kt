package com.losad.fridgegenius

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.losad.fridgegenius.ui.nav.AppNav
import com.losad.fridgegenius.ui.theme.FridgeGeniusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FridgeGeniusTheme {
                AppNav()
            }
        }
    }
}
