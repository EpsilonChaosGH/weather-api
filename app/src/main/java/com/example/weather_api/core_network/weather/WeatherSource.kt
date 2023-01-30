package com.example.weather_api.core_network.weather

import com.example.weather_api.core_data.models.AirPollutionEntity
import com.example.weather_api.core_data.models.City
import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_data.models.WeatherEntity

interface WeatherSource {

    suspend fun getWeatherByCity(city: City): WeatherEntity
    suspend fun getWeatherForecastByCity(city: City): List<WeatherEntity>

    suspend fun getWeatherByCoordinates(coordinates: Coordinates): WeatherEntity
    suspend fun getWeatherForecastByCoordinates(coordinates: Coordinates): List<WeatherEntity>

    suspend fun getAirPollutionByCoordinates(coordinates: Coordinates): AirPollutionEntity

}