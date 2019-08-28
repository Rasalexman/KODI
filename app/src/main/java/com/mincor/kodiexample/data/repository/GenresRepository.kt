package com.mincor.kodiexample.data.repository

import com.mincor.kodiexample.data.source.local.IGenresCacheDataSource
import com.mincor.kodiexample.data.source.local.IGenresLocalDataSource
import com.mincor.kodiexample.data.source.remote.IGenresRemoteDataSource
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.model.local.GenreEntity

class GenresRepository(
        private val remoteDataSource: IGenresRemoteDataSource,
        private val localDataSource: IGenresLocalDataSource,
        private val memoryDataSource: IGenresCacheDataSource
) {
    suspend fun getLocalGenreList(): SResult<List<GenreEntity>> =
        memoryDataSource.getGenresFromCache().takeIf { it.data.isNotEmpty() }
            ?: localDataSource.getGenresList()

    suspend fun saveGenres(genresList: List<GenreEntity>) =
        localDataSource.insertGenres(genresList)
            .also { memoryDataSource.putGenresInCache(genresList) }

    suspend fun getRemoteGenresList(): SResult<List<GenreEntity>> =
        remoteDataSource.getRemoteGenresList().mapListTo()
}