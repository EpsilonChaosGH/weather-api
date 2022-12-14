package com.example.weather_api.app.model.main.entities

import java.sql.Date

data class WeatherEntity(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val mainWeather: String,
    val description: String,
    val feelsLike: Double,
    val humidity: Long,
    val pressure: Long,
    val windSpeed: Double,
    val data: Date,
)