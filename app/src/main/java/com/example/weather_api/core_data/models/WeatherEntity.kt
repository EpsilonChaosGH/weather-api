package com.example.weather_api.core_data.models

import java.sql.Date

data class WeatherEntity(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val icon: String,
    val description: String,
    val feelsLike: Double,
    val humidity: Long,
    val pressure: Long,
    val windSpeed: Double,
    val data: Date,
    val location: Location
)