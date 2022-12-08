package com.example.weather_api.app.model.main

import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.app.model.main.entities.WeatherEntity
import com.example.weather_api.source.weather.entities.GetWeatherForecastResponseEntity
import com.example.weather_api.source.weather.entities.GetWeatherResponseEntity

interface WeatherSource {

    suspend fun getWeatherByCity(city: City): WeatherEntity

    suspend fun getWeatherByCoordinates(coordinates: Coordinates): WeatherEntity

    suspend fun getWeatherForecastByCoordinates(coordinates: Coordinates): List<WeatherEntity>

    suspend fun getWeatherForecastByCity(city: City): List<WeatherEntity>

}