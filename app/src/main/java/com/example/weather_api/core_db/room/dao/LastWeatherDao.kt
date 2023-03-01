package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.LastWeatherDbEntity
import com.example.weather_api.core_db.room.entitity.WeatherUpdateFavoritesTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface LastWeatherDao {

    @Query("SELECT * FROM last_weather WHERE last_weather_key =:key")
    fun getLastWeatherFlow(key: String): Flow<LastWeatherDbEntity>

    @Query("SELECT * FROM last_weather WHERE last_weather_key =:key")
    fun getLastWeather(key: String): LastWeatherDbEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastWeather(lastWeather: LastWeatherDbEntity)

    @Query("SELECT city FROM last_weather WHERE last_weather_key = :last_weather_key")
    suspend fun findByEmail(last_weather_key: String): String

    @Update(entity = LastWeatherDbEntity::class)
    suspend fun updateFavorites(weather: WeatherUpdateFavoritesTuple)
}