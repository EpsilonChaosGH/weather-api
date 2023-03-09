package com.example.weather_api.core_data

import com.example.weather_api.core_data.models.*
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun listenMainWeather(): Flow<MainWeatherEntity?>
    suspend fun listenFavoriteLocations(): Flow<List<MainWeatherEntity?>>

    suspend fun getMainWeatherByCity(city: String)
    suspend fun getMainWeatherByCoordinates(coordinates: Coordinates)
    suspend fun refreshCurrentMainWeather()

    suspend fun addToFavoritesByCity(city: String)
    suspend fun deleteFromFavoritesByCity(city: String)
    suspend fun getFavoriteWeatherByCoordinates(coordinates: Coordinates): WeatherEntity
    suspend fun refreshFavorites()

    suspend fun fromFavoritesMainWeatherToCurrent(city: String)
}