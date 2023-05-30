package com.example.justweather.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.justweather.BuildConfig
import com.example.justweather.ui.navigation.JustWeatherNavigation
import com.example.justweather.ui.theme.JustWeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JustWeatherTheme {
                Surface(content = { JustWeatherNavigation() })
            }
        }
    }
}