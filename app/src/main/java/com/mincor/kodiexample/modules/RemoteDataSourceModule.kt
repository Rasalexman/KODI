package com.mincor.kodiexample.modules

import com.mincor.kodi.core.bind
import com.mincor.kodi.core.instance
import com.mincor.kodi.core.kodiModule
import com.mincor.kodi.core.single
import com.mincor.kodiexample.data.source.remote.GenresRemoteDataSource
import com.mincor.kodiexample.data.source.remote.IGenresRemoteDataSource
import com.mincor.kodiexample.data.source.remote.IMoviesRemoteDataSource
import com.mincor.kodiexample.data.source.remote.MoviesRemoteDataSource

val remoteDataSourceModule = kodiModule {
    bind<IGenresRemoteDataSource>() with single { GenresRemoteDataSource(instance()) }
    bind<IMoviesRemoteDataSource>() with single { MoviesRemoteDataSource(instance()) }
}