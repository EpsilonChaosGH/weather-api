package com.example.weather_api.core_network.weather

import com.example.weather_api.core_data.ConnectionException
import com.example.weather_api.core_data.BackendException
import com.example.weather_api.core_data.ParseBackendResponseException
import com.example.weather_api.core_data.models.*

interface WeatherSource {

    /**
     * Returns current weather data for any location on Earth including over 200,000 cities.
     * @param city
     * @return [WeatherEntity]
     * @throws ConnectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */
    suspend fun getWeatherByCity(city: String): WeatherEntity

    /**
     * Returns weather forecast for 5 days. It includes weather forecast data with 3-hour step.
     * @param city
     * @return list of [WeatherEntity]
     * @throws ConnectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */
    suspend fun getForecastByCity(city: String): List<ForecastEntity>

    /**
     * Returns current weather data for coordinates.
     * @param coordinates Coordinates to search for locations near.
     * @return [WeatherEntity]
     * @throws ConnectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */
    suspend fun getWeatherByCoordinates(coordinates: Coordinates): WeatherEntity

    /**
     * Returns weather forecast for 5 days. It includes weather forecast data with 3-hour step.
     * @param coordinates Coordinates to search for locations near.
     * @return list of [WeatherEntity]
     * @throws ConnectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */
    suspend fun getForecastByCoordinates(coordinates: Coordinates): List<ForecastEntity>

    /**
     * Returns current air pollution data for any coordinates on the globe.
     * @param coordinates Coordinates to search for locations near.
     * @return [AirEntity]
     * @throws ConnectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */
    suspend fun getAirPollutionByCoordinates(coordinates: Coordinates): AirEntity
}