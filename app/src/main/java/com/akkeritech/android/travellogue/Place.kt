package com.akkeritech.android.travellogue

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place_table")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val placeId: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0,

    @ColumnInfo(name = "notes")
    var notes: String = "",

    @ColumnInfo(name = "rating")
    var rating: Float = 0F,

    @ColumnInfo(name = "referencePhoto")
    var referencePhoto: String = ""
)