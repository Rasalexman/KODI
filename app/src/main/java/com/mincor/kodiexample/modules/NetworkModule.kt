package com.mincor.kodiexample.modules

import coil.util.CoilUtils
import com.rasalexman.kodi.core.*
import com.mincor.kodiexample.providers.network.api.IMovieApi
import com.mincor.kodiexample.providers.network.createOkHttpClient
import com.mincor.kodiexample.providers.network.createWebServiceApi
import okhttp3.Cache
import okhttp3.OkHttpClient

val networkModule = kodiModule {
    bind<Cache>() with provider { CoilUtils.createDefaultCache(instance()) }
    bind<OkHttpClient>() with single { createOkHttpClient(instance()) }
    bind<IMovieApi>() with single { createWebServiceApi<IMovieApi>(instance()) }
}