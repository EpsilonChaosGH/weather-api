package com.example.weather_api.core_data.models

import java.sql.Date

data class ForecastEntity(
    val city: String,
    val temperature: Double,
    val icon: String,
    val data: Date,
)