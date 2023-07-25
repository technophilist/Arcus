package com.example.justweather.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.justweather.R
import com.example.justweather.domain.hourStringInTwelveHourFormat
import com.example.justweather.domain.models.HourlyForecast
import java.time.LocalDateTime

/**
 * A [CompactWeatherCard] with a scrollable hourly forecasts list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactWeatherCardWithHourlyForecast(
    nameOfLocation: String,
    shortDescription: String,
    @DrawableRes shortDescriptionIcon: Int,
    weatherInDegrees: String,
    onClick: () -> Unit,
    hourlyForecasts: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    val weatherWithDegreesSuperscript = remember(weatherInDegrees) {
        // note: the weather superscript used here is not the default one that
        // is available on Macs by using the shortcut option + 0. The one used in
        // MacOS does not look good in the user interface.
        "$weatherInDegrees°"
    }
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nameOfLocation,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                ShortWeatherDescriptionWithIconRow(
                    shortDescription = shortDescription,
                    iconRes = shortDescriptionIcon
                )
            }
            Text(
                text = weatherWithDegreesSuperscript,
                style = MaterialTheme.typography.displayMedium
            )
        }
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(hourlyForecasts) {
                HourlyForecastItem(
                    dateTime = it.dateTime,
                    iconResId = it.weatherIconResId,
                    temperature = it.temperature
                )
            }
        }
    }
}

@Composable
private fun HourlyForecastItem(
    modifier: Modifier = Modifier,
    dateTime: LocalDateTime,
    @DrawableRes iconResId: Int,
    temperature: Int
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dateTime.hourStringInTwelveHourFormat,
            style = MaterialTheme.typography.labelLarge
        )
        // Explicitly set tint to Color.Unspecified to ensure that no tint is applied to the vector
        // resource. See documentation of the Icon composable for more information.
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = ImageVector.vectorResource(id = iconResId),
            contentDescription = null,
            tint = Color.Unspecified
        )
        // note: the weather superscript used here is not the default one that
        // is available on MacOS by using the shortcut option + 0. The one used in
        // MacOS does not look good in the user interface.
        Text(
            text = "${temperature}°",
            style = MaterialTheme.typography.labelLarge
        )
    }

}

@Composable
private fun ShortWeatherDescriptionWithIconRow(
    shortDescription: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Explicitly set tint to Color.Unspecified to ensure that no tint is applied to the vector
        // resource. See documentation of the Icon composable for more information.
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified
        )
        Text(
            text = shortDescription,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Normal
        )
    }
}