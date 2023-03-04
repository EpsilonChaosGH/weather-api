package com.example.weather_api.core_db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_api.core_db.room.dao.*
import com.example.weather_api.core_db.room.entitity.ForecastDbEntity
import com.example.weather_api.core_db.room.entitity.MainWeatherDbEntity

@Database(
    entities = [
        MainWeatherDbEntity::class,
        ForecastDbEntity::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
}