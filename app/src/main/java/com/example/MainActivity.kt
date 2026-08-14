package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.navigation.PricePilotNavGraph
import com.example.ui.theme.PricePilotTheme
import com.example.ui.viewmodel.PricePilotViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PricePilotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            PricePilotTheme(darkTheme = darkTheme) {
                PricePilotNavGraph(viewModel = viewModel)
            }
        }
    }
}
