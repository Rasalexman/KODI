package com.mincor.kodiexample.data.source.remote

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.GenreEntity
import com.mincor.kodiexample.data.model.remote.GenreModel

interface IGenresRemoteDataSource {
    suspend fun getRemoteGenresList(): SResult<List<GenreModel>>
    suspend fun getGenresImages(result: GenreEntity): GenreEntity
}