package com.example.pixelbit.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Immutable
data class MyOrdersColors(
    val statusPending: Color,
    val statusProcessing: Color,
    val statusShipped: Color,
    val statusDelivered: Color,
    val statusCancelled: Color
)

val LocalMyOrdersColors = staticCompositionLocalOf {
    MyOrdersColors(
        statusPending = Color.Unspecified,
        statusProcessing = Color.Unspecified,
        statusShipped = Color.Unspecified,
        statusDelivered = Color.Unspecified,
        statusCancelled = Color.Unspecified
    )
}

val MaterialTheme.myOrdersColors: MyOrdersColors
    @Composable
    get() = LocalMyOrdersColors.current

@Composable
fun PixelbitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    val myOrdersColors = MyOrdersColors(
        statusPending = StatusPending,
        statusProcessing = StatusProcessing,
        statusShipped = StatusShipped,
        statusDelivered = StatusDelivered,
        statusCancelled = StatusCancelled
    )

    val activity = LocalActivity.current

    LaunchedEffect(darkTheme) {
        activity?.let {
            WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
                isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalMyOrdersColors provides myOrdersColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
