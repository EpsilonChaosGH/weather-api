package com.example.weather_api.core_data.models


data class ForecastEntity(
    val city: String,
    val temperature: Double,
    val icon: String,
    val data: Long,
)