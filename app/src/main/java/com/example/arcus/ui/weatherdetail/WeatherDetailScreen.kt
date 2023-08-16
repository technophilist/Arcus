package com.example.arcus.ui.weatherdetail

import android.os.Build.VERSION.SDK_INT
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.arcus.R
import com.example.arcus.domain.models.weather.HourlyForecast
import com.example.arcus.domain.models.weather.PrecipitationProbability
import com.example.arcus.domain.models.weather.SingleWeatherDetail
import com.example.arcus.ui.components.HourlyForecastCard
import com.example.arcus.ui.components.PrecipitationProbabilitiesCard
import com.example.arcus.ui.components.SingleWeatherDetailCard
import com.example.arcus.ui.components.TypingAnimatedText


/**
 * An overload that uses [WeatherDetailScreenUiState].
 */
@Composable
fun WeatherDetailScreen(
    uiState: WeatherDetailScreenUiState,
    snackbarHostState: SnackbarHostState,
    onSaveButtonClick: () -> Unit,
    onBackButtonClick: () -> Unit,
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            content = { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) }
        )
    } else if (uiState.errorMessage != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                text = uiState.errorMessage
            )
            Button(onClick = onBackButtonClick, content = { Text("Go back") })
        }
    } else {
        WeatherDetailScreen(
            snackbarHostState = snackbarHostState,
            nameOfLocation = uiState.weatherDetailsOfChosenLocation!!.nameOfLocation,
            weatherConditionImage = uiState.weatherDetailsOfChosenLocation.imageResId,
            weatherConditionIconId = uiState.weatherDetailsOfChosenLocation.iconResId,
            weatherInDegrees = uiState.weatherDetailsOfChosenLocation.temperatureRoundedToInt,
            weatherCondition = uiState.weatherDetailsOfChosenLocation.weatherCondition,
            aiGeneratedWeatherSummaryText = uiState.weatherSummaryText,
            isPreviouslySavedLocation = uiState.isPreviouslySavedLocation,
            isWeatherSummaryLoading = uiState.isWeatherSummaryTextLoading,
            singleWeatherDetails = uiState.additionalWeatherInfoItems,
            hourlyForecasts = uiState.hourlyForecasts,
            precipitationProbabilities = uiState.precipitationProbabilities,
            onBackButtonClick = onBackButtonClick,
            onSaveButtonClick = onSaveButtonClick,
        )
    }
}

@Composable
fun WeatherDetailScreen(
    nameOfLocation: String,
    weatherCondition: String,
    aiGeneratedWeatherSummaryText: String?,
    @DrawableRes weatherConditionImage: Int,
    @DrawableRes weatherConditionIconId: Int,
    weatherInDegrees: Int,
    isWeatherSummaryLoading: Boolean,
    isPreviouslySavedLocation: Boolean,
    onBackButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    singleWeatherDetails: List<SingleWeatherDetail>,
    hourlyForecasts: List<HourlyForecast>,
    precipitationProbabilities: List<PrecipitationProbability>,
    snackbarHostState: SnackbarHostState,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Box {
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
                        .height(350.dp),
                    headerImageResId = weatherConditionImage,
                    weatherConditionIconId = weatherConditionIconId,
                    onBackButtonClick = onBackButtonClick,
                    shouldDisplaySaveButton = !isPreviouslySavedLocation,
                    onSaveButtonClick = onSaveButtonClick,
                    nameOfLocation = nameOfLocation,
                    currentWeatherInDegrees = weatherInDegrees,
                    weatherCondition = weatherCondition
                )
            }

            if (aiGeneratedWeatherSummaryText != null || isWeatherSummaryLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    WeatherSummaryTextCard(
                        summaryText = aiGeneratedWeatherSummaryText ?: "",
                        isWeatherSummaryLoading = isWeatherSummaryLoading
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HourlyForecastCard(hourlyForecasts = hourlyForecasts)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                PrecipitationProbabilitiesCard(precipitationProbabilities = precipitationProbabilities)
            }
            items(singleWeatherDetails) {
                SingleWeatherDetailCard(
                    name = it.name,
                    value = it.value,
                    iconResId = it.iconResId
                )
            }
            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }

        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            hostState = snackbarHostState
        )
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    @DrawableRes headerImageResId: Int,
    @DrawableRes weatherConditionIconId: Int,
    onBackButtonClick: () -> Unit,
    shouldDisplaySaveButton: Boolean,
    onSaveButtonClick: () -> Unit,
    nameOfLocation: String,
    currentWeatherInDegrees: Int,
    weatherCondition: String,
) {
    Box(modifier = modifier) {
        val iconButtonContainerColor = remember {
            Color.Black.copy(0.4f)
        }
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
        if (shouldDisplaySaveButton) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = iconButtonContainerColor
                ),
                onClick = onSaveButtonClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = nameOfLocation,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$currentWeatherInDegrees°",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp)
            )
            Row(
                modifier = Modifier.offset(x = (-8).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Explicitly set tint to Color.Unspecified to ensure that no tint is applied to the vector
                // resource. See documentation of the Icon composable for more information.
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(id = weatherConditionIconId),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Text(text = weatherCondition)
            }
        }
    }
}

@Composable
private fun WeatherSummaryTextCard(
    modifier: Modifier = Modifier,
    isWeatherSummaryLoading: Boolean,
    summaryText: String,
) {
    Card(modifier = modifier) {
        val context = LocalContext.current
        val imageLoader = remember {
            ImageLoader.Builder(context = context)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        }
        val imageRequest = remember {
            ImageRequest.Builder(context = context)
                .data(R.drawable.bard_sparkle_thinking_anim)
                .size(Size.ORIGINAL)
                .build()
        }
        val asyncImagePainter = rememberAsyncImagePainter(
            model = imageRequest,
            imageLoader = imageLoader
        )

        Row(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isWeatherSummaryLoading) {
                Image(
                    modifier = Modifier.size(16.dp),
                    painter = asyncImagePainter,
                    contentDescription = null
                )
            } else {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_bard_logo),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleMedium
            )
        }
        TypingAnimatedText(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = summaryText
        )
    }
}