package com.example.weather_api.app.model


data class WeatherState(
    val city: String,
    val country: String,
    val temperature: String,
    val weatherType: WeatherType,
    val description: String,
    val feelsLike: String,
    val humidity: String,
    val pressure: String,
    val windSpeed: String,
    val data: String,
    val isFavorites: Boolean
)