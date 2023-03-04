package com.example.weather_api.core_data.models


data class WeatherEntity(
    val city: String,
    val country: String,
    val temperature: Double,
    val icon: String,
    val description: String,
    val feelsLike: Double,
    val humidity: Long,
    val pressure: Long,
    val windSpeed: Double,
    val data: Long,
    val lon: String,
    val lat: String,
)