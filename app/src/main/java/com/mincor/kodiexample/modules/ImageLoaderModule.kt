package com.mincor.kodiexample.modules

import coil.ImageLoader
import com.mincor.kodi.core.*
import okhttp3.OkHttpClient

val imageModule = kodiModule {
    bind<ImageLoader>() with provider {
        ImageLoader(instance()) {
            availableMemoryPercentage(0.5)
            bitmapPoolPercentage(0.5)
            crossfade(true)
            okHttpClient(instance<OkHttpClient>())
        }
    }
}