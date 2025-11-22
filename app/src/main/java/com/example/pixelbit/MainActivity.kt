package com.example.pixelbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.navigation.NavGraph
import com.example.pixelbit.presentation.theme.PixelbitTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val navigator: AppNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixelbitTheme {
                NavGraph(navController = navigator)
            }
        }
    }
}
