package com.mujapps.golfgarage.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@Composable
fun GolfPlayerDetailsView(mPlayer: String, backStack: NavBackStack<NavKey>) {
    Text("Details View")
}