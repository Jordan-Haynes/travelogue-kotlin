package com.akkeritech.android.travellogue

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_table")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val photoId: Long = 0L,

    @ColumnInfo(name = "placeId")
    var placeId: Long = 0L,

    @ColumnInfo(name = "isReferencePhoto")
    var isReferencePhoto: Boolean = false,

    @ColumnInfo(name = "photoFilename")
    var photoFilename: String = ""
)