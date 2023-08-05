package com.example.arcus.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A [Card] composable, that is used to display a single weather detail such as wind speed,
 * humidity, precipitation etc..
 *
 * @param iconResId The drawable resource ID for the icon.
 * @param name The name of the weather detail.
 * @param value The value of the weather detail.
 * @param modifier The modifier for the card.
 */
@Composable
fun SingleWeatherDetailCard(
    @DrawableRes iconResId: Int,
    name: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(id = iconResId),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    minLines = 1
                )
            }
        }
    }
}