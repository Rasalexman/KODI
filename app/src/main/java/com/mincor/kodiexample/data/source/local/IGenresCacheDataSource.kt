package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.GenreEntity

interface IGenresCacheDataSource {
    suspend fun putGenresInCache(genresList: List<GenreEntity>)
    suspend fun getGenresFromCache(): SResult.Success<List<GenreEntity>>
}