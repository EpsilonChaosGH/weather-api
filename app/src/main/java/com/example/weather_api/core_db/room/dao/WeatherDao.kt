package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE weather.is_current = 1")
    fun getCurrentWeatherFlow(): Flow<WeatherWithForecast?>

    @Query("SELECT weather_city FROM weather WHERE weather.is_current = 1")
    fun getCurrentCity(): String

    @Update(entity = MainWeatherDbEntity::class)
    suspend fun updateFavorites(isFavorites: UpdateFavoritesTuple)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: MainWeatherDbEntity)

    @Query("SELECT EXISTS( SELECT weather_city  FROM weather WHERE weather_city = :city AND is_favorites = :isFavorites)")
    fun checkForFavorites(city: String, isFavorites: Boolean): Boolean

    @Query("SELECT * FROM weather WHERE weather.is_favorites = 1")
    fun getFavoritesFlow(): Flow<List<WeatherWithForecast?>>

    @Query("SELECT * FROM weather WHERE weather.is_favorites = 1")
    fun getFavoritesCity(): List<CityTuple>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastDbEntity>)

    @Update(entity = MainWeatherDbEntity::class)
    suspend fun updateMainWeather(weather: UpdateMainWeatherTuple)

    @Transaction
    suspend fun setCurrentByCity(city: String) {
        val currentCity = getCurrentCity()
        if (city == currentCity) return
        updateCurrent(UpdateCurrentTuple(city = city, isCurrent = true))
        updateCurrent(UpdateCurrentTuple(city = currentCity, isCurrent = false))
    }

    @Update(entity = MainWeatherDbEntity::class)
    suspend fun updateCurrent(isCurrent: UpdateCurrentTuple)


    @Transaction
    suspend fun deleteMainWeatherByCity(city: String) {
        deleteMainWeather(city)
        deleteMainWeather(isFavorites = false, isCurrent = false)
    }

    @Query("DELETE FROM weather WHERE weather.weather_city = :city")
    suspend fun deleteMainWeather(city: String)

    @Query("DELETE FROM weather WHERE is_favorites = :isFavorites AND is_current = :isCurrent")
    suspend fun deleteMainWeather(isFavorites: Boolean, isCurrent: Boolean)
}