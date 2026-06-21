package com.mujapps.golfgarage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mujapps.domain.models.GolfShot
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

@Composable
fun ShotCard(
    shot: GolfShot,
    shotNumber: Int,
    modifier: Modifier = Modifier
) {
    val clubName = shot.mClubName
    val formattedTime = formatTime(shot.mTimestamp)

    // Format metrics (no numeric conversion, just metric notation)
    val formattedSpeed = "${shot.mBallSpeed.roundToInt()} km/h"
    val formattedLaunch = "${formatOneDecimal(shot.mLaunchAngle)}°"
    val formattedCarry = "${shot.mCarryDistance.roundToInt()} m"
    val formattedSpin = "${formatIntegerWithSeparator(shot.mSpinRate)} RPM"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Club Name
            Text(
                text = clubName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stat Grid (4-column row layout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "SPEED", value = formattedSpeed, modifier = Modifier.weight(1f))
                StatItem(label = "LAUNCH", value = formattedLaunch, modifier = Modifier.weight(1f))
                StatItem(label = "CARRY", value = formattedCarry, modifier = Modifier.weight(1f))
                StatItem(label = "SPIN", value = formattedSpin, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Row: SHOT #n + Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SHOT #$shotNumber",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatTime(timestamp: kotlinx.datetime.Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val formattedMinute = if (minute < 10) "0$minute" else "$minute"
    return "$displayHour:$formattedMinute $amPm"
}

private fun formatOneDecimal(value: Double): String {
    val isNegative = value < 0
    val absValue = kotlin.math.abs(value)
    val rounded = (absValue * 10).roundToInt()
    val integerPart = rounded / 10
    val fractionalPart = rounded % 10
    val sign = if (isNegative && (integerPart > 0 || fractionalPart > 0)) "-" else ""
    return "$sign$integerPart.$fractionalPart"
}

private fun formatIntegerWithSeparator(value: Double): String {
    val intValue = value.roundToInt()
    val isNegative = intValue < 0
    val absValue = kotlin.math.abs(intValue)
    val str = absValue.toString()
    val sb = StringBuilder()
    var count = 0
    for (i in str.length - 1 downTo 0) {
        if (count > 0 && count % 3 == 0) {
            sb.append(',')
        }
        sb.append(str[i])
        count++
    }
    val reversed = sb.reverse().toString()
    return if (isNegative) "-$reversed" else reversed
}
