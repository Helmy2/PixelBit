package com.example.pixelbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pixelbit.presentation.navigation.AppNavigator
import com.example.pixelbit.presentation.navigation.NavGraph
import com.example.pixelbit.presentation.theme.PixelbitTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()
        val navigator: AppNavigator by inject()
        setContent {
            val keepSplashScreen by navigator.shouldKeepSplashScreenFlow()
                .collectAsStateWithLifecycle(true)
            SideEffect {
                splashScreen.setKeepOnScreenCondition {
                    keepSplashScreen
                }
            }
            PixelbitTheme {
                if (!keepSplashScreen) {
                    NavGraph(navController = navigator)
                }
            }
        }
    }
}
