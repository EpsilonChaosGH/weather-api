package com.example.weather_api.core_data

import com.example.weather_api.core_data.models.*
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun listenCurrentWeatherState(): Flow<WeatherEntity>
    suspend fun listenCurrentForecastState(): Flow<List<WeatherEntity>>
    suspend fun listenCurrentAirPollutionState(): Flow<AirPollutionEntity>
    suspend fun listenCurrentFavoritesLocations(): Flow<List<Location>>

    suspend fun getWeatherByCity(city: City)
    suspend fun getForecastByCity(city: City)
    suspend fun getAirPollutionByCity(city: City)

    suspend fun getWeatherByCoordinates(coordinates: Coordinates)
    suspend fun getForecastByCoordinates(coordinates: Coordinates)
    suspend fun getAirPollutionByCoordinate(coordinates: Coordinates)

    suspend fun addToFavorites()
    suspend fun removeFromFavorites()
    suspend fun getFavoriteWeatherByCoordinates(coordinates: Coordinates): WeatherEntity

}