package com.akkeritech.android.travellogue.database

import androidx.room.*
import com.akkeritech.android.travellogue.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert
    fun addPlace(place: Place): Long

    @Delete
    fun delete(place: Place)

    @Update
    fun update(place: Place)

    @Query("SELECT * from place_table WHERE placeId = :key")
    fun get(key: Long): Place

    @Query("SELECT * FROM place_table ORDER BY placeId DESC")
    fun getAllPlaces(): Flow<List<Place>>

    @Query("DELETE FROM place_table")
    fun clear()
}