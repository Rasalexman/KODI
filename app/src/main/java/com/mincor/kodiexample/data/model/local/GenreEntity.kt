package com.mincor.kodiexample.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mincor.kodiexample.data.dto.IConvertableTo
import com.mincor.kodiexample.presentation.genres.GenreItem
import com.rasalexman.kodi.core.or

@Entity
data class GenreEntity(
        @JvmField
        @PrimaryKey
        val id: Int? = null,
        @JvmField
        val name: String? = null,
        @JvmField
        val images: MutableList<String> = mutableListOf()
) : IConvertableTo<GenreItem> {
    override fun convertTo() = GenreItem(this.id.or { 0 }, this.name.orEmpty(), this.images)
}