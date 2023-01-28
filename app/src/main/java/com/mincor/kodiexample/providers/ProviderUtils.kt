package com.mincor.kodiexample.providers

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.providers.network.api.IMovieApi
import com.mincor.kodiexample.providers.network.createWebServiceApi
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.BindSingle
import okhttp3.OkHttpClient

object ProviderUtils {

    @BindProvider(
        toClass = IMovieApi::class,
        toModule = Consts.Modules.ProvidersName
    )
    fun createMovieApi(client: OkHttpClient): IMovieApi = createWebServiceApi(client)


    @BindSingle(
        toClass = ImageLoader::class,
        toModule = Consts.Modules.ProvidersName
    )
    fun createImageLoader(context: Context) =
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.5)
                    .build()
            }
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .build()
            }.build()
}