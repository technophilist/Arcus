@file:OptIn(ExperimentalFoundationApi::class)

package com.example.justweather.ui.weather

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.justweather.R
import com.example.justweather.domain.models.HourlyForecast
import com.example.justweather.domain.models.PrecipitationProbability
import com.example.justweather.domain.models.SingleWeatherDetail
import com.example.justweather.ui.components.HourlyForecastCard
import com.example.justweather.ui.components.PrecipitationProbabilitiesCard
import com.example.justweather.ui.components.SingleWeatherDetailCard



// todo - need to add top bar when screen is fully scrolled
@Composable
fun WeatherDetailsV2(
    nameOfLocation: String,
    @DrawableRes weatherConditionImage: Int,
    @DrawableRes weatherConditionIconId: Int,
    weatherInDegrees: Int,
    weatherCondition: String,
    onBackButtonClick: () -> Unit,
    singleWeatherDetails: List<SingleWeatherDetail>,
    hourlyForecasts: List<HourlyForecast>,
    precipitationProbabilities: List<PrecipitationProbability>
) {
    require(singleWeatherDetails.size >= 4)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Header(
                modifier = Modifier
                    .requiredWidth(screenWidth)
                    .height(360.dp),
                headerImageResId = weatherConditionImage,
                weatherConditionIconId = weatherConditionIconId,
                onBackButtonClick = onBackButtonClick,
                nameOfLocation = nameOfLocation,
                currentWeatherInDegrees = weatherInDegrees,
                weatherCondition = weatherCondition
            )
        }

        items(singleWeatherDetails.subList(0, 4)) {
            SingleWeatherDetailCard(
                name = it.name,
                value = it.value,
                iconResId = it.iconResId
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            PrecipitationProbabilitiesCard(precipitationProbabilities = precipitationProbabilities)
        }


        item(span = { GridItemSpan(maxLineSpan) }) {
            HourlyForecastCard(hourlyForecasts = hourlyForecasts)
        }

        items(singleWeatherDetails.subList(4, singleWeatherDetails.size)) {
            SingleWeatherDetailCard(
                name = it.name,
                value = it.value,
                iconResId = it.iconResId
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    @DrawableRes headerImageResId: Int,
    @DrawableRes weatherConditionIconId: Int,
    onBackButtonClick: () -> Unit,
    nameOfLocation: String,
    currentWeatherInDegrees: Int,
    weatherCondition: String,
) {
    Box(modifier = modifier) {
        val iconButtonContainerColor by animateColorAsState(
            targetValue = if (isSystemInDarkTheme()) Color.Black.copy(0.4f)
            else Color.White.copy(0.4f)
        )
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = headerImageResId),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        //scrim for image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = iconButtonContainerColor
            ),
            onClick = onBackButtonClick
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.offset(y = 24.dp),
                text = nameOfLocation,
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "$currentWeatherInDegrees°",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp)
            )
            Row(
                modifier = Modifier.offset(y = (-24).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(id = weatherConditionIconId),
                    contentDescription = null
                )
                Text(text = weatherCondition)
            }
        }
    }
}