package com.example.weather_api.app.model


data class ForecastState(
    val temperature: String,
    val data: String,
    val weatherType: WeatherType,
)