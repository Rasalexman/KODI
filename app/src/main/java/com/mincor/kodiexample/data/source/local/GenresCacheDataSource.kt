package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.dto.successResult
import com.mincor.kodiexample.data.model.local.GenreEntity

class GenresCacheDataSource : IGenresCacheDataSource {

    private val memoryGenresList = mutableListOf<GenreEntity>()

    override suspend fun putGenresInCache(genresList: List<GenreEntity>) {
        if(memoryGenresList.isEmpty()) memoryGenresList.addAll(genresList)
    }

    override suspend fun getGenresFromCache(): SResult.Success<List<GenreEntity>> {
        return successResult(memoryGenresList)
    }
}