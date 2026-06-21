package com.mujapps.golfgarage.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import com.mujapps.presentation.features.player_details.PlayerDetailsViewModel
import com.mujapps.golfgarage.ui.components.MetricCard
import com.mujapps.golfgarage.ui.components.ScatterPlot
import com.mujapps.golfgarage.ui.components.ShotCard
import com.mujapps.golfgarage.navigation.NavRoutes
import com.mujapps.golfgarage.ui.theme.ExtendedTheme
import androidx.compose.ui.text.font.FontWeight
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Person
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import kotlin.math.roundToInt

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import io.github.aakira.napier.Napier

@Composable
fun GolfPlayerDetailsView(
    mPlayerId: String,
    backStack: NavBackStack<NavKey>,
    onPlayerLoaded: (String) -> Unit,
    mViewModel: PlayerDetailsViewModel = koinViewModel(parameters = { parametersOf(mPlayerId) }),
    listState: LazyListState = rememberLazyListState(),
    onScrollStateChanged: (Float) -> Unit = {},
    onProfileImageLoaded: (String?) -> Unit = {}
) {

    val mDetailsUiState = mViewModel.mDetailsUiState.collectAsStateWithLifecycle()
    val playerDetail = mDetailsUiState.value.mPlayerDetail
    val player = playerDetail?.mPlayer

    LaunchedEffect(Unit) {
        mViewModel.getUserDetails()
    }

    LaunchedEffect(player) {
        if (player != null) {
            onPlayerLoaded(player.mName)
            onProfileImageLoaded(player.mProfPicUrl)
        }
    }

    // Scroll metrics calculations to update collapsing toolbar progress
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        if (listState.firstVisibleItemIndex > 0) {
            onScrollStateChanged(1f) // Fully collapsed
        } else {
            // First item (Profile card) size calculation fallback
            val layoutInfo = listState.layoutInfo
            val firstItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == 0 }
            if (firstItem != null) {
                val size = firstItem.size.toFloat()
                if (size > 0f) {
                    val progress = (listState.firstVisibleItemScrollOffset.toFloat() / size).coerceIn(0f, 1f)
                    Napier.d("Collapsing toolbar progress: $progress", tag = "GolfPlayerDetailsView")
                    onScrollStateChanged(progress)
                }
            } else {
                onScrollStateChanged(0f)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (player != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = player.mProfPicUrl,
                            contentDescription = player.mName,
                            contentScale = ContentScale.Crop,
                            placeholder = rememberVectorPainter(EvaIcons.Outline.Person),
                            error = rememberVectorPainter(EvaIcons.Outline.Person),
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = player.mName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = player.mPreferenceClub,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            val playerShots = playerDetail.mShots
            val allShots = mDetailsUiState.value.mAllShots
            val hasShots = playerShots.isNotEmpty()
            val syncErrorMessage = mDetailsUiState.value.mErrorMessage

            if (!hasShots && syncErrorMessage != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ExtendedTheme.colors.warning.copy(alpha = 0.12f)
                        )
                    ) {
                        Text(
                            text = "Shot data unavailable offline. Connect to the internet to load this player's shots.",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ExtendedTheme.colors.warning
                        )
                    }
                }
            }

            // Calculate player averages
            val playerAvgBallSpeed = if (hasShots) playerShots.map { it.mBallSpeed }.average() else 0.0
            val playerAvgLaunchAngle = if (hasShots) playerShots.map { it.mLaunchAngle }.average() else 0.0
            val playerAvgCarryDistance = if (hasShots) playerShots.map { it.mCarryDistance }.average() else 0.0
            val playerAvgSpinRate = if (hasShots) playerShots.map { it.mSpinRate }.average() else 0.0

            // Calculate database overall averages
            val allAvgBallSpeed = if (allShots.isNotEmpty()) allShots.map { it.mBallSpeed }.average() else 0.0
            val allAvgLaunchAngle = if (allShots.isNotEmpty()) allShots.map { it.mLaunchAngle }.average() else 0.0
            val allAvgCarryDistance = if (allShots.isNotEmpty()) allShots.map { it.mCarryDistance }.average() else 0.0
            val allAvgSpinRate = if (allShots.isNotEmpty()) allShots.map { it.mSpinRate }.average() else 0.0

            // Deltas
            val deltaBallSpeed = playerAvgBallSpeed - allAvgBallSpeed
            val deltaLaunchAngle = playerAvgLaunchAngle - allAvgLaunchAngle
            val deltaCarryDistance = playerAvgCarryDistance - allAvgCarryDistance
            val deltaSpinRate = playerAvgSpinRate - allAvgSpinRate

            // Format values
            val formattedBallSpeed = if (hasShots) playerAvgBallSpeed.roundToInt().toString() else "--"
            val formattedLaunchAngle = if (hasShots) formatOneDecimal(playerAvgLaunchAngle) else "--"
            val formattedCarryDistance = if (hasShots) playerAvgCarryDistance.roundToInt().toString() else "--"
            val formattedSpinRate = if (hasShots) formatIntegerWithSeparator(playerAvgSpinRate) else "--"

            // Delta formats
            val deltaBallSpeedText = if (hasShots) {
                val deltaInt = deltaBallSpeed.roundToInt()
                val prefix = if (deltaInt >= 0) "+" else ""
                "$prefix$deltaInt km/h vs avg"
            } else {
                "-- vs avg"
            }
            val deltaBallSpeedIsPositive = deltaBallSpeed >= 0.0

            val deltaLaunchAngleText = if (hasShots) {
                val deltaStr = formatOneDecimal(deltaLaunchAngle)
                val prefix = if (deltaLaunchAngle >= 0.0) "+" else ""
                "$prefix$deltaStr° vs avg"
            } else {
                "-- vs avg"
            }
            val deltaLaunchAngleIsPositive = deltaLaunchAngle >= 0.0

            val deltaCarryDistanceText = if (hasShots) {
                val deltaInt = deltaCarryDistance.roundToInt()
                val prefix = if (deltaInt >= 0) "+" else ""
                "$prefix$deltaInt m vs avg"
            } else {
                "-- vs avg"
            }
            val deltaCarryDistanceIsPositive = deltaCarryDistance >= 0.0

            val deltaSpinRateText = if (hasShots) {
                val deltaInt = deltaSpinRate.roundToInt()
                val prefix = if (deltaInt >= 0) "+" else ""
                "$prefix$deltaInt RPM vs avg"
            } else {
                "-- vs avg"
            }
            val deltaSpinRateIsPositive = deltaSpinRate >= 0.0

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // AVERAGES Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "AVERAGES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // 2x2 Grid of Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Average Ball Speed",
                        value = formattedBallSpeed,
                        unit = "km/h",
                        deltaText = deltaBallSpeedText,
                        deltaIsPositive = deltaBallSpeedIsPositive,
                        index = 0,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Average Launch Angle",
                        value = formattedLaunchAngle,
                        unit = "°",
                        deltaText = deltaLaunchAngleText,
                        deltaIsPositive = deltaLaunchAngleIsPositive,
                        index = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Average Carry Distance",
                        value = formattedCarryDistance,
                        unit = "m",
                        deltaText = deltaCarryDistanceText,
                        deltaIsPositive = deltaCarryDistanceIsPositive,
                        index = 2,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Average Spin Rate",
                        value = formattedSpinRate,
                        unit = "RPM",
                        deltaText = deltaSpinRateText,
                        deltaIsPositive = deltaSpinRateIsPositive,
                        index = 3,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // LAUNCH ANGLE vs CARRY DISTANCE Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "LAUNCH ANGLE vs CARRY DISTANCE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                ScatterPlot(
                    shots = playerShots,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (playerShots.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                backStack.add(NavRoutes.Shots(mPlayerId))
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "View Shots History",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        } else if (mDetailsUiState.value.mIsLoading) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
        } else if (mDetailsUiState.value.mErrorMessage != null) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mDetailsUiState.value.mErrorMessage ?: "Failed to load player details",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
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