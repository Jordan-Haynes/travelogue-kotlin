package com.akkeritech.android.travellogue.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.akkeritech.android.travellogue.Photo
import com.akkeritech.android.travellogue.Place

@Database(entities = [Place::class, Photo::class], version = 2, exportSchema = false)
abstract class PlaceDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao
    abstract fun photoDao(): PhotoDao

    companion object {

        @Volatile
        private var INSTANCE: PlaceDatabase? = null

        fun getInstance(context: Context) : PlaceDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlaceDatabase::class.java,
                        "place_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}