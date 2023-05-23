package com.example.justweather.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/**
 * A composable that displays the provided weather information in a compact manner. The information
 * is mainly displayed in a row-wise manner, within the column.
 *
 * @param nameOfLocation The name of the location.
 * @param shortDescription A short description of the weather.
 * @param shortDescriptionIcon The icon for the short description.
 * @param weatherInDegrees The temperature in degrees (without the superscript).
 * @param onClick A callback that is called when this card is clicked.
 * @param modifier The modifier for the card.
 */
@ExperimentalMaterial3Api
@Composable
fun CompactWeatherCard(
    nameOfLocation: String,
    shortDescription: String,
    @DrawableRes shortDescriptionIcon: Int,
    weatherInDegrees: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val superscriptSpan = MaterialTheme.typography.displaySmall.toSpanStyle()
    val weatherWithDegreesSuperscript = remember(weatherInDegrees) {
        buildAnnotatedString {
            append(weatherInDegrees)
            withStyle(
                style = superscriptSpan.copy(baselineShift = BaselineShift.Superscript),
                block = { append("º") }
            )
        }
    }
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = nameOfLocation,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                ShortWeatherDescriptionWithIconRow(
                    shortDescription = shortDescription,
                    iconRes = shortDescriptionIcon
                )
            }
            // offset text composable to accommodate for superscript
            Text(
                modifier = Modifier.offset(y = (-8).dp),
                text = weatherWithDegreesSuperscript,
                style = MaterialTheme.typography.displayMedium
            )
        }
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
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null
        )
        Text(
            text = shortDescription,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Normal
        )
    }
}
