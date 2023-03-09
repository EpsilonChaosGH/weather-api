package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE weather.is_current =:isCurrent")
    fun getCurrentWeatherFlow(isCurrent: Boolean): Flow<WeatherWithForecast?>

    @Query("SELECT weather_city FROM weather WHERE weather.is_current =:isCurrent")
    fun getCurrentCity(isCurrent: Boolean): String

    @Update(entity = MainWeatherDbEntity::class)
    suspend fun updateFavorites(isFavorites: UpdateFavoritesTuple)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: MainWeatherDbEntity)

    @Query("SELECT EXISTS( SELECT weather_city  FROM weather WHERE weather_city = :city AND is_favorites = :isFavorites)")
    fun checkForFavorites(city: String, isFavorites: Boolean): Boolean

    @Query("SELECT * FROM weather WHERE weather.is_favorites =:isFavorites")
    fun getFavoritesFlow(isFavorites: Boolean): Flow<List<WeatherWithForecast?>>

    @Query("SELECT * FROM weather WHERE weather.is_favorites =:isFavorites")
    fun getFavoritesCity(isFavorites: Boolean): List<CityTuple>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastDbEntity>)

    @Update(entity = MainWeatherDbEntity::class)
    suspend fun updateMainWeather(weather: UpdateMainWeatherTuple)


    @Transaction
    suspend fun setCurrentByCity(city: String) {
        updateAllCurrent(current = true, new_current = false)
        updateCurrent(UpdateCurrentTuple(city = city, isCurrent = true))
    }
    @Query("UPDATE weather SET is_current = :new_current WHERE is_current IN (:current)")
    fun updateAllCurrent(current: Boolean, new_current: Boolean)
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