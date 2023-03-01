package com.example.weather_api.core_data

import com.example.weather_api.core_data.models.*
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun listenCurrentWeatherState(): Flow<WeatherEntity>
    suspend fun listenCurrentForecastState(): Flow<List<ForecastEntity>>
    suspend fun listenCurrentAirPollutionState(): Flow<AirEntity>
    suspend fun listenCurrentFavoritesLocations(): Flow<List<WeatherEntity>>

    suspend fun getWeatherByCity(city: City)
    suspend fun getForecastByCity(city: City)
    suspend fun getAirPollutionByCity(city: City)

    suspend fun getWeatherByCoordinates(coordinates: Coordinates)
    suspend fun getForecastByCoordinates(coordinates: Coordinates)
    suspend fun getAirPollutionByCoordinate(coordinates: Coordinates)

    suspend fun addToFavorites()
    suspend fun deleteFromFavorites()
    suspend fun deleteFromFavoritesByCity(city: String)
    suspend fun getFavoriteWeatherByCoordinates(coordinates: Coordinates): WeatherEntity

    suspend fun setCurrentWeather(city: String)
}