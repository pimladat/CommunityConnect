package com.example.jetnews.data.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostData(
    @PrimaryKey val title: String,
    @ColumnInfo(name = "imageUrl") val imageURL: String? = null,
    @ColumnInfo(name = "text") val text: String? = null,
    @ColumnInfo(name = "author") var author: String? = null,
    @ColumnInfo(name = "url") val url: String? = null,
    @ColumnInfo(name = "date") val date: Long? = null,
    @ColumnInfo(name = "pubDate") val pubDate: String? = null,
    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean? = false,
)
