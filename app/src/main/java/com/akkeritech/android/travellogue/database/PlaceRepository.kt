package com.akkeritech.android.travellogue.database

import android.content.Context
import com.akkeritech.android.travellogue.Photo
import com.akkeritech.android.travellogue.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class PlaceRepository constructor(context: Context) {
    private val database : PlaceDatabase = PlaceDatabase.getInstance(context)
    private val placeDao = database.placeDao()
    private val photoDao = database.photoDao()
    private val executor = Executors.newSingleThreadExecutor()

    val allPlaces: Flow<List<Place>> = placeDao.getAllPlaces()

    suspend fun getCurrentPlace(placeId: Long): Place {
        return withContext(Dispatchers.IO) {
            placeDao.get(placeId)
        }
    }

    fun deletePlace(place: Place) {
        executor.execute {
            placeDao.delete(place)
        }
    }

    fun updatePlace(place: Place) {
        executor.execute {
            placeDao.update(place)
        }
    }

    fun getPlacePhotos(placeId: Long): Flow<List<Photo>> {
        return photoDao.getPlacePhotos(placeId)
    }

    suspend fun addPlace(place: Place): Long {
        return withContext(Dispatchers.IO) {
            placeDao.addPlace(place)
        }
    }

    fun addPhoto(photo: Photo) {
        executor.execute {
            photoDao.insert(photo)
        }
    }

    fun deletePlacePhotos(placeId: Long) {
        executor.execute {
            photoDao.deletePlacePhotos(placeId)
        }
    }
}