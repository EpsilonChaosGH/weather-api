package com.example.weather_api.core_db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_api.core_db.room.dao.*
import com.example.weather_api.core_db.room.entitity.*

@Database(
    entities = [
        FavoritesDbEntity::class,
        AirDbEntity::class,
        ForecastDbEntity::class,
        LastWeatherDbEntity::class,
        LastAirDbEntity::class,
        LastForecastDbEntity::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

    abstract fun airDao(): AirDao

    abstract fun forecastDao(): ForecastDao

    abstract fun lastWeatherDao(): LastWeatherDao

    abstract fun lastAirDao(): LastAirDao

    abstract fun lastForecastDao(): LastForecastDao
}