package com.mincor.kodiexample.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mincor.kodiexample.data.dto.IConvertableTo
import com.mincor.kodiexample.presentation.genres.GenreItem

@Entity
data class GenreEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val images: MutableList<String> = mutableListOf()
) : IConvertableTo<GenreItem> {
    override fun convertTo() = GenreItem(this.id, this.name, this.images)
}