package com.example.justweather.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * An autofill suggestion composable function.
 * @param title The title of the suggestion.
 * @param subText The subtext of the suggestion.
 * @param modifier The modifier to be applied to the composable.
 * @param leadingIcon The leading icon of the suggestion. By default the composable displays
 * [Icons.Filled.LocationOn].
 */
@Composable
fun AutofillSuggestion(
    title: String,
    subText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit = { DefaultLeadingIcon() }
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon()
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = subText,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

/**
 * The default leading icon used in [AutofillSuggestion].
 */
@Composable
private fun DefaultLeadingIcon() {
    Icon(
        modifier = Modifier
            .size(40.dp)
            .drawBehind {
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    center = center,
                    radius = size.minDimension / 1.7f
                )
            },
        imageVector = Icons.Filled.LocationOn,
        tint = Color.White,
        contentDescription = null
    )
}