package com.example.weather_api.core_db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_api.core_db.room.dao.LocationDao
import com.example.weather_api.core_db.room.entitity.LocationDB

@Database(entities = [LocationDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}