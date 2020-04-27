package com.mincor.kodiexample.providers.network

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.util.CoilUtils
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.providers.database.MoviesDatabase
import com.mincor.kodiexample.providers.database.dao.IGenresDao
import com.mincor.kodiexample.providers.database.dao.IMoviesDao
import com.mincor.kodiexample.providers.network.api.IMovieApi
import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.kodi.annotations.BindSingle
import okhttp3.Cache
import okhttp3.OkHttpClient

object ProviderUtils {


    @BindSingle(
            toClass = Cache::class,
            toModule = Consts.Modules.ProvidersName
    )
    fun createCache(context: Context) = CoilUtils.createDefaultCache(context)

    @BindProvider(
            toClass = IMovieApi::class,
            toModule = Consts.Modules.ProvidersName
    )
    fun createMovieApi(client: OkHttpClient): IMovieApi = createWebServiceApi(client)


    @BindSingle(
            toClass = ImageLoader::class,
            toModule = Consts.Modules.ProvidersName
    )
    fun createImageLoader(context: Context, client: OkHttpClient) = ImageLoader(context) {
        availableMemoryPercentage(0.5)
        bitmapPoolPercentage(0.5)
        crossfade(true)
        okHttpClient(client)
    }

    @BindSingle(
            toClass = MoviesDatabase::class,
            toModule = Consts.Modules.ProvidersName
    )
    fun createDatabase(context: Context) = Room.databaseBuilder(
            context,
            MoviesDatabase::class.java, "kodiMoviesDB"
    ).fallbackToDestructiveMigration().build()

    @BindProvider(
            toClass = IGenresDao::class,
            toModule = Consts.Modules.ProvidersName
    )
    fun getGenresDao(database: MoviesDatabase) = database.getGenresDao()

    @BindProvider(
            toClass = IMoviesDao::class,
            toModule = Consts.Modules.ProvidersName
    )
    fun getMoviesDao(database: MoviesDatabase) = database.getMoviesDao()
}