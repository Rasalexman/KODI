package com.mincor.kodiexample.providers

import android.content.Context
import coil.ImageLoader
import coil.util.CoilUtils
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.providers.network.api.IMovieApi
import com.mincor.kodiexample.providers.network.createWebServiceApi
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.BindSingle
import okhttp3.Cache
import okhttp3.OkHttpClient

object ProviderUtils {

    @BindSingle(
        toClass = Cache::class,
        toModule = Consts.Modules.ProvidersName
    )
    fun createCache(context: Context): Cache = CoilUtils.createDefaultCache(context)

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
            .availableMemoryPercentage(0.5)
            .bitmapPoolPercentage(0.5)
            .crossfade(true)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(context))
                    .build()
            }.build()
}