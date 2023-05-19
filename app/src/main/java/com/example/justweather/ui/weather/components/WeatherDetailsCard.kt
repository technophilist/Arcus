package com.example.justweather.ui.weather.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.justweather.R
import com.example.justweather.domain.models.WeatherDetails


/**
 * A data class that contains all weather icons used in [WeatherDetailsCard].
 *
 * @param minTempIcon The minimum temperature icon.
 * @param maxTempIcon The maximum temperature icon.
 * @param humidityIcon The humidity icon.
 * @param windSpeedIcon The wind speed icon.
 * @param windDirectionIcon The wind direction icon.
 * @param pressureIcon The pressure icon.
 */
data class WeatherIcons(
    @DrawableRes val minTempIcon: Int = R.drawable.ic_min_temp,
    @DrawableRes val maxTempIcon: Int = R.drawable.ic_max_temp,
    @DrawableRes val humidityIcon: Int = R.drawable.ic_humidity,
    @DrawableRes val windSpeedIcon: Int = R.drawable.ic_wind,
    @DrawableRes val windDirectionIcon: Int = R.drawable.ic_wind_direction,
    @DrawableRes val pressureIcon: Int = R.drawable.ic_wind_pressure
)


/**
 * A Weather details card composable that displays the [weatherDetails] with the appropriate
 * [weatherDetailIcons], with the provided [shortWeatherDescription].
 * @param modifier the modifier to be applied to the composable
 * @param cardColors  [CardColors] that will be used to resolve the colors used for this card in
 * different states. See [CardDefaults.cardColors].
 */
@ExperimentalLayoutApi
@Composable
fun WeatherDetailsCard(
    weatherDetails: WeatherDetails,
    shortWeatherDescription: String,
    modifier: Modifier = Modifier,
    weatherDetailIcons: WeatherIcons = WeatherIcons(),
    cardColors: CardColors = CardDefaults.cardColors()
) {
    Card(
        modifier = Modifier
            .navigationBarsPadding()
            .then(modifier),
        colors = cardColors
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = shortWeatherDescription,
            maxLines = 2,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardItem(
                title = "Low",
                value = weatherDetails.temperature.minTemperature,
                imageVector = ImageVector.vectorResource(weatherDetailIcons.minTempIcon)
            )
            CardItem(
                title = "High",
                value = weatherDetails.temperature.maxTemperature,
                imageVector = ImageVector.vectorResource(weatherDetailIcons.maxTempIcon)
            )
            CardItem(
                title = "Humidity",
                value = weatherDetails.humidity,
                imageVector = ImageVector.vectorResource(weatherDetailIcons.humidityIcon)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardItem(
                title = "Wind Speed",
                value = weatherDetails.wind.speed,
                imageVector = ImageVector.vectorResource(weatherDetailIcons.windSpeedIcon)
            )
            CardItem(
                title = "Wind Direction",
                value = weatherDetails.wind.direction,
                imageVector = ImageVector.vectorResource(weatherDetailIcons.windDirectionIcon)
            )
            CardItem(
                title = "Pressure",
                value = weatherDetails.pressure,
                imageVector = ImageVector.vectorResource(weatherDetailIcons.pressureIcon)
            )
        }
    }
}

/**
 * A composable that represents one item in [WeatherDetailsCard].
 */
@Composable
private fun CardItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    imageVector: ImageVector
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = imageVector,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // applied emphasis according to material design spec
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), // applied emphasis according to material design spec
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Medium
        )
    }
}