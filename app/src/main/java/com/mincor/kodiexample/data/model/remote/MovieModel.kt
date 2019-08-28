package com.mincor.kodiexample.data.model.remote

import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.IConvertableTo
import com.mincor.kodiexample.data.model.local.MovieEntity

data class MovieModel(
    val id: Int?,
    val vote_count: Int?,
    val vote_average: Double?,
    val video: Boolean?,
    val title: String?,
    val popularity: Double?,
    val poster_path: String?,
    val original_language: String?,
    val original_title: String?,
    val genre_ids: List<Int>?,
    val backdrop_path: String?,
    val release_date: String?,
    val adult: Boolean?,
    val overview: String?,
    val revenue: Int?,
    val runtime: Int?,
    val status: String?,
    val tagline: String?,
    val budget: Long?
) : IConvertableTo<MovieEntity> {
    override fun convertTo(): MovieEntity {
        return MovieEntity(
            id = id ?: 0,
            voteCount = vote_count ?: 0,
            voteAverage = vote_average ?: 0.0,
            isVideo = video ?: false,
            title = title.orEmpty(),
            popularity = popularity ?: 0.0,
            posterPath = poster_path.orEmpty(),
            originalLanguage = original_language.orEmpty(),
            originalTitle = original_title.orEmpty(),
            genreIds = genre_ids ?: emptyList(),
            backdropPath = backdrop_path.orEmpty(),
            releaseDate = release_date?.let { Consts.MODEL_DATE_FORMATTER.parse(release_date).time } ?: 0L,
            adult = adult ?: false,
            overview = overview.orEmpty(),
            revenue = revenue ?: 0,
            runtime = runtime ?: 0,
            status = status.orEmpty(),
            tagline = tagline.orEmpty(),
            budget = budget ?: 0L
        )
    }
}