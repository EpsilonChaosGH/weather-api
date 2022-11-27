package com.example.weather_api.app.model.main

import com.example.weather_api.app.model.main.entities.City

interface WeatherSource {

    suspend fun getWeatherByCity(city: City): String

}