package com.mincor.kodiexample.data.source.local

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.GenreEntity

interface IGenresLocalDataSource {
    suspend fun getGenresList(): SResult<List<GenreEntity>>
    suspend fun insertGenres(data: List<GenreEntity>)
}