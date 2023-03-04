package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Transaction
    @Query("SELECT * FROM weather WHERE weather.is_current =:isCurrent")
    fun getCurrentWeatherFlow(isCurrent: Boolean): Flow<WeatherWithForecast?>

    @Query("UPDATE weather SET is_current = :new_current WHERE is_current IN (:current)")
    fun updateAllCurrent(current: Boolean, new_current: Boolean)

    @Query("SELECT weather_city FROM weather WHERE weather.is_current =:isCurrent")
    fun getCurrentCity(isCurrent: Boolean): String

    @Update(entity = MainWeatherDbEntity::class)
    suspend fun updateCurrent(isCurrent: UpdateCurrentTuple)

    @Update(entity = MainWeatherDbEntity::class)
    suspend fun updateFavorites(isFavorites: UpdateFavoritesTuple)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: MainWeatherDbEntity)

    @Query("SELECT EXISTS( SELECT weather_city  FROM weather WHERE weather_city = :city)")
    fun checkForFavorites(city: String): Boolean

    @Query("SELECT * FROM weather WHERE weather.is_favorites =:isFavorites")
    fun getFavoritesFlow(isFavorites: Boolean): Flow<List<WeatherWithForecast?>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastDbEntity>)
}