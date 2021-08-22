package com.mincor.kodiexample.data.repository

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.*
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.data.source.local.IGenresCacheDataSource
import com.mincor.kodiexample.data.source.local.IGenresLocalDataSource
import com.mincor.kodiexample.data.source.remote.IGenresRemoteDataSource
import com.rasalexman.kodi.annotations.BindSingle

@BindSingle(
    toClass = IGenresRepository::class,
    toModule = Consts.Modules.RepName
)
class GenresRepository(
    private val remoteDataSource: IGenresRemoteDataSource,
    private val localDataSource: IGenresLocalDataSource,
    private val memoryDataSource: IGenresCacheDataSource
) : IGenresRepository {
    override suspend fun getLocalGenreList(): SResult<List<GenreEntity>> =
        memoryDataSource.getGenresFromCache().flatMapIfEmptySuspend {
            localDataSource.getGenresList().applyIfSuccessSuspend { data ->
                memoryDataSource.putGenresInCache(data)
            }
        }

    override suspend fun saveGenres(genresList: List<GenreEntity>) {
        localDataSource.insertGenres(genresList)
        memoryDataSource.putGenresInCache(genresList)
    }


    override suspend fun getRemoteGenresList(): SResult<List<GenreEntity>> {
        return remoteDataSource.getRemoteGenresList().mapListTo().flatMapIfSuccessSuspend {
            remoteDataSource.getGenresImages(it).applyIfSuccessSuspend { allGenres ->
                println("-----> All genre posters was loaded with count = ${allGenres.size}")
            }
        }
    }

}

interface IGenresRepository {
    suspend fun getLocalGenreList(): SResult<List<GenreEntity>>
    suspend fun saveGenres(genresList: List<GenreEntity>)
    suspend fun getRemoteGenresList(): SResult<List<GenreEntity>>
}