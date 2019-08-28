package com.mincor.kodiexample.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mincor.kodiexample.data.dto.IConvertableTo
import com.mincor.kodiexample.data.model.ui.genres.GenreUI

@Entity
data class GenreEntity(
    @PrimaryKey
    val id: Int,
    val name: String
) : IConvertableTo<GenreUI> {
    override fun convertTo() = GenreUI(this.id, this.name)
}