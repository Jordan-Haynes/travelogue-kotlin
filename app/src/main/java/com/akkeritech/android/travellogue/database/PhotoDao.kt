package com.akkeritech.android.travellogue.database

import androidx.room.*
import com.akkeritech.android.travellogue.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert
    fun insert(photo: Photo)

    @Delete
    fun delete(photo: Photo)

    @Update
    fun update(photo: Photo)

    @Query("SELECT * FROM photo_table ORDER BY photoId DESC")
    fun getAllPhotos(): Flow<List<Photo>>

    @Query("SELECT * FROM photo_table WHERE isReferencePhoto = 'true'")
    fun getReferencePhotos(): Flow<List<Photo>>

    @Query("SELECT * FROM photo_table WHERE placeId = :key ORDER BY photoId DESC")
    fun getPlacePhotos(key: Long): Flow<List<Photo>>

    @Query("DELETE FROM photo_table WHERE photoId = :key")
    fun deletePlacePhotos(key: Long)
}