package com.mujapps.golfgarage.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mujapps.golfgarage.ui.theme.ExtendedTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    deltaText: String,
    deltaIsPositive: Boolean,
    index: Int,
    modifier: Modifier = Modifier
) {
    val animDelay = index * 90L

    val cardAlpha = remember { Animatable(0f) }
    val cardOffsetY = remember { Animatable(10f) }
    val stripeScaleY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(animDelay)
        launch {
            cardAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
        }
        launch {
            cardOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
        }
        launch {
            stripeScaleY.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                alpha = cardAlpha.value
                translationY = cardOffsetY.value * density
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
            // Left Accent Bar (5dp width, full height)
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .graphicsLayer {
                        scaleY = stripeScaleY.value
                        transformOrigin = TransformOrigin(0.5f, 1f) // origin bottom
                    }
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )

            // Content Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 14.dp, end = 16.dp, bottom = 14.dp)
            ) {
                // Average Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Value and Unit
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Delta representation
                val deltaColor = if (deltaIsPositive) {
                    ExtendedTheme.colors.success
                } else {
                    ExtendedTheme.colors.warning
                }

                Text(
                    text = deltaText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = deltaColor
                    )
                )
            }
        }
    }
}
