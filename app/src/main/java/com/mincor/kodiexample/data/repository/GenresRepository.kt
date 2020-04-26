package com.mincor.kodiexample.data.repository

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.mapListTo
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.data.source.local.IGenresCacheDataSource
import com.mincor.kodiexample.data.source.local.IGenresLocalDataSource
import com.mincor.kodiexample.data.source.remote.IGenresRemoteDataSource

class GenresRepository(
        private val remoteDataSource: IGenresRemoteDataSource,
        private val localDataSource: IGenresLocalDataSource,
        private val memoryDataSource: IGenresCacheDataSource
) : IGenresRepository {
    override suspend fun getLocalGenreList(): SResult<List<GenreEntity>> =
        memoryDataSource.getGenresFromCache().takeIf { it.data.isNotEmpty() }
            ?: localDataSource.getGenresList().also {
                memoryDataSource.putGenresInCache(it.data)
            }

    override suspend fun saveGenres(genresList: List<GenreEntity>) =
        localDataSource.insertGenres(genresList)
            .also { memoryDataSource.putGenresInCache(genresList) }

    override suspend fun getRemoteGenresList(): SResult<List<GenreEntity>> {
        val result = remoteDataSource
                .getRemoteGenresList()
                .mapListTo()

        if(result is SResult.Success<List<GenreEntity>>) {
            remoteDataSource.getGenresImages(result.data)
        }
        return result
    }

}

interface IGenresRepository {
    suspend fun getLocalGenreList(): SResult<List<GenreEntity>>
    suspend fun saveGenres(genresList: List<GenreEntity>)
    suspend fun getRemoteGenresList(): SResult<List<GenreEntity>>
}