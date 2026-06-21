package com.mujapps.golfgarage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.mujapps.golfgarage.navigation.GolfersNavRoute
import com.mujapps.golfgarage.ui.theme.GolfGarageTheme
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import okio.FileSystem

@Composable
@Preview
fun App() {
    remember {
        Napier.base(DebugAntilog())
    }

    var darkTheme by remember { mutableStateOf(false) }

    GolfGarageTheme(darkTheme = darkTheme) {
        setSingletonImageLoaderFactory { context ->
            Napier.d("Initializing Coil ImageLoader with memory (25% RAM) and disk (100MB) caches", tag = "App")
            ImageLoader.Builder(context).components {
                add(KtorNetworkFetcherFactory()) //Ensure image download across all platforms(accessing external resources)
            }
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, 0.25) // Use up to 25% of available RAM
                        .build()
                }
                .diskCachePolicy(CachePolicy.ENABLED)
                .diskCache {
                    DiskCache.Builder()
                        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "coil_cache")
                        .maxSizeBytes(1024L * 1024 * 100) // 100 MB disk cache limit
                        .build()
                }
                .build()
        }
        
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GolfersNavRoute(
                isDarkTheme = { darkTheme },
                onToggleDarkTheme = { darkTheme = !darkTheme },
            )
        }
    }
}