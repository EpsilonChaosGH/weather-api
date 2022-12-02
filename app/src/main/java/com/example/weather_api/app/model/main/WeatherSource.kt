package com.example.weather_api.app.model.main

import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.source.weather.entities.GetWeatherResponseEntity

interface WeatherSource {

    suspend fun getWeatherByCity(city: City): GetWeatherResponseEntity

    suspend fun getWeatherByCoordinates(coordinates: Coordinates): GetWeatherResponseEntity

}