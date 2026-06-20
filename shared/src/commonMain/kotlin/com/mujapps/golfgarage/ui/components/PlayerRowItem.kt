package com.mujapps.golfgarage.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mujapps.domain.models.GolfPlayer

@Composable
fun PlayerRowItem(mPlayer: GolfPlayer) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = mPlayer.mName,
            style = TextStyle(
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Blue
            ),
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
    }
}