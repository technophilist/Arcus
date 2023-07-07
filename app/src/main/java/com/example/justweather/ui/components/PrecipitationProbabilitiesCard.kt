package com.example.justweather.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.justweather.R
import com.example.justweather.domain.models.PrecipitationProbability
import com.example.justweather.domain.models.hourStringInTwelveHourFormat

/**
 * A card composable that displays precipitation probabilities in a "progress bar" styled manner.
 * @param precipitationProbabilities The list of precipitation probabilities.
 * @param modifier The modifier to apply to the card.
 */
@Composable
fun PrecipitationProbabilitiesCard(
    precipitationProbabilities: List<PrecipitationProbability>,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_day_rain), // todo change icon
                contentDescription = null
            )
            Text(
                text = "Chance of Rain",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            for (precipitationProbability in precipitationProbabilities) {
                ProbabilityProgressRow(
                    modifier = Modifier.fillMaxWidth(),
                    precipitationProbability = precipitationProbability
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun ProbabilityProgressRow(
    precipitationProbability: PrecipitationProbability,
    modifier: Modifier = Modifier
) {
    var progressValue by remember { mutableStateOf(0f) }
    val animatedProgressValue by animateFloatAsState(targetValue = progressValue)
    LaunchedEffect(precipitationProbability) {
        // dividing a percentage value by 100 will yield a value that is between 0.0f..1.0f
        progressValue = precipitationProbability.probabilityPercentage / 100f
    }
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = precipitationProbability.hourStringInTwelveHourFormat.padStart(length = 5),
            style = MaterialTheme.typography.labelLarge
        )
        LinearProgressIndicator(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(24.dp)
                .weight(1f),
            progress = animatedProgressValue,
            strokeCap = StrokeCap.Round,
            trackColor = ProgressIndicatorDefaults.linearColor.copy(alpha = 0.5f)
        )
        Text(
            text = "${precipitationProbability.probabilityPercentage}%".padStart(length = 4),
            style = MaterialTheme.typography.labelLarge
        )
    }
}