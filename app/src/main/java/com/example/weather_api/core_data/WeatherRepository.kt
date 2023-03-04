package com.example.weather_api.core_data

import com.example.weather_api.core_data.models.*
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun listenCurrentWeatherState(): Flow<MainWeatherEntity?>
    suspend fun listenCurrentFavoritesLocations(): Flow<List<MainWeatherEntity?>>

    suspend fun getWeatherByCity(city: String)
    suspend fun getWeatherByCoordinates(coordinates: Coordinates)

    suspend fun addToFavorites()
    suspend fun deleteFromFavorites()
    suspend fun deleteFromFavoritesByCity(city: String)
    suspend fun getFavoriteWeatherByCoordinates(coordinates: Coordinates): WeatherEntity

    suspend fun setCurrentWeather(city: String)
}