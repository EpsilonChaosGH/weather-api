package com.example.weather_api.core_db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_api.core_db.room.dao.LastLocationDao
import com.example.weather_api.core_db.room.dao.LocationDao
import com.example.weather_api.core_db.room.entitity.LastLocationDbEntity
import com.example.weather_api.core_db.room.entitity.LocationDbEntity

@Database(
    entities = [
        LocationDbEntity::class,
        LastLocationDbEntity::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun lastLocationDao(): LastLocationDao
}