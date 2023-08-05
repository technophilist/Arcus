package com.example.arcus.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val JustWeatherDarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    inversePrimary = Blue40,
    secondary = DarkBlue80,
    onSecondary = DarkBlue20,
    secondaryContainer = DarkBlue30,
    onSecondaryContainer = DarkBlue90,
    tertiary = Yellow80,
    onTertiary = Yellow20,
    tertiaryContainer = Yellow30,
    onTertiaryContainer = Yellow90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey20,
    surfaceVariant = BlueGrey30,
    onSurfaceVariant = BlueGrey80,
    outline = BlueGrey60
)

@Composable
fun JustWeatherTheme(areDynamicColorsEnabled: Boolean = true, content: @Composable () -> Unit) {
    val doesDeviceSupportDynamicColors =
        areDynamicColorsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current
    MaterialTheme(
        colorScheme = if (doesDeviceSupportDynamicColors) dynamicDarkColorScheme(context)
        else JustWeatherDarkColorScheme,
        typography = Typography,
        content = content
    )
}