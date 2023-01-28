package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.emptyResult
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.rasalexman.kodi.annotations.BindSingle
import com.rasalexman.kodi.annotations.IgnoreInstance

@BindSingle(
        toClass = IGenresCacheDataSource::class,
        toModule = Consts.Modules.LDSName
)
class GenresCacheDataSource (
       @IgnoreInstance private val memoryGenresList: MutableList<GenreEntity> = mutableListOf()
) : IGenresCacheDataSource {

    override suspend fun putGenresInCache(genresList: List<GenreEntity>) {
        if (memoryGenresList.isEmpty()) {
            memoryGenresList.addAll(genresList)
        }
    }

    override suspend fun getGenresFromCache(): SResult<List<GenreEntity>> {
        val isEmptyCache = memoryGenresList.isEmpty()
        println("-----> getGenresFromCache isEmptyCache = $isEmptyCache")
        return if(isEmptyCache) {
            emptyResult()
        } else {
            successResult(memoryGenresList)
        }
    }
}

interface IGenresCacheDataSource : IBaseDataSource {
    suspend fun putGenresInCache(genresList: List<GenreEntity>)
    suspend fun getGenresFromCache(): SResult<List<GenreEntity>>
}

interface IBaseDataSource