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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.mujapps.presentation.features.player_shot_details.PlayerShotDetailsViewModel
import com.mujapps.golfgarage.ui.components.ShotCard
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GolfPlayerShotsView(
    mPlayerId: String,
    backStack: NavBackStack<NavKey>,
    onPlayerLoaded: (String) -> Unit,
    mViewModel: PlayerShotDetailsViewModel = koinViewModel(parameters = { parametersOf(mPlayerId) })
) {
    val mShotsUiState = mViewModel.mShotsUiState.collectAsStateWithLifecycle()
    val uiState = mShotsUiState.value

    LaunchedEffect(Unit) {
        onPlayerLoaded("Shots")
    }

    val selectedClub by mViewModel.selectedClubName.collectAsStateWithLifecycle()

    var clubsExpanded by remember { mutableStateOf(false) }

    val clubOptions = listOf(
        "All clubs",
        "Putter",
        "Pitching Wedge",
        "9 Iron",
        "8 Iron",
        "7-Iron",
        "Lob Wedge",
        "5-Iron",
        "4-Iron"
    )

    val borderCol = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = borderCol,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Club Dropdown Box
            Box {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(999.dp))
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(999.dp))
                        .clickable { clubsExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedClub,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "▾",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = clubsExpanded,
                    onDismissRequest = { clubsExpanded = false }
                ) {
                    clubOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                mViewModel.onClubSelected(option)
                                clubsExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Content
        if (uiState.mIsLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else if (uiState.mErrorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.mErrorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(
                    items = uiState.mFilteredShots,
                    key = { it.shot.mId }
                ) { shotWithNumber ->
                    ShotCard(
                        shot = shotWithNumber.shot,
                        shotNumber = shotWithNumber.number
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Padding to offset system bars or float buttons
                }
            }
        }
    }
}
