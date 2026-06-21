package com.mujapps.golfgarage.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ThemeModeToggleButton(
    isDarkTheme: () -> Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onToggle,
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(
            text = if (isDarkTheme()) "Light Mode" else "Dark Mode",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
        )
    }
}
