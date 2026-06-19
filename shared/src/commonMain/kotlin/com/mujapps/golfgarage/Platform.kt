package com.mujapps.golfgarage

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform