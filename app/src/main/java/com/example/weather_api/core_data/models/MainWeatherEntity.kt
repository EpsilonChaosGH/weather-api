package com.example.weather_api.core_data.models

data class MainWeatherEntity(
    val city: String,
    val isCurrent: Boolean,
    val isFavorites: Boolean,
    val weatherEntity: WeatherEntity,
    val airEntity: AirEntity,
    val forecastEntityList: List<ForecastEntity>,
)