package com.mujapps.golfgarage.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.mujapps.domain.models.GolfShot
import kotlin.math.roundToInt

@Composable
fun ScatterPlot(
    shots: List<GolfShot>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val dotStrokeColor = MaterialTheme.colorScheme.surface
    val dotColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Dimensions & margins
                val leftMargin = 40.dp.toPx()
                val rightMargin = 12.dp.toPx()
                val topMargin = 16.dp.toPx()
                val bottomMargin = 24.dp.toPx()

                val plotWidth = size.width - leftMargin - rightMargin
                val plotHeight = size.height - topMargin - bottomMargin

                // 1. Draw Gridlines and Y Labels (100, 160, 220)
                val yGridValues = listOf(100.0, 160.0, 220.0)
                yGridValues.forEach { yVal ->
                    val yFraction = ((yVal - 70.0) / 200.0).coerceIn(0.0, 1.0)
                    val yPos = topMargin + ((1.0 - yFraction) * plotHeight).toFloat()

                    // Horizontal Gridline
                    drawLine(
                        color = gridColor,
                        start = Offset(leftMargin, yPos),
                        end = Offset(leftMargin + plotWidth, yPos),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Y Label
                    val textLayoutResult = textMeasurer.measure(
                        text = yVal.roundToInt().toString(),
                        style = labelStyle
                    )
                    val labelX = leftMargin - textLayoutResult.size.width - 8.dp.toPx()
                    val labelY = yPos - (textLayoutResult.size.height / 2f)
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(labelX, labelY)
                    )
                }

                // 2. Draw X Labels (15°, 25°, 35°, 42°)
                val xTicks = listOf(15.0, 25.0, 35.0, 42.0)
                xTicks.forEach { xVal ->
                    val xFraction = ((xVal - 10.0) / 35.0).coerceIn(0.0, 1.0)
                    val xPos = leftMargin + (xFraction * plotWidth).toFloat()

                    // Tick Label
                    val textLayoutResult = textMeasurer.measure(
                        text = "${xVal.roundToInt()}°",
                        style = labelStyle
                    )
                    val labelX = xPos - (textLayoutResult.size.width / 2f)
                    val labelY = topMargin + plotHeight + 6.dp.toPx()
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(labelX, labelY)
                    )
                }

                // 3. Draw Axis Lines
                // Vertical axis
                drawLine(
                    color = gridColor,
                    start = Offset(leftMargin, topMargin),
                    end = Offset(leftMargin, topMargin + plotHeight),
                    strokeWidth = 1.dp.toPx()
                )
                // Horizontal axis
                drawLine(
                    color = gridColor,
                    start = Offset(leftMargin, topMargin + plotHeight),
                    end = Offset(leftMargin + plotWidth, topMargin + plotHeight),
                    strokeWidth = 1.dp.toPx()
                )

                // 4. Draw Shot Dots (clamped)
                shots.forEach { shot ->
                    val xFraction = ((shot.mLaunchAngle - 10.0) / (45.0 - 10.0)).coerceIn(0.0, 1.0)
                    val yFraction = ((shot.mCarryDistance - 70.0) / (270.0 - 70.0)).coerceIn(0.0, 1.0)

                    val xPos = leftMargin + (xFraction * plotWidth).toFloat()
                    val yPos = topMargin + ((1.0 - yFraction) * plotHeight).toFloat()

                    // Background border circle
                    drawCircle(
                        color = dotStrokeColor,
                        radius = 8.dp.toPx(),
                        center = Offset(xPos, yPos)
                    )
                    // Foreground fill circle
                    drawCircle(
                        color = dotColor,
                        radius = 6.dp.toPx(),
                        center = Offset(xPos, yPos)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Axis Captions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← launch angle (°)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "carry (m) ↑",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
