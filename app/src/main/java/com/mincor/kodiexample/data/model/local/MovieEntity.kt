package com.mincor.kodiexample.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mincor.kodiexample.BuildConfig
import com.mincor.kodiexample.common.Consts
import com.mincor.kodiexample.data.dto.IConvertableTo
import com.mincor.kodiexample.presentation.movies.MovieUI

@Entity
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val voteCount: Long,
    val voteAverage: Double,
    val isVideo: Boolean,
    val title: String,
    val popularity: Double,
    val posterPath: String,
    val originalLanguage: String,
    val originalTitle: String,
    val genreIds: List<Int>,
    val backdropPath: String,
    val releaseDate: Long,
    val adult: Boolean,
    val overview: String,
    val revenue: Long,
    val runtime: Long,
    val status: String,
    val tagline: String,
    val budget: Long,
    var hasDetails: Boolean = false
) : IConvertableTo<MovieUI> {
    override fun convertTo(): MovieUI {
        return MovieUI(
                id = id,
                voteCount = voteCount,
                voteAverage = voteAverage,
                isVideo = isVideo,
                title = title,
                popularity = popularity,
                posterPath = posterPath,
                originalLanguage = originalLanguage,
                originalTitle = originalTitle,
                genreIds = genreIds,
                backdropPath = backdropPath,
                releaseDate = Consts.UI_DATE_FORMATTER.format(releaseDate),
                adult = adult,
                overview = overview
        )
    }

    fun getBackDropImageUrl(): String {
        val backdropUrl = "${BuildConfig.IMAGES_BACKDROP_URL}$backdropPath"
        return backdropUrl
    }
    fun getImageUrl(): String {
        val posterUrl = "${BuildConfig.IMAGES_URL}$posterPath"
        return posterUrl
    }
}