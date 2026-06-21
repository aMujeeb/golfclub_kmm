package com.mujapps.golfgarage.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import coil3.compose.AsyncImage
import com.mujapps.domain.models.GolfPlayer
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Person

@Composable
fun PlayerRowItem(mPlayer: GolfPlayer, onSelectedPlayer: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            onSelectedPlayer(mPlayer.mId)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        AsyncImage(
            model = mPlayer.mProfPicUrl,
            contentDescription = mPlayer.mName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray.copy(alpha = 0.6f), CircleShape),
            placeholder = rememberVectorPainter(EvaIcons.Outline.Person),
            error = rememberVectorPainter(EvaIcons.Outline.Person)
        )

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