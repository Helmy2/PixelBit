package com.example.pixelbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.navigation.NavGraph
import com.example.pixelbit.presentation.navigation.Screen
import com.example.pixelbit.presentation.theme.PixelbitTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixelbitTheme {
                val appNavigator = remember { AppNavigator(Screen.Splash) }
                NavGraph(navController = appNavigator)
            }
        }
    }
}
